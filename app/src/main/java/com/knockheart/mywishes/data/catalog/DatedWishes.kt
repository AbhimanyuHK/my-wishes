package com.knockheart.mywishes.data.catalog

import java.time.LocalDate
import java.time.MonthDay
import java.time.temporal.ChronoUnit

/** Verified fixed-date observances only. Lunar/region-dependent festivals must not be guessed. */
object DatedWishes {
    private data class Event(val id: String, val month: Int, val day: Int, val english: String, val kannada: String)
    private val events = listOf(
        Event("new_year",1,1,"New Year's Day","ಹೊಸ ವರ್ಷದ ದಿನ"),
        Event("youth_day",1,12,"National Youth Day","ರಾಷ್ಟ್ರೀಯ ಯುವ ದಿನ"),
        Event("army_day",1,15,"Indian Army Day","ಭಾರತೀಯ ಸೇನಾ ದಿನ"),
        Event("republic_day",1,26,"Republic Day","ಗಣರಾಜ್ಯೋತ್ಸವ"),
        Event("science_day",2,28,"National Science Day","ರಾಷ್ಟ್ರೀಯ ವಿಜ್ಞಾನ ದಿನ"),
        Event("womens_day",3,8,"International Women's Day","ಅಂತರರಾಷ್ಟ್ರೀಯ ಮಹಿಳಾ ದಿನ"),
        Event("earth_day",4,22,"Earth Day","ಭೂಮಿ ದಿನ"),
        Event("labour_day",5,1,"Labour Day","ಕಾರ್ಮಿಕ ದಿನ"),
        Event("environment_day",6,5,"World Environment Day","ವಿಶ್ವ ಪರಿಸರ ದಿನ"),
        Event("doctors_day",7,1,"National Doctors' Day","ರಾಷ್ಟ್ರೀಯ ವೈದ್ಯರ ದಿನ"),
        Event("independence_day",8,15,"Independence Day","ಸ್ವಾತಂತ್ರ್ಯ ದಿನಾಚರಣೆ"),
        Event("teachers_day",9,5,"Teachers' Day","ಶಿಕ್ಷಕರ ದಿನ"),
        Event("engineers_day",9,15,"Engineers' Day","ಇಂಜಿನಿಯರ್‌ಗಳ ದಿನ"),
        Event("gandhi_jayanti",10,2,"Gandhi Jayanti","ಗಾಂಧಿ ಜಯಂತಿ"),
        Event("karnataka_rajyotsava",11,1,"Karnataka Rajyotsava","ಕರ್ನಾಟಕ ರಾಜ್ಯೋತ್ಸವ"),
        Event("childrens_day",11,14,"Children's Day","ಮಕ್ಕಳ ದಿನ"),
        Event("constitution_day",11,26,"Constitution Day","ಸಂವಿಧಾನ ದಿನ"),
        Event("christmas",12,25,"Christmas Day","ಕ್ರಿಸ್‌ಮಸ್ ದಿನ")
    )

    data class DatedOccasion(val date: LocalDate, val occasion: Occasion)

    fun upcoming(today: LocalDate, languageCode: String, daysAhead: Long = 30): List<DatedOccasion> =
        (2026..2030).flatMap { year ->
            events.map { event ->
                val date = LocalDate.of(year, event.month, event.day)
                val title = if (languageCode == "kn") event.kannada else event.english
                val texts = if (languageCode == "kn") listOf(
                    "${title}ದ ಹಾರ್ದಿಕ ಶುಭಾಶಯಗಳು! ನಿಮ್ಮ ದಿನ ಸಂತೋಷದಿಂದ ತುಂಬಿರಲಿ.",
                    "${title}ದ ಶುಭ ಸಂದರ್ಭದಲ್ಲಿ ನಿಮಗೂ ನಿಮ್ಮ ಕುಟುಂಬಕ್ಕೂ ಶುಭ ಹಾರೈಕೆಗಳು.",
                    "${title}ದ ಸಂಭ್ರಮ ನಿಮ್ಮ ಬದುಕಿನಲ್ಲಿ ಹೊಸ ಉತ್ಸಾಹ ತರಲಿ.",
                    "${title}ದ ಈ ವಿಶೇಷ ದಿನ ಶಾಂತಿ, ಪ್ರೀತಿ ಮತ್ತು ಯಶಸ್ಸು ತರಲಿ.",
                    "${title}ದ ಶುಭಾಶಯಗಳು! ಒಳ್ಳೆಯ ನೆನಪುಗಳು ಸದಾ ನಿಮ್ಮೊಂದಿಗಿರಲಿ."
                ) else listOf(
                    "Warm wishes on ${title}! May your day be filled with joy.",
                    "Wishing you and your family a wonderful ${title}.",
                    "May ${title} bring fresh inspiration and beautiful memories.",
                    "Celebrating ${title} with gratitude, hope and happiness.",
                    "Happy ${title}! Wishing you peace, kindness and success."
                )
                DatedOccasion(date, Occasion(
                    id = "dated_${event.id}_${year}",
                    title = title,
                    description = date.toString(),
                    greetings = texts.mapIndexed { index, text -> Greeting("dated_${event.id}_${year}_${index + 1}", text) }
                ))
            }
        }.filter { !it.date.isBefore(today) && ChronoUnit.DAYS.between(today, it.date) <= daysAhead }
            .sortedWith(compareBy<DatedOccasion> { it.date }.thenBy { it.occasion.title })

    fun forYear(year: Int, languageCode: String): List<DatedOccasion> {
        require(year in 2026..2030)
        return upcoming(LocalDate.of(year, 1, 1), languageCode, 366)
            .filter { it.date.year == year }
    }
}
