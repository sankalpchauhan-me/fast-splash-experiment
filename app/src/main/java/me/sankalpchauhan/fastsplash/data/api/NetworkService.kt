package me.sankalpchauhan.fastsplash.data.api

import me.sankalpchauhan.fastsplash.data.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): MoviesResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null,
        @Query("include_adult") includeAdult: Boolean = false
    ): MoviesResponse
}