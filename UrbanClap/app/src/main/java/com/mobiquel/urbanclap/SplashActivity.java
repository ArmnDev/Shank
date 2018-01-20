package com.mobiquel.urbanclap;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.mobiquel.urbanclap.utils.Preferences;

public class SplashActivity extends AppCompatActivity {

    private final Runnable  runnable = new Runnable() {
        @Override
        public void run() {

            Intent intent;
            Preferences.getInstance().loadPreferences(SplashActivity.this);
            if(Preferences.getInstance().isLoggedIn)
            {
                if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
                {
                    if(Preferences.getInstance().companyType.contains("3"))
                    {
                        intent = new Intent(SplashActivity.this, CompanyHomeHR.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(Preferences.getInstance().companyType.contains("4"))
                    {
                        intent = new Intent(SplashActivity.this, CompanyHomeCorporate.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        intent = new Intent(SplashActivity.this, CompanyHome.class);
                        startActivity(intent);
                        finish();
                    }

                }
                else
                {
                    intent = new Intent(SplashActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                }

            }
            else
            {
                intent = new Intent(SplashActivity.this, MobileOTPActivity.class);
                startActivity(intent);
                finish();
            }


        }
    };
    private Handler handler;
    private final int PERMISSION_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }
        else {
            moveOn();

        }
    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    private void moveOn() {
        final Handler handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveOn();

                } else {
                    // permission denied. show an explanation stating the importance of this permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }
                break;
        }
    }
}



