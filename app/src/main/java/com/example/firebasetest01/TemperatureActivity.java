package com.example.firebasetest01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TemperatureActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent = getIntent();
        String ID = intent.getStringExtra("userEmail");

        databaseReference.child("User List").child(ID).child("temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();
                TextView textView_temperature = findViewById(R.id.textView_temperature);
                textView_temperature.setText("현재 차량의 온도는 " + data + "도 입니다");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("User List").child(ID).child("motion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();
                TextView textView_motion = findViewById(R.id.textView_motion);
                if (data.equals("false")) {
                    textView_motion.setText("차량에 움직임이 감지되지 않습니다");
                }
                else if (data.equals("true")) {
                    textView_motion.setText("차량에 움직임이 감지되고 있습니다.");
                }
                else {
                    textView_motion.setText("이도저도 아님, 오류인듯");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
