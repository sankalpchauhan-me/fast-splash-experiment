package me.sankalpchauhan.eventsplash

import androidx.activity.ComponentActivity
import me.sankalpchauhan.eventsplash.core.EventSplash
import me.sankalpchauhan.eventsplash.model.DefaultConfig
import me.sankalpchauhan.eventsplash.model.EventSplashConfig
import android.graphics.drawable.Drawable

/**
 * Provides a fluent API for the init of EventSplash
 */
object EventSplashApi {
    @JvmStatic
    fun attachTo(activity: ComponentActivity): Builder = Builder(activity)

    class Builder(private val activity: ComponentActivity) {
        private var config: EventSplashConfig? = null

        fun with(config: EventSplashConfig) = apply { this.config = config }

        fun show() {
            val finalConfig = config ?: DefaultConfig(
                appIcon = getAppIcon()
            )
            EventSplash(activity = activity, config = finalConfig)
        }

        private fun getAppIcon(): Drawable {
            return activity.packageManager.getApplicationIcon(activity.applicationInfo)
        }
    }
}