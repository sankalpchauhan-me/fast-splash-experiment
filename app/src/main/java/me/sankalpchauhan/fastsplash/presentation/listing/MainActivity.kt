package me.sankalpchauhan.fastsplash.presentation.listing

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import me.sankalpchauhan.fastsplash.FastSplashApplication
import me.sankalpchauhan.fastsplash.data.model.Movie
import me.sankalpchauhan.fastsplash.presentation.base.ui.theme.FastSplashTheme
import me.sankalpchauhan.fastsplash.presentation.listing.widgets.MovieCard
import me.sankalpchauhan.fastsplash.presentation.listing.widgets.MovieListContent
import me.sankalpchauhan.fastsplash.presentation.listing.widgets.MovieSearchHeader
import me.sankalpchauhan.perftracker.PerfTrace

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcpTrace = (applicationContext as FastSplashApplication).fcp
        val pageRender = (applicationContext as FastSplashApplication).pageRender
        val fptTrace = (applicationContext as FastSplashApplication).fpt
        pageRender.stopTrace()
        Log.d("PERF", "\tPage Ready\t${pageRender.getDuration()}")
        enableEdgeToEdge()
        setContent {
            val uiState by mainViewModel.uiState.collectAsState()
            var userQuery by remember { mutableStateOf("") }
            FastSplashTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        MovieSearchHeader(
                            query = userQuery,
                            onQueryChange = { query ->
                                userQuery = query
                                mainViewModel.onUserQuery(query)
                            },
                            isSearchMode = uiState.isSearchMode,
                            onFCP = {
                                logFcp(fcpTrace)
                            }
                        )
                    }
                ) { innerPadding ->
                    MovieListContent(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState,
                        onLoadMore = { mainViewModel.loadNextPage() },
                        onMovieClick = { movie ->
                            // TODO: Navigate to movie details
                        },
                        onError = {
                            mainViewModel.refresh()
                        },
                        onFirstPaint = {
                            logFcp(fcpTrace)
                        },
                        onFullyPainted = {
                            logFpt(fptTrace)
                        }
                    )
                }
            }
        }
    }

    private fun logFcp(
        fcpTrace: PerfTrace,
    ) {
        if (fcpTrace.isStopped.not()) {
            fcpTrace.stopTrace()
            Log.d("PERF", "\tFirst Content Painted Time\t${fcpTrace.getDuration()}")
        }
    }

    private fun logFpt(
        fptTrace: PerfTrace,
    ) {
        if (fptTrace.isStopped.not()) {
            fptTrace.stopTrace()
            Log.d("PERF", "\tFully Painted Time\t${fptTrace.getDuration()}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieCardPreview() {
    FastSplashTheme {
        MovieCard(
            movie = Movie(
                id = 1,
                title = "Sample Movie",
                overview = "This is a sample movie description that shows how the card looks with some text content.",
                releaseDate = "2024-01-01",
                voteAverage = 8.5,
                posterPath = null
            ),
            onClick = { }
        )
    }
}