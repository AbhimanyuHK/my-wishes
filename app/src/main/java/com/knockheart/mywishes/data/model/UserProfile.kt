package com.knockheart.mywishes.data.model

import java.util.Locale

/**
 * Languages with bundled greeting content in the current release.
 *
 * Additional languages can be added by adding a catalog asset and a value here.
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH(code = "en", displayName = "English", nativeName = "English"),
    KANNADA(code = "kn", displayName = "Kannada", nativeName = "ಕನ್ನಡ");

    companion object {
        val supported: List<AppLanguage> = entries.toList()

        fun fromCode(code: String?): AppLanguage {
            val normalizedCode = code?.trim()?.lowercase(Locale.ROOT)
            return entries.firstOrNull { it.code == normalizedCode } ?: ENGLISH
        }
    }
}

/**
 * User-owned profile data. profileImagePath should be an app-private file path,
 * never a temporary Photo Picker URI. cardSignature is used on generated cards.
 */
data class UserProfile(
    val displayName: String = "",
    val profileImagePath: String? = null,
    val cardSignature: String = ""
)

data class UserPreferences(
    val profile: UserProfile = UserProfile(),
    val preferredLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isDarkTheme: Boolean = false,
    val favoriteGreetingIds: Set<String> = emptySet(),
    val cardPaletteId: String = "sunset"
)
