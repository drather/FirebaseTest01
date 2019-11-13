package com.example.firebasetest01;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationtest);

        String text = "전달 받은 값은";
        int id = 0;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            text = "값을 전달 받는데 문제 발생";
        }
        else {
            id = extras.getInt("notificationId");
        }
        TextView textView_notificationResult = (TextView) findViewById(R.id.textView_notificationResult);
        textView_notificationResult.setText(text + " " + id);

        NotificationManager notificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //노티피케이션 제거
        notificationManager.cancel(id);
        }
    }