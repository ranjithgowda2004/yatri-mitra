package com.yatrimitra.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var ready = false
        splashScreen.setKeepOnScreenCondition { !ready }

        lifecycleScope.launch {
            delay(800)
            ready = true
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
