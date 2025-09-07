package me.sankalpchauhan.fastsplash

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.sankalpchauhan.perftracker.PerfTrace

@HiltAndroidApp
class FastSplashApplication: Application(){
    val fcp = PerfTrace("FCP")
    override fun onCreate() {
        super.onCreate()
        fcp.startTrace()
    }
}