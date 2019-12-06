package com.example.firebasetest01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoadingActivity extends AppCompatActivity {
    final private String TAG = "Loading_Activity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_2 = 2;

    private final int SPLASH_DISPLAY_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 퍼미션 받는 부분
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Log.e("permission", "SMS Permission already granted.");
            } else {
                requestPermission();
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission2()) {
                Log.e("permission", "INTERNET Permission already granted.");
            } else {
                requestPermission2();
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                startActivity(new Intent(getApplication(), LoginActivity.class));
                /* 스플래시 액티비티를 스택에서 제거. */
                LoadingActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPermission2() {
        int result = ContextCompat.checkSelfPermission(LoadingActivity.this, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    private void requestPermission2() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE_2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoadingActivity.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoadingActivity.this,
                            "Permission denied at function", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_CODE_2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoadingActivity.this,
                            "iNTERNET_Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoadingActivity.this,
                            "iNTERNET_Permission denied at function", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}