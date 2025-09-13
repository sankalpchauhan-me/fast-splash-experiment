package me.sankalpchauhan.eventsplash.viewprovider

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import me.sankalpchauhan.eventsplash.model.DefaultConfig
import me.sankalpchauhan.eventsplash.model.OutAnimType
import me.sankalpchauhan.eventsplash.utils.AnimatedExitContainer
import me.sankalpchauhan.eventsplash.utils.splashBackground

class DefaultSplashProvider(
    private val config: DefaultConfig
) : SplashComposableProvider {
    @Composable
    override fun Content(onFinish: () -> Unit) {
        val triggerExit = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            triggerExit.value = true
        }

        val icon = config.appIcon.toBitmap().asImageBitmap()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .splashBackground(config.bgColor),
            contentAlignment = Alignment.Center
        ) {
            val inner: @Composable () -> Unit = {
                Image(
                    bitmap = icon,
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )
            }

            AnimatedExitContainer(
                animType = OutAnimType.ZOOM_IN,
                durationMs = config.outDuration,
                start = triggerExit.value,
                onEnd = { onFinish() }
            ) { inner() }
        }
    }
}


