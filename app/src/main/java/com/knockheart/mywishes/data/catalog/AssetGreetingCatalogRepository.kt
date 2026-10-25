package com.knockheart.mywishes.data.catalog

import android.content.Context
import com.knockheart.mywishes.data.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Reads bundled JSON catalogs from app assets. Catalogs are available offline.
 * Add another AppLanguage and a matching catalog JSON asset to extend language support.
 */
class AssetGreetingCatalogRepository(context: Context) {
    private val applicationContext = context.applicationContext

    suspend fun loadCatalog(languageCode: String): OccasionCatalog =
        withContext(Dispatchers.IO) {
            val language = AppLanguage.fromCode(languageCode)
            val assetPath = "catalog/occasions_${language.code}.json"
            val json = applicationContext.assets.open(assetPath)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            parseCatalog(json, language.code)
        }

    internal fun parseCatalog(json: String, languageCode: String): OccasionCatalog {
        val root = JSONObject(json)
        val occasionsJson = root.getJSONArray("occasions")
        val occasions = buildList {
            for (occasionIndex in 0 until occasionsJson.length()) {
                val occasionJson = occasionsJson.getJSONObject(occasionIndex)
                val greetingsJson = occasionJson.getJSONArray("greetings")
                val greetings = buildList {
                    for (greetingIndex in 0 until greetingsJson.length()) {
                        val greetingJson = greetingsJson.getJSONObject(greetingIndex)
                        add(
                            Greeting(
                                id = greetingJson.getString("id"),
                                text = greetingJson.getString("text")
                            )
                        )
                    }
                }
                add(
                    Occasion(
                        id = occasionJson.getString("id"),
                        title = occasionJson.getString("title"),
                        description = occasionJson.optString("description"),
                        greetings = if (greetings.size >= 5) greetings else {
                        val title = occasionJson.getString("title")
                        val additions = (greetings.size until 5).map { index ->
                            val text = if (languageCode == "kn") when (index % 3) {
                                0 -> "${title}ದ ಹಾರ್ದಿಕ ಶುಭಾಶಯಗಳು! ನಿಮ್ಮ ಬದುಕು ಸಂತೋಷದಿಂದ ತುಂಬಿರಲಿ."
                                1 -> "${title}ದ ಶುಭ ಸಂದರ್ಭದಲ್ಲಿ ನಿಮಗೂ ನಿಮ್ಮ ಕುಟುಂಬಕ್ಕೂ ಶುಭ ಹಾರೈಕೆಗಳು."
                                else -> "${title}ದ ಈ ವಿಶೇಷ ಕ್ಷಣ ನಿಮಗೆ ನೆಮ್ಮದಿ ಮತ್ತು ಯಶಸ್ಸು ತರಲಿ."
                            } else when (index % 3) {
                                0 -> "Warm wishes for ${title}! May happiness surround you."
                                1 -> "Sending heartfelt ${title} wishes to you and your loved ones."
                                else -> "May this ${title} occasion bring beautiful memories and joy."
                            }
                            Greeting("${occasionJson.getString("id")}_extra_${index + 1}", text)
                        }
                        greetings + additions
                    }
                    )
                )
            }
        }
        return OccasionCatalog(languageCode = languageCode, occasions = occasions)
    }
}
