package com.knockheart.mywishes.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import kotlin.math.min

/**
 * Generates a crisp 1080x1080 greeting-card bitmap entirely on-device.
 *
 * This class does not access the network and does not mutate the supplied photo.
 * A custom Typeface can be passed in the request (for example, a bundled Kannada
 * font loaded by the UI layer once the font asset is added to the project).
 */
class CardGeneratorEngine(
    private val context: Context,
    private val outputSize: Int = DEFAULT_OUTPUT_SIZE
) {
    init {
        require(outputSize >= 512) { "Output size must be at least 512px." }
    }

    fun render(request: CardGenerationRequest): Bitmap {
        val bitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scale = outputSize / DESIGN_SIZE
        canvas.scale(scale, scale)

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = request.backgroundColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, DESIGN_SIZE, DESIGN_SIZE, backgroundPaint)

        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = request.accentColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(64f, 64f, 1016f, 1016f), 36f, 36f, accentPaint)

        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = request.backgroundColor
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(76f, 76f, 1004f, 1004f), 28f, 28f, innerPaint)

        var cursorY = 132f
        request.occasionTitle?.takeIf(String::isNotBlank)?.let { title ->
            val titlePaint = textPaint(30f, request.accentColor, request.typeface, true)
            cursorY = drawCenteredText(canvas, title, titlePaint, 820, cursorY) + 28f
        }

        request.photo?.let { photo ->
            val diameter = min(300f, if (request.occasionTitle.isNullOrBlank()) 300f else 270f)
            val photoRect = RectF(
                (DESIGN_SIZE - diameter) / 2f,
                cursorY,
                (DESIGN_SIZE + diameter) / 2f,
                cursorY + diameter
            )
            drawSoftBlendedPhoto(canvas, photo, photoRect)
            cursorY = photoRect.bottom + 42f
        } ?: run {
            cursorY += 26f
        }

        val bottomReserved = if (request.signature.isNullOrBlank()) 100f else 150f
        val availableHeight = (DESIGN_SIZE - cursorY - bottomReserved).coerceAtLeast(160f)
        // Fit by measured layout height so long English and Kannada greetings
        // remain inside the card's safe text region without drawing twice.
        var greetingSize = 42f
        var greetingPaint = textPaint(greetingSize, request.textColor, request.typeface, false)
        var greetingLayout = createLayout(request.greetingText, greetingPaint, 820)
        while (greetingLayout.height > availableHeight && greetingSize > 22f) {
            greetingSize -= 2f
            greetingPaint = textPaint(greetingSize, request.textColor, request.typeface, false)
            greetingLayout = createLayout(request.greetingText, greetingPaint, 820)
        }
        canvas.save()
        canvas.translate((DESIGN_SIZE - greetingLayout.width) / 2f, cursorY)
        greetingLayout.draw(canvas)
        canvas.restore()

        request.signature?.takeIf(String::isNotBlank)?.let { signature ->
            val signaturePaint = textPaint(27f, request.accentColor, request.typeface, true)
            val layout = createLayout(signature, signaturePaint, 820)
            val y = DESIGN_SIZE - 118f - layout.height
            canvas.save()
            canvas.translate((DESIGN_SIZE - layout.width) / 2f, y)
            layout.draw(canvas)
            canvas.restore()
        }

        return bitmap
    }

    /**
     * Renders and writes a PNG into the app's private cache directory.
     * Use the configured FileProvider to share the returned file URI.
     */
    @Throws(IOException::class)
    fun exportPng(request: CardGenerationRequest): File {
        val outputDirectory = File(context.cacheDir, "generated-cards").apply {
            if (!exists() && !mkdirs()) {
                throw IOException("Could not create card cache directory.")
            }
        }
        val outputFile = File(outputDirectory, "wish-${UUID.randomUUID()}.png")
        val bitmap = render(request)
        try {
            FileOutputStream(outputFile).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    throw IOException("Bitmap encoder could not write PNG.")
                }
                stream.flush()
            }
        } catch (error: Exception) {
            outputFile.delete()
            throw error
        } finally {
            bitmap.recycle()
        }
        return outputFile
    }

    /**
     * Feather the photo into the card without a circular crop, rectangular frame,
     * outline or hard edge. Keep the central portrait clear and fade only its edges.
     */
    private fun drawSoftBlendedPhoto(canvas: Canvas, source: Bitmap, rect: RectF) {
        val width = rect.width().toInt()
        val height = rect.height().toInt()
        val cropped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            val cropCanvas = Canvas(cropped)
            val sourceSize = min(source.width, source.height)
            val sourceLeft = (source.width - sourceSize) / 2
            val sourceTop = (source.height - sourceSize) / 2
            val sourceRect = android.graphics.Rect(
                sourceLeft, sourceTop, sourceLeft + sourceSize, sourceTop + sourceSize
            )
            cropCanvas.drawBitmap(
                source, sourceRect, android.graphics.Rect(0, 0, width, height),
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )

            val pixels = IntArray(width * height)
            cropped.getPixels(pixels, 0, width, 0, 0, width, height)
            val feather = min(width, height) * 0.22f
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val edgeDistance = min(
                        min(x.toFloat(), (width - 1 - x).toFloat()),
                        min(y.toFloat(), (height - 1 - y).toFloat())
                    )
                    val t = (edgeDistance / feather).coerceIn(0f, 1f)
                    val fade = t * t * (3f - 2f * t)
                    val index = y * width + x
                    val pixel = pixels[index]
                    val originalAlpha = pixel ushr 24
                    val alpha = (originalAlpha * fade).toInt().coerceIn(0, 255)
                    pixels[index] = (pixel and 0x00FFFFFF) or (alpha shl 24)
                }
            }
            cropped.setPixels(pixels, 0, width, 0, 0, width, height)
            canvas.drawBitmap(cropped, null, rect, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        } finally {
            cropped.recycle()
        }
    }

    private fun drawCenteredText(
        canvas: Canvas,
        text: String,
        paint: TextPaint,
        maxWidth: Int,
        top: Float
    ): Float {
        val layout = createLayout(text, paint, maxWidth)
        canvas.save()
        canvas.translate((DESIGN_SIZE - layout.width) / 2f, top)
        layout.draw(canvas)
        canvas.restore()
        return top + layout.height
    }

    private fun createLayout(text: String, paint: TextPaint, width: Int): StaticLayout =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(true)
            .setLineSpacing(8f, 1.08f)
            .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
            .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE)
            .build()

    private fun textPaint(
        size: Float,
        color: Int,
        typeface: Typeface?,
        bold: Boolean
    ) = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        textSize = size
        this.color = color
        this.typeface = typeface ?: Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    companion object {
        const val DEFAULT_OUTPUT_SIZE = 1080
        private const val DESIGN_SIZE = 1080f
    }
}
