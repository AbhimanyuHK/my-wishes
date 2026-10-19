package com.knockheart.mywishes.card

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CardPaletteTest {
    @Test
    fun allPersistedPaletteIdsResolveToTheirPalette() {
        CardPalette.entries.forEach { palette ->
            assertEquals(palette, CardPalette.fromId(palette.id))
        }
    }

    @Test
    fun unknownOrMissingPaletteFallsBackToSunset() {
        assertEquals(CardPalette.SUNSET, CardPalette.fromId("unknown"))
        assertEquals(CardPalette.SUNSET, CardPalette.fromId(null))
    }

    @Test
    fun everyPaletteDefinesOpaqueColors() {
        CardPalette.entries.forEach { palette ->
            assertNotNull(palette.name)
            listOf(
                palette.backgroundColor,
                palette.accentColor,
                palette.textColor
            ).forEach { color ->
                assertEquals(255, color ushr 24 and 0xFF)
            }
        }
    }
}
