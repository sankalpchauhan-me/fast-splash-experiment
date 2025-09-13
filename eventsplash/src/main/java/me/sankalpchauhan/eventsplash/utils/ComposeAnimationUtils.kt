package me.sankalpchauhan.eventsplash.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import me.sankalpchauhan.eventsplash.model.OutAnimType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AnimatedExitContainer(
    animType: OutAnimType,
    durationMs: Long,
    start: Boolean,
    onEnd: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }
    val translateX = remember { Animatable(0f) }
    val translateY = remember { Animatable(0f) }
    val sizeState = remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(start, animType, durationMs) {
        if (!start) return@LaunchedEffect
        when (animType) {
            OutAnimType.ZOOM_IN -> {
                coroutineScope {
                    val zoomEffect = launch { scale.animateTo(1.15f, tween(durationMs.toInt())) }
                    val fadeOutEffect = launch { alpha.animateTo(0f, tween(durationMs.toInt())) }
                    zoomEffect.join(); fadeOutEffect.join()
                }
            }
            OutAnimType.FADE_OUT -> {
                alpha.animateTo(0f, tween(durationMs.toInt()))
            }
            OutAnimType.SLIDE_UP -> {
                val h = sizeState.value.height.toFloat()
                translateY.animateTo(-h, tween(durationMs.toInt()))
            }
            OutAnimType.SLIDE_LEFT -> {
                val w = sizeState.value.width.toFloat()
                translateX.animateTo(-w, tween(durationMs.toInt()))
            }
        }
        onEnd()
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .onGloballyPositioned { sizeState.value = it.size }
            .graphicsLayer(
                alpha = alpha.value,
                scaleX = scale.value,
                scaleY = scale.value,
                translationX = translateX.value,
                translationY = translateY.value
            )
    ) {
        content()
    }
}

private fun safeColor(s: String): androidx.compose.ui.graphics.Color =
    runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(s)) }
        .getOrElse { androidx.compose.ui.graphics.Color.White }

fun Modifier.splashBackground(hex: List<String>): Modifier = when (hex.size) {
    0 -> this
    1 -> this.background(safeColor(hex.first()))
    else -> this.background(brush = Brush.verticalGradient(colors = hex.map(::safeColor)))
}

@Composable
fun AnimatedExitOrFinish(
    animType: OutAnimType,
    durationMs: Long,
    start: Boolean,
    onHandledExit: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    if (start) {
        AnimatedExitContainer(
            animType = animType,
            durationMs = durationMs,
            start = true,
            onEnd = onHandledExit,
            modifier = modifier
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier,
                contentAlignment = alignment
            ) { content() }
        }
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = modifier,
            contentAlignment = alignment
        ) { content() }
    }
}


