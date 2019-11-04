package com.example.firebasetest01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        LinearLayout linearLayout_login = (LinearLayout)findViewById(R.id.linearLayout_login);

        linearLayout_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //intent로 값을 주고받는 예
                Intent intent = new Intent(LoadingActivity.this,LoginActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        finish();
    }
}
