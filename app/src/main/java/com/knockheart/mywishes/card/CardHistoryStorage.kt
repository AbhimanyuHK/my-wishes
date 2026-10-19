package com.knockheart.mywishes.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

/** Keeps a small, private on-device history of generated greeting cards. */
class CardHistoryStorage(context: Context, private val maxCards: Int = 10) {
    private val directory = File(context.filesDir, DIRECTORY_NAME)

    init {
        require(maxCards > 0) { "maxCards must be positive." }
    }

    @Synchronized
    fun save(bitmap: Bitmap): File {
        if (!directory.exists() && !directory.mkdirs()) {
            throw IllegalStateException("Could not create card history directory.")
        }
        val file = File(directory, "wish-${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { output ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                file.delete()
                throw IllegalStateException("Could not save generated card to history.")
            }
        }
        listRecent().drop(maxCards).forEach { it.delete() }
        return file
    }

    @Synchronized
    fun listRecent(): List<File> {
        if (!directory.isDirectory) return emptyList()
        return directory.listFiles { file ->
            file.isFile && file.extension.equals("png", ignoreCase = true)
        }?.sortedByDescending(File::lastModified).orEmpty()
            .take(maxCards)
    }

    companion object {
        private const val DIRECTORY_NAME = "card-history"
    }
}
