package me.sankalpchauhan.fastsplash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.sankalpchauhan.fastsplash.presentation.listing.MainActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splash = installSplashScreen()
        enableEdgeToEdge()
        splash.setOnExitAnimationListener{ exit ->
            // Some routing logic basis some conditions
            this@SplashActivity.startActivity(
                Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )
            )
        }
    }
}