package com.example.firebasetest01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            Intent intent = getIntent();
            String ID = intent.getStringExtra("userEmail");

            databaseReference.child("User List").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //이 밑에 5줄은 getValue()시험
                    UserData userDataFromDB = dataSnapshot.getValue(UserData.class);

                    TextView textView_email_profile, textView_name_profile;
                    EditText editText_phoneNum_profile, editText_carNum_profile, editText_carType_profile;

                    textView_email_profile = findViewById(R.id.textView_email_profile);
                    textView_name_profile = findViewById(R.id.textView_name_profile);
                    editText_phoneNum_profile = findViewById(R.id.editText_phoneNum_profile);
                    editText_carNum_profile = findViewById(R.id.editText_carNum_profile);
                    editText_carType_profile = findViewById(R.id.editText_carType_profile);

                    textView_email_profile.setText(userDataFromDB.getUserEmail());
                    textView_name_profile.setText(userDataFromDB.getName());
                    editText_phoneNum_profile.setText(userDataFromDB.getPhoneNum());
                    editText_carNum_profile.setText(userDataFromDB.getCarNum());
                    editText_carType_profile.setText(userDataFromDB.getCarType());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
}
