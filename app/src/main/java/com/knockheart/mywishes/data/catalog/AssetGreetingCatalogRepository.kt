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
                        greetings = greetings
                    )
                )
            }
        }
        return OccasionCatalog(languageCode = languageCode, occasions = occasions)
    }
}
