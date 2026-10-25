package com.knockheart.mywishes.data.catalog

import android.content.Context
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Reads the actual offline date/event catalog bundled in assets.
 * The fixed-date catalog covers 2026–2030; never invent lunar festival dates.
 */
object DatedWishes {
    data class DatedOccasion(val date: LocalDate, val occasion: Occasion)

    private val cache = mutableMapOf<String, List<DatedOccasion>>()

    @Synchronized
    fun load(context: Context, languageCode: String): List<DatedOccasion> {
        val language = if (languageCode == "kn") "kn" else "en"
        return cache.getOrPut(language) {
            val raw = context.applicationContext.assets
                .open("catalog/dated_events_${language}.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val array = JSONObject(raw).getJSONArray("events")
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    val date = LocalDate.parse(item.getString("date"))
                    val greetingsJson = item.getJSONArray("greetings")
                    val greetings = buildList {
                        for (j in 0 until greetingsJson.length()) {
                            val greeting = greetingsJson.getJSONObject(j)
                            add(Greeting(greeting.getString("id"), greeting.getString("text")))
                        }
                    }
                    require(greetings.size >= 5) { "Insufficient wishes for ${item.getString("id")}" }
                    add(DatedOccasion(date, Occasion(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        description = date.toString(),
                        greetings = greetings
                    )))
                }
            }.sortedWith(compareBy<DatedOccasion> { it.date }.thenBy { it.occasion.title })
        }
    }

    fun upcoming(context: Context, today: LocalDate, languageCode: String, daysAhead: Long = 30): List<DatedOccasion> =
        load(context, languageCode).filter {
            !it.date.isBefore(today) && ChronoUnit.DAYS.between(today, it.date) <= daysAhead
        }

    fun forYear(context: Context, year: Int, languageCode: String): List<DatedOccasion> =
        load(context, languageCode).filter { it.date.year == year }
}
