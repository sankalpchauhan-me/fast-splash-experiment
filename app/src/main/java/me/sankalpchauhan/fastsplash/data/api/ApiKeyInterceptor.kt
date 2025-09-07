package me.sankalpchauhan.fastsplash.data.api

import me.sankalpchauhan.fastsplash.utils.TMDB_API_KEY
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val urlWithApiKey = originalUrl.newBuilder()
            .addQueryParameter("api_key", TMDB_API_KEY)
            .build()
        
        val requestWithApiKey = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        
        return chain.proceed(requestWithApiKey)
    }
}