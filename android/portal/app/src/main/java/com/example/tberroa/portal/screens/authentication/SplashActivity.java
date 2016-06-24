package com.example.tberroa.portal.screens.authentication;

// Intermediate activity which occurs right after signing in. It's purpose
// is to allow for some initialization processing to occur.

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.tberroa.portal.R;
import com.example.tberroa.portal.screens.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean inView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // go to home activity after 3 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (inView) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inView = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        inView = false;
    }
}
