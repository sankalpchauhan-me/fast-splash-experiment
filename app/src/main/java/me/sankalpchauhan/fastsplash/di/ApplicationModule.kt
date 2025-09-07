package me.sankalpchauhan.fastsplash.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sankalpchauhan.fastsplash.utils.TMDB_BASE_URL

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = TMDB_BASE_URL

}