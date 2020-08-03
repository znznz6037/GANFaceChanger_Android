package com.webview.gan.gan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * Created by dlehd on 2020-08-02.
 */

public class splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);
    }
    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class));
            splash.this.finish();
        }
    }
}
