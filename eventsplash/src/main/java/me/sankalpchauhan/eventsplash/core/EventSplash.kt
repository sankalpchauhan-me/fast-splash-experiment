package me.sankalpchauhan.eventsplash.core

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import me.sankalpchauhan.eventsplash.model.DefaultConfig
import me.sankalpchauhan.eventsplash.model.EventSplashConfig
import me.sankalpchauhan.eventsplash.model.ImageConfig
import me.sankalpchauhan.eventsplash.model.LottieConfig
import me.sankalpchauhan.eventsplash.viewprovider.DefaultSplashProvider
import me.sankalpchauhan.eventsplash.viewprovider.ImageSplashProvider
import me.sankalpchauhan.eventsplash.viewprovider.LottieSplashProvider
import me.sankalpchauhan.eventsplash.viewprovider.SplashComposableProvider

class EventSplash(
    private val activity: ComponentActivity,
    private val config: EventSplashConfig,
) {
    private var isReady: Boolean = false
    private var isCleanedUp = false
    private val decorView: ViewGroup = activity.window.decorView as ViewGroup
    private var composeView: ComposeView? = null

    private val gate = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            return if (isReady) {
                decorView.viewTreeObserver.removeOnPreDrawListener(this)
                true
            } else {
                false
            }
        }
    }

    init {
        decorView.viewTreeObserver.addOnPreDrawListener(gate)
        setupSplashCompose()
        isReady = true
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                removeView()
                super.onStop(owner)
            }
        })
    }

    private fun setupSplashCompose() {
        val view = ComposeView(activity).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setContent {
                getProvider(config).Content(onFinish = {
                    dismiss()
                })
            }
        }
        composeView = view
        decorView.addView(view)
    }

    private fun dismiss() {
        if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            removeView()
            return
        }
        removeView()
    }

    private fun getProvider(config: EventSplashConfig): SplashComposableProvider {
        return when (config) {
            is ImageConfig -> {
                ImageSplashProvider(config)
            }

            is LottieConfig -> {
                LottieSplashProvider(config)
            }

            is DefaultConfig -> {
                DefaultSplashProvider(config)
            }
        }
    }

    private fun removeView() {
        if (isCleanedUp) return
        isCleanedUp = true
        composeView?.let { view ->
            view.animate().setListener(null)
            decorView.removeView(view)
        }
    }
}