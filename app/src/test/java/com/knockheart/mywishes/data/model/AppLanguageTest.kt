package com.knockheart.mywishes.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun knownLanguageCodeIsResolvedCaseInsensitively() {
        assertEquals(AppLanguage.KANNADA, AppLanguage.fromCode(" KN "))
    }

    @Test
    fun unknownOrMissingLanguageFallsBackToEnglish() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("te"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
    }

    @Test
    fun currentCatalogLanguagesAreExplicitlyListed() {
        assertEquals(
            listOf(AppLanguage.ENGLISH, AppLanguage.KANNADA),
            AppLanguage.supported
        )
    }
}
