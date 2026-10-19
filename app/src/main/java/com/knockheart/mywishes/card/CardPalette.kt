package com.knockheart.mywishes.card

/**
 * Accessible preset palettes for generated greeting cards.
 * IDs are persisted, so labels may be localized independently.
 */
enum class CardPalette(
    val id: String,
    val backgroundColor: Int,
    val accentColor: Int,
    val textColor: Int
) {
    SUNSET("sunset", 0xFFFFF7ED.toInt(), 0xFF9A3412.toInt(), 0xFF292524.toInt()),
    OCEAN("ocean", 0xFFEFF6FF.toInt(), 0xFF1D4ED8.toInt(), 0xFF172554.toInt()),
    FOREST("forest", 0xFFF0FDF4.toInt(), 0xFF166534.toInt(), 0xFF14532D.toInt()),
    ROSE("rose", 0xFFFFF1F2.toInt(), 0xFFBE123C.toInt(), 0xFF4C0519.toInt()),
    NIGHT("night", 0xFF18181B.toInt(), 0xFFFBBF24.toInt(), 0xFFF4F4F5.toInt());

    companion object {
        fun fromId(id: String?): CardPalette =
            entries.firstOrNull { it.id == id } ?: SUNSET
    }
}
