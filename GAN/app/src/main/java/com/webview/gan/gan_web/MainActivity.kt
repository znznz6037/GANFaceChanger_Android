package com.webview.gan.gan_web

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mWebSettings: WebSettings? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        mWebSettings = webView!!.settings
        mWebSettings!!.javaScriptEnabled = true
        mWebSettings!!.setSupportMultipleWindows(false)
        mWebSettings!!.javaScriptCanOpenWindowsAutomatically = false
        mWebSettings!!.defaultZoom = WebSettings.ZoomDensity.FAR
        webView!!.webViewClient = WebViewClient()
        mWebSettings!!.loadWithOverviewMode = true
        mWebSettings!!.useWideViewPort = true
        mWebSettings!!.setSupportZoom(true)
        mWebSettings!!.displayZoomControls = false

        mWebSettings!!.builtInZoomControls = true
        webView!!.loadUrl("https://znznz6037.github.io/GANFaceChanger/")


    }

    override fun onBackPressed() {

        val alBuilder = AlertDialog.Builder(this)
        alBuilder.setMessage("종료하시겠습니까?")


        alBuilder.setPositiveButton("예") { dialog, which -> finish() }

        alBuilder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
        alBuilder.setTitle("프로그램 종료")
        alBuilder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        if (id == R.id.who) {
            val alBuilder = AlertDialog.Builder(this)
            alBuilder.setMessage("201524462 박세범\n" +
                    "201524499 안형진\n" +
                    "201624539 이동현")
            alBuilder.setPositiveButton("닫기", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
            alBuilder.setTitle("2020 전기 졸업과제")
            alBuilder.show()
            return true
        }


        return super.onOptionsItemSelected(item)
    }
}
