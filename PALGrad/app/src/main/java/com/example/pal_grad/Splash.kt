package com.example.pal_grad

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by dlehd on 2020-08-02.
 */

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.splash_activity)
        this.window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.WHITE
        }
        val handler = Handler()
        handler.postDelayed(SplashHandler(), 3000)
    }

    private inner class SplashHandler : Runnable {
        override fun run() {
            startActivity(Intent(application, MainActivity::class.java))
            this@Splash.finish()
        }
    }
}