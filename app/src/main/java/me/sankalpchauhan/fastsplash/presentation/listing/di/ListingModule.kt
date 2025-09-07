package me.sankalpchauhan.fastsplash.presentation.listing.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import me.sankalpchauhan.fastsplash.domain.repository.DefaultMoviesRepository
import me.sankalpchauhan.fastsplash.domain.repository.MoviesRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class ListingModule {
    @Binds
    abstract fun bindListingRepository(moviesRepository: DefaultMoviesRepository): MoviesRepository
}