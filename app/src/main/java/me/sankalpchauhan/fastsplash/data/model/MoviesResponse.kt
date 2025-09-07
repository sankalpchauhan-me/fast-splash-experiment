package me.sankalpchauhan.fastsplash.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class MoviesResponse(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("results")
    val results: List<Movie> = emptyList(),
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("total_results")
    val totalResults: Int = 0
) : Parcelable

@Keep
@Serializable
@Parcelize
data class Movie(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("popularity")
    val popularity: Double = 0.0,
    @SerialName("adult")
    val adult: Boolean = false,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("video")
    val video: Boolean = false
) : Parcelable {

    fun getReleaseYear(): String? {
        return releaseDate?.take(4)
    }
}
