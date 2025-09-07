package me.sankalpchauhan.fastsplash.data.model


data class MoviesRequest(
    val page: Int = 1,
    val language: String = "en-US",
    val region: String? = null
)
