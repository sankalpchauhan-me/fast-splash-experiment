package me.sankalpchauhan.fastsplash

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.sankalpchauhan.perftracker.PerfTrace

@HiltAndroidApp
class FastSplashApplication: Application(){
    val fcp = PerfTrace("FCP")
    val pageRender = PerfTrace("RenderTrace")
    val fpt = PerfTrace("FPT")
    override fun onCreate() {
        super.onCreate()
        fcp.startTrace()
        pageRender.startTrace()
        fpt.startTrace()
    }
}