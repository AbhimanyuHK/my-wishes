package com.knockheart.mywishes.card

import android.graphics.Bitmap
import android.graphics.Typeface

/**
 * Inputs for an offline, square greeting-card render.
 *
 * [photo] is expected to be a decoded bitmap supplied by the app after copying
 * the selected photo into app-private storage. The engine never retains it.
 */
data class CardGenerationRequest(
    val greetingText: String,
    val occasionTitle: String? = null,
    val signature: String? = null,
    val photo: Bitmap? = null,
    val backgroundColor: Int = 0xFFFFF7ED.toInt(),
    val accentColor: Int = 0xFF9A3412.toInt(),
    val textColor: Int = 0xFF292524.toInt(),
    val typeface: Typeface? = null
) {
    init {
        require(greetingText.isNotBlank()) { "Greeting text must not be blank." }
    }
}
