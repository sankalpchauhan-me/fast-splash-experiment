package me.sankalpchauhan.fastsplash.presentation.listing.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import me.sankalpchauhan.fastsplash.R
import me.sankalpchauhan.fastsplash.data.model.Movie
import me.sankalpchauhan.fastsplash.presentation.listing.ListingState
import me.sankalpchauhan.fastsplash.utils.TMDB_IMAGE_BASE_URL
import me.sankalpchauhan.fastsplash.utils.onVisibilityChanged

@Composable
fun MovieSearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchMode: Boolean,
    modifier: Modifier = Modifier,
    onFCP:()->Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isSearchMode) stringResource(R.string.search_results) else stringResource(R.string.popular_movies),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.onVisibilityChanged(threshold = 1.0f, onVisible = {onFCP.invoke()})
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_movies)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun MovieListContent(
    uiState: ListingState,
    onLoadMore: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    onError: () -> Unit,
    onFirstPaint: ()->Unit,
    onFullyPainted: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading && !uiState.hasMovies() -> {
                CircularProgressIndicator(
                    modifier = Modifier.onVisibilityChanged(threshold = 0.5f, onVisible = {
                        onFirstPaint.invoke()
                    })
                )
            }

            uiState.hasError() -> {
                ErrorState(
                    message = uiState.error ?: stringResource(R.string.something_went_wrong),
                    modifier = Modifier.padding(16.dp),
                    onError = onError
                )
            }

            !uiState.hasMovies() && !uiState.isSearchMode -> {
                EmptyState(
                    title = stringResource(R.string.welcome_to_movie_app),
                    message = stringResource(R.string.popular_movies_are_loading),
                    modifier = Modifier.padding(16.dp)
                )
            }

            !uiState.hasMovies() && uiState.isSearchMode -> {
                EmptyState(
                    title = stringResource(R.string.no_results_found),
                    message = stringResource(R.string.try_searching_for_a_different_movie),
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                MovieList(
                    movies = uiState.movies.toList(),
                    isLoading = uiState.isLoading,
                    canLoadMore = uiState.canLoadMore(),
                    onLoadMore = onLoadMore,
                    onMovieClick = onMovieClick,
                    onFullyPainted = onFullyPainted
                )
            }
        }
    }
}


@Composable
fun MovieList(
    movies: List<Movie>,
    isLoading: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    onFullyPainted: ()->Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.map { it.index } }
            .collect { visible ->
                if(visible.isNotEmpty()){
                    onFullyPainted.invoke()
                }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = movies.size,
            key = { index -> movies[index].id }
        ) { index ->
            val movie = movies[index]
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null &&
                lastVisibleIndex >= movies.size - 3 &&
                canLoadMore &&
                !isLoading) {
                onLoadMore()
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePosterImage(
                posterPath = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (movie.releaseDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.getReleaseYear() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (movie.overview?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.rating),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.vote_10, movie.voteAverage),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MoviePosterImage(
    posterPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val imageUrl = posterPath?.let { "$TMDB_IMAGE_BASE_URL$it" }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.no_poster_available),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onError: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.oops_something_went_wrong),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = {
            onError.invoke()
        }) {
            Text(
                text = stringResource(R.string.retry),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}