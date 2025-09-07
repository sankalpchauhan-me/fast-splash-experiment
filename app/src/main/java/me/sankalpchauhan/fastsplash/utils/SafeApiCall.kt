package me.sankalpchauhan.fastsplash.utils

suspend fun <T> safeApiCall(apiCall: suspend ()->T): Result<T>{
    try {
        return Result.success(apiCall())
    } catch (e: Exception){
        e.printStackTrace()
        return Result.failure(e)
    }
}