package me.sankalpchauhan.eventsplash

import androidx.activity.ComponentActivity
import me.sankalpchauhan.eventsplash.core.EventSplash
import me.sankalpchauhan.eventsplash.model.EventSplashConfig

object EventSplashApi {

    @JvmStatic
    fun attachTo(activity: ComponentActivity, config: EventSplashConfig){
        EventSplash(activity = activity, config = config)
    }
}