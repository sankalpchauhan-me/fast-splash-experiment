package me.sankalpchauhan.eventsplash.model

import android.graphics.drawable.Drawable
import com.airbnb.lottie.LottieComposition

sealed class EventSplashConfig(
    open val outAnimation: OutAnimType,
    open val outDuration: Long,
    open val bgColor: List<String>
)

data class ImageConfig(
    override val outAnimation: OutAnimType,
    override val outDuration: Long,
    override val bgColor: List<String>,
    val drawable: Drawable,
    val showDuration: Long
): EventSplashConfig(outAnimation, outDuration, bgColor)

data class DefaultConfig(
    override val outAnimation: OutAnimType = OutAnimType.FADE_OUT,
    override val outDuration: Long = 500,
    override val bgColor: List<String> = listOf("#FFFFFF"),
    val appIcon: Drawable
): EventSplashConfig(outAnimation, outDuration, bgColor)

data class LottieConfig(
    override val outAnimation: OutAnimType,
    override val outDuration: Long,
    val lottieComposition: LottieComposition,
    override val bgColor: List<String>,
): EventSplashConfig(outAnimation, outDuration, bgColor)



enum class OutAnimType{
    ZOOM_IN,
    FADE_OUT,
    SLIDE_UP,
    SLIDE_LEFT
}