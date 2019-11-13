package com.example.firebasetest01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;
    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;

    private String email = "";
    private String password = "";

    //로그인 유지 위해 수정한 부분
    private SharedPreferences appData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        // 아이디와 패스워드 받는 부분
        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);
        // 저장하기 위해 sharedPreferences 받는 부분
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        String id = sf.getString("id","");
        String pw = sf.getString("pw", "");

        editTextEmail.setText(id);
        editTextPassword.setText(pw);

        //수정한 부분
        Button btn_signUp = findViewById(R.id.btn_signUp);

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //intent로 값을 주고받는 예
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String inputId = editTextEmail.getText().toString();
        String inputPW = editTextPassword.getText().toString();
        editor.putString("id", inputId);
        editor.putString("pw", inputPW);

        editor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        finish();
    }

    //로그인하는 메소드, 이 밑으로는 회원가입 관련임. 따라서, 옮기는게 좋을듯
    public void signIn(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            loginUser(email, password);
        }
    }

    // 로그인
    private void loginUser(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            // 값 넘기기
                            intent.putExtra("userEmail", editTextEmail.getText().toString());
                            startActivity(intent);

                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 유효성 검사
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

//    // 이메일, 비밀번호 모두 유효한 경우 진행되는 회원가입 메소드
//    private void createUser(String email, String password) {
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // 회원가입 성공
//                            Toast.makeText(LoginActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 회원가입 실패
//                            Toast.makeText(LoginActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
}