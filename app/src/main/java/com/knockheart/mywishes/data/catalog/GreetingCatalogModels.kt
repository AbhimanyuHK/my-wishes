package com.knockheart.mywishes.data.catalog

data class Greeting(
    val id: String,
    val text: String
)

data class Occasion(
    val id: String,
    val title: String,
    val description: String,
    val greetings: List<Greeting>
)

data class OccasionCatalog(
    val languageCode: String,
    val occasions: List<Occasion>
)
