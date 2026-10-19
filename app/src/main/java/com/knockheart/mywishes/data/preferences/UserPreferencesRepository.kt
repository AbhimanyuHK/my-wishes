package com.knockheart.mywishes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.knockheart.mywishes.data.model.AppLanguage
import com.knockheart.mywishes.data.model.UserPreferences
import com.knockheart.mywishes.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.knockheartPreferencesDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "knockheart_user_preferences")

/**
 * Local-only preferences backed by AndroidX DataStore.
 * No network access or external storage is used.
 */
class UserPreferencesRepository(context: Context) {
    private val dataStore = context.applicationContext.knockheartPreferencesDataStore

    val preferences: Flow<UserPreferences> = dataStore.data.map { stored ->
        UserPreferences(
            profile = UserProfile(
                displayName = stored[Keys.DISPLAY_NAME].orEmpty(),
                profileImagePath = stored[Keys.PROFILE_IMAGE_PATH],
                cardSignature = stored[Keys.CARD_SIGNATURE].orEmpty()
            ),
            preferredLanguage = AppLanguage.fromCode(stored[Keys.PREFERRED_LANGUAGE]),
            isDarkTheme = stored[Keys.DARK_THEME] ?: false,
            favoriteGreetingIds = stored[Keys.FAVORITE_GREETING_IDS] ?: emptySet()
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { stored ->
            val name = profile.displayName.trim()
            if (name.isEmpty()) {
                stored.remove(Keys.DISPLAY_NAME)
            } else {
                stored[Keys.DISPLAY_NAME] = name
            }

            val imagePath = profile.profileImagePath?.takeIf(String::isNotBlank)
            if (imagePath == null) {
                stored.remove(Keys.PROFILE_IMAGE_PATH)
            } else {
                stored[Keys.PROFILE_IMAGE_PATH] = imagePath
            }

            val signature = profile.cardSignature.trim()
            if (signature.isEmpty()) {
                stored.remove(Keys.CARD_SIGNATURE)
            } else {
                stored[Keys.CARD_SIGNATURE] = signature
            }
        }
    }

    suspend fun setFavoriteGreeting(greetingId: String, isFavorite: Boolean) {
        dataStore.edit { stored ->
            val favorites = stored[Keys.FAVORITE_GREETING_IDS].orEmpty().toMutableSet()
            if (isFavorite) favorites.add(greetingId) else favorites.remove(greetingId)
            if (favorites.isEmpty()) stored.remove(Keys.FAVORITE_GREETING_IDS)
            else stored[Keys.FAVORITE_GREETING_IDS] = favorites
        }
    }

    suspend fun setPreferredLanguage(language: AppLanguage) {
        dataStore.edit { stored ->
            stored[Keys.PREFERRED_LANGUAGE] = language.code
        }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { stored ->
            stored[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun clearProfile() {
        dataStore.edit { stored ->
            stored.remove(Keys.DISPLAY_NAME)
            stored.remove(Keys.PROFILE_IMAGE_PATH)
            stored.remove(Keys.CARD_SIGNATURE)
        }
    }

    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("profile_display_name")
        val PROFILE_IMAGE_PATH = stringPreferencesKey("profile_image_path")
        val CARD_SIGNATURE = stringPreferencesKey("profile_card_signature")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val DARK_THEME = booleanPreferencesKey("dark_theme_enabled")
        val FAVORITE_GREETING_IDS = stringSetPreferencesKey("favorite_greeting_ids")
    }
}
