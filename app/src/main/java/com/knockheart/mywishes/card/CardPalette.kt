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
    SUNSET("sunset", 0xFF190A3D.toInt(), 0xFFD4AF37.toInt(), 0xFFFFF3D6.toInt()),
    OCEAN("ocean", 0xFFEFF6FF.toInt(), 0xFF1D4ED8.toInt(), 0xFF172554.toInt()),
    FOREST("forest", 0xFFF0FDF4.toInt(), 0xFF166534.toInt(), 0xFF14532D.toInt()),
    ROSE("rose", 0xFFFFF1F2.toInt(), 0xFFBE123C.toInt(), 0xFF4C0519.toInt()),
    NIGHT("night", 0xFF28104F.toInt(), 0xFFF4D36F.toInt(), 0xFFFFF3D6.toInt());

    companion object {
        fun fromId(id: String?): CardPalette =
            entries.firstOrNull { it.id == id } ?: SUNSET
    }
}
