package me.sankalpchauhan.fastsplash.presentation.listing

import me.sankalpchauhan.fastsplash.data.model.Movie

data class ListingState(
    val isLoading: Boolean = false,
    val movies: Set<Movie> = emptySet(), //Using set as we were getting duplicate ids and repeated entries
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val userQuery: String = "",
    val isSearchMode: Boolean = false,
    val hasMorePages: Boolean = false
) {
    fun hasMovies(): Boolean = movies.isNotEmpty()
    fun hasError(): Boolean = error != null
    fun canLoadMore(): Boolean = hasMorePages && !isLoading
}