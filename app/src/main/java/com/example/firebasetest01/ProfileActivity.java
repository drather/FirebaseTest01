package com.example.firebasetest01;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    final private String TAG = "Profile_Activity";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    TextView textView_email_profile, textView_name_profile;
    EditText editText_phoneNum_profile, editText_carNum_profile, editText_carType_profile;
    Intent intent;
    Button btn_modifyProfile, btn_confirm;
    String ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        ID = intent.getStringExtra("userEmail");
        Log.d(TAG, "넘어온 Id: " + ID);

        databaseReference.child("User List").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, ID + "로 dataChange 들어옴");
                UserData userDataFromDB = dataSnapshot.getValue(UserData.class);

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

        btn_modifyProfile = (Button)findViewById(R.id.btn_modifyProfile);
        btn_confirm = (Button)findViewById(R.id.btn_confirm);

        btn_modifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_phoneNum_profile.setEnabled(true);
                editText_carNum_profile.setEnabled(true);
                editText_carType_profile.setEnabled(true);
                btn_confirm.setEnabled(true);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modified_phoneNum = editText_phoneNum_profile.getText().toString();
                String modified_carNum = editText_carNum_profile.getText().toString();
                String modified_carType = editText_carType_profile.getText().toString();

                databaseReference.child("User List").child(ID).child("phoneNum").setValue(modified_phoneNum);
                databaseReference.child("User List").child(ID).child("carNum").setValue(modified_carNum);
                databaseReference.child("User List").child(ID).child("carType").setValue(modified_carType)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "회원 정보가 성공적으로 변경되었습니다",
                                Toast.LENGTH_LONG).show();
                    }
                });


                Log.d(TAG, "modified_phoneNum" + modified_phoneNum);

                editText_phoneNum_profile.setEnabled(false);
                editText_carNum_profile.setEnabled(false);
                editText_carType_profile.setEnabled(false);

                btn_confirm.setEnabled(false);
        }
        });

//        Button btn_changePassword = findViewById(R.id.btn_changePassword);
//        btn_changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String newPassword = "";
//
//                user.updatePassword(newPassword)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "User password updated.");
//                                }
//                            }
//                        });
//            }
//        });

    }
}
