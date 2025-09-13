package me.sankalpchauhan.eventsplash.viewprovider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import me.sankalpchauhan.eventsplash.model.LottieConfig
import me.sankalpchauhan.eventsplash.utils.AnimatedExitOrFinish
import me.sankalpchauhan.eventsplash.utils.splashBackground

class LottieSplashProvider(
    private val config: LottieConfig
) : SplashComposableProvider {
    @Composable
    override fun Content(onFinish: () -> Unit) {
        val lottieCfg = this.config

        var triggerExit by remember { mutableStateOf(false) }

        val animContent: @Composable () -> Unit = {
            val progress by animateLottieCompositionAsState(
                composition = lottieCfg.lottieComposition,
                iterations = 1
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .splashBackground(lottieCfg.bgColor),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = lottieCfg.lottieComposition,
                    progress = { progress }
                )
            }
            if (!triggerExit && progress >= 0.8f) {
                triggerExit = true
            }
        }

        AnimatedExitOrFinish(
            animType = lottieCfg.outAnimation,
            durationMs = lottieCfg.outDuration,
            start = triggerExit,
            onHandledExit = { onFinish() },
        ) { animContent() }
    }
}


