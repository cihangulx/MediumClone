package com.cihangul.mediumclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cihangul.mediumclone.MainActivity;
import com.cihangul.mediumclone.R;
import com.parse.ParseUser;

public class SplashScreen extends AppCompatActivity {

    private static final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuth();
    }

    private void checkAuth(){
        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated())
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            },DELAY_TIME);
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                }
            },DELAY_TIME);
        }

    }

}
