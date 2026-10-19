package com.knockheart.mywishes.data.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/** Stores profile images in app-private files; no public-storage permission is needed. */
class ProfileImageStorage(context: Context) {
    private val appContext = context.applicationContext

    @Throws(IOException::class)
    fun copyFromContentUri(uriString: String): String {
        val uri = android.net.Uri.parse(uriString)
        val directory = File(appContext.filesDir, DIRECTORY).apply {
            if (!exists() && !mkdirs()) throw IOException("Could not create profile image directory.")
        }
        val destination = File(directory, "profile-${UUID.randomUUID()}.jpg")
        val bitmap = appContext.contentResolver.openInputStream(uri)?.use { stream -> BitmapFactory.decodeStream(stream) }
            ?: throw IOException("Could not read the selected profile image.")
        try {
            FileOutputStream(destination).use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)) {
                    throw IOException("Could not save the profile image.")
                }
            }
        } catch (error: Exception) {
            destination.delete()
            throw error
        } finally {
            bitmap.recycle()
        }
        return destination.absolutePath
    }

    fun load(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null
        val file = File(path)
        if (!file.isFile || !file.absolutePath.startsWith(appContext.filesDir.absolutePath)) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    companion object {
        private const val DIRECTORY = "profile-images"
        private const val JPEG_QUALITY = 92
    }
}
