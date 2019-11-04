package com.example.firebasetest01;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference = mDatabase.child("User List/");

    private String email = "";
    private String password = "";

    EditText editText_candID, editText_candPW, editText_name, editText_phoneNumber, editText_carNumber, editText_carType;
    Button btn_submitSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editText_candID = findViewById(R.id.editText_candID);
        editText_candPW = findViewById(R.id.editText_candPW);
        editText_name = findViewById(R.id.editText_name);
        editText_phoneNumber = findViewById(R.id.editText_phoneNumber);
        editText_carNumber = findViewById(R.id.editText_carNumber);
        editText_carType = findViewById(R.id.editText_carType);

        btn_submitSignup = findViewById(R.id.btn_submitSignup);

        btn_submitSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                email = editText_candID.getText().toString();
                password = editText_candPW.getText().toString();

                    if(isValidEmail() && isValidPasswd()) {
                        createUser(email, password);

                        String userEmail = email;
                        String name = editText_name.getText().toString();
                        String phoneNum = editText_phoneNumber.getText().toString();
                        String carNum = editText_carNumber.getText().toString();
                        String type = editText_carType.getText().toString();

                        writeNewUser(userEmail, name, phoneNum, carNum, type);
                }

                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.putExtra("userEmail", email);
                startActivity(intent);
            }
        });

    }
    public void signup(View view) {
        email = editText_candID.getText().toString();
        password = editText_candPW.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }


    // 이메일 유효성 검사
            private boolean isValidEmail() {
                if (email.isEmpty()) {
                    // 이메일 공백
                    return false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
                    return false;
                 } else {
                    return true;
                 }
            }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(SignupActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();

                            // 이 부분 나중에 안되면 보셈(등록 성공하고나서 매인화면으로 안가면)
//                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                            startActivity(intent);

                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignupActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void writeNewUser(String uEmail, String name, String phoneNum, String carNum, String carType) {
        UserData data = new UserData(uEmail, name, phoneNum, carNum, carType);
        String replacedEmail = new String(uEmail.replace('@','_'));
        replacedEmail = replacedEmail.replace('.', '_');
        Log.d("uemail", data.getUserEmail());

        mDatabase.child("User List").child(replacedEmail).setValue(data);

//        mDatabase.child("User List").child(uEmail).setValue(data)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Write was successful!
//                        // ...
//                        Toast.makeText(SignupActivity.this, "DB 등록 성공", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Write failed
//                        // ...
//                        Toast.makeText(SignupActivity.this, "DB 등록 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }


//    private void writeNewUser(String uEmail, String address, String carType) {
//        // Create new post at /user-posts/$userid/$postid and at
//        // /posts/$postid simultaneously
//        String key = mDatabase.child("User List").push().getKey();
//        UserData data = new UserData(uEmail, address, carType);
//        Map<String, Object> postValues = data.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/posts/" + key, postValues);
//        childUpdates.put("/user-posts/" + uEmail + "/" + key, postValues);
}