package me.sankalpchauhan.fastsplash

import android.app.Application
import android.os.Trace
import dagger.hilt.android.HiltAndroidApp
import me.sankalpchauhan.perftracker.PerfTrace

@HiltAndroidApp
class FastSplashApplication: Application(){
    val fcp = PerfTrace("FCP")
    val pageRender = PerfTrace("RenderTrace")
    val fpt = PerfTrace("FPT")
    override fun onCreate() {
        Trace.beginSection("Application#onCreate")
        try {
            super.onCreate()
            fcp.startTrace()
            pageRender.startTrace()
            fpt.startTrace()
        } finally {
            Trace.endSection()
        }
    }
}