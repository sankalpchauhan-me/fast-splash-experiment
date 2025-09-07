package me.sankalpchauhan.fastsplash.domain.model

sealed class DataState<out T> {
    data object Loading: DataState<Nothing>()
    data class Success<T>(val data: T): DataState<T>()
    data class Error<T>(val data: T? = null, val throwable: Throwable?): DataState<T>()
}