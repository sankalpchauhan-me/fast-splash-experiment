package me.sankalpchauhan.eventsplash.viewprovider

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.delay
import me.sankalpchauhan.eventsplash.model.ImageConfig
import me.sankalpchauhan.eventsplash.utils.AnimatedExitOrFinish
import me.sankalpchauhan.eventsplash.utils.splashBackground

class ImageSplashProvider(
    private val config: ImageConfig
) : SplashComposableProvider {
    @Composable
    override fun Content(onFinish: () -> Unit) {
        val imageConfig = this.config
        val isFullScreen = imageConfig.bgColor.isEmpty()

        var triggerExit by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(imageConfig.showDuration)
            triggerExit = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .splashBackground(imageConfig.bgColor),
            contentAlignment = Alignment.Center
        ) {
            val imageBitmap = remember(imageConfig.drawable) { imageConfig.drawable.toBitmap().asImageBitmap() }
            val inner: @Composable () -> Unit = {
                if (isFullScreen) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

            AnimatedExitOrFinish(
                animType = imageConfig.outAnimation,
                durationMs = imageConfig.outDuration,
                start = triggerExit,
                onHandledExit = { onFinish() },
            ) { inner() }
        }
    }
}


