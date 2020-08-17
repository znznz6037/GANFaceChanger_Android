package com.webview.gan.gan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Window

/**
 * Created by dlehd on 2020-08-02.
 */

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.splash_activity)
        val hd = Handler()
        hd.postDelayed(splashhandler(), 3000)
    }

    private inner class splashhandler : Runnable {
        override fun run() {
            startActivity(Intent(application, MainActivity::class.java))
            this@splash.finish()
        }
    }
}
