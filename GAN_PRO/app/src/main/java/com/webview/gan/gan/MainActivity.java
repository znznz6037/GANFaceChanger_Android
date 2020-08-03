package com.webview.gan.gan;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom( true );
        mWebSettings.setDisplayZoomControls(false);

        mWebSettings.setBuiltInZoomControls(true);
        mWebView.loadUrl("https://znznz6037.github.io/GANFaceChanger/");


    }
    public void onBackPressed() {

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");


        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.who) {
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
            alBuilder.setMessage("201524462 박세범\n" +
                    "201524499 안형진\n" +
                    "201624539 이동현");
            alBuilder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;

                }
            });
            alBuilder.setTitle("2020 전기 졸업과제");
            alBuilder.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
