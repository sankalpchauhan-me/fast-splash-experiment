package me.sankalpchauhan.eventsplash.viewprovider

import androidx.compose.runtime.Composable

fun interface SplashComposableProvider {
    @Composable
    fun Content(onFinish: () -> Unit)
}


