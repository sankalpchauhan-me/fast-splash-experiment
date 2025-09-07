package me.sankalpchauhan.fastsplash.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.sankalpchauhan.fastsplash.data.api.NetworkService
import me.sankalpchauhan.fastsplash.data.model.MoviesRequest
import me.sankalpchauhan.fastsplash.data.model.MoviesResponse
import me.sankalpchauhan.fastsplash.domain.model.DataState
import me.sankalpchauhan.fastsplash.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

interface MoviesRepository {
    suspend fun getPopularMovies(request: MoviesRequest): Flow<DataState<MoviesResponse>>
    suspend fun searchMovies(query: String, request: MoviesRequest): Flow<DataState<MoviesResponse>>
}

@Singleton
class DefaultMoviesRepository @Inject constructor(
    private val networkService: NetworkService
) : MoviesRepository {
    
    override suspend fun getPopularMovies(request: MoviesRequest): Flow<DataState<MoviesResponse>> {
        return flow {
            emit(DataState.Loading)
            val response = safeApiCall { 
                networkService.getPopularMovies(
                    page = request.page,
                    language = request.language,
                    region = request.region
                )
            }
            
            response.onSuccess { data ->
                emit(DataState.Success(data))
            }
            
            response.onFailure { throwable ->
                emit(DataState.Error(null, throwable))
            }
        }
    }
    
    override suspend fun searchMovies(query: String, request: MoviesRequest): Flow<DataState<MoviesResponse>> {
        return flow {
            emit(DataState.Loading)
            val response = safeApiCall { 
                networkService.searchMovies(
                    query = query,
                    page = request.page,
                    language = request.language,
                    region = request.region,
                    includeAdult = false
                )
            }
            
            response.onSuccess { data ->
                emit(DataState.Success(data))
            }
            
            response.onFailure { throwable ->
                emit(DataState.Error(null, throwable))
            }
        }
    }
}