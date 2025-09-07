package me.sankalpchauhan.fastsplash.presentation.listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.sankalpchauhan.fastsplash.BuildConfig
import me.sankalpchauhan.fastsplash.data.model.MoviesRequest
import me.sankalpchauhan.fastsplash.data.model.MoviesResponse
import me.sankalpchauhan.fastsplash.domain.model.DataState
import me.sankalpchauhan.fastsplash.domain.repository.MoviesRepository
import javax.inject.Inject


@OptIn(FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MoviesRepository
) : ViewModel() {
    
    private val _userQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow(ListingState())
    val uiState: StateFlow<ListingState> = _uiState

    init {
        loadPopularMovies()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _userQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        loadPopularMovies()
                    } else {
                        searchMovies(query)
                    }
                }
        }
    }

    private fun loadPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            val request = MoviesRequest(page = page)
            repository.getPopularMovies(request).collect { state ->
                handlePopularMoviesState(state, page)
            }
        }
    }

    private fun searchMovies(query: String, page: Int = 1) {
        viewModelScope.launch {
            val request = MoviesRequest(page = page)
            repository.searchMovies(query, request).collect { state ->
                handleSearchMoviesState(state, page)
            }
        }
    }


    fun onUserQuery(query: String) {
        _userQuery.value = query
        _uiState.value = _uiState.value.copy(
            userQuery = query,
            isSearchMode = query.isNotBlank()
        )
    }


    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.canLoadMore()) {
            val nextPage = currentState.currentPage + 1
            if (currentState.isSearchMode) {
                searchMovies(currentState.userQuery, nextPage)
            } else {
                loadPopularMovies(nextPage)
            }
        }
    }

    fun refresh() {
        val currentState = _uiState.value
        if (currentState.isSearchMode) {
            searchMovies(currentState.userQuery)
        } else {
            loadPopularMovies()
        }
    }

    private fun handlePopularMoviesState(state: DataState<MoviesResponse>, page: Int) {
        _uiState.value = when (state) {
            is DataState.Loading -> {
                handleLoadingState()
            }

            is DataState.Success -> {
                val currentMovies = if (page == 1) emptySet() else _uiState.value.movies
                _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    movies = currentMovies + state.data.results,
                    currentPage = state.data.page,
                    totalPages = state.data.totalPages,
                    hasMorePages = state.data.page < state.data.totalPages,
                    isSearchMode = false
                )
            }

            is DataState.Error -> {
                handleErrorState(state)
            }
        }
    }

    private fun handleSearchMoviesState(state: DataState<MoviesResponse>, page: Int) {
        _uiState.value = when (state) {
            is DataState.Loading -> {
                handleLoadingState()
            }

            is DataState.Success -> {
                val currentMovies = if (page == 1) emptySet() else _uiState.value.movies
                _uiState.value.copy(
                    isLoading = false,
                    error = null,
                    movies = currentMovies + state.data.results.toSet(),
                    currentPage = state.data.page,
                    totalPages = state.data.totalPages,
                    hasMorePages = state.data.page < state.data.totalPages,
                    isSearchMode = true
                )
            }

            is DataState.Error -> {
                handleErrorState(state)
            }
        }
    }

    private fun handleErrorState(state: DataState.Error<MoviesResponse>): ListingState{
        val errorState = _uiState.value.copy(
            isLoading = false,
            error = state.throwable?.message
        )
        if(BuildConfig.TMDB_API_KEY.isNullOrEmpty() || BuildConfig.TMDB_API_KEY == "YOUR_TMDB_API_KEY_HERE"){
            return errorState.copy(error = "API KEY not setup properly, please read instructions in README to setup")
        }
        return errorState
    }

    private fun handleLoadingState() = _uiState.value.copy(
        isLoading = true,
        error = null
    )
}