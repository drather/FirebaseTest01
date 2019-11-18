package com.example.firebasetest01;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    final private String TAG = "Main_Activity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;


    String ID = "";
    Intent intent;

    //탈퇴 시 인증 제거 위해 수정할 것
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ImageView_btn1_temperature = (ImageView) findViewById(R.id.ImageView_btn1_temperature);
        ImageView ImageView_btn2_camera = (ImageView) findViewById(R.id.ImageView_btn2_camera);
        ImageView ImageView_btn3_profile = (ImageView) findViewById(R.id.ImageView_btn3_profile);
        Button btn4_logOut = (Button) findViewById(R.id.btn4_logOut);
        Button btn5_signOut = (Button) findViewById((R.id.btn5_signOut));

        //
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            Log.d(TAG, user.getUid() + "사용자 로그인됨");
//        } else {
//            // No user is signed in
//            Log.d(TAG, "사용자 로그인 안됨");
//        }

        intent = getIntent();
        ID = intent.getStringExtra("userEmail");
        ID = ID.replace('@', '_');
        ID = ID.replace('.', '_');

        Log.d("MAIN_ACTIVITY", "userEmail: " + ID);

        ImageView_btn1_temperature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, TemperatureActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        ImageView_btn2_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        ImageView_btn3_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        //로그아웃 구현해본거입니다~~~~~~~~~~~~~
        btn4_logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alBuilder = new AlertDialog.Builder(MainActivity.this);
                alBuilder.setMessage("로그아웃 하시겠습니까?");

                // "예" 버튼을 누르면 실행되는 리스너
                alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        //
                        if (user != null) {
                            // User is signed in
                            auth.signOut();
                            Log.d(TAG, "사용자 로그아웃 안됨");

                        } else {
                            // No user is signed in
                            Log.d(TAG, "사용자 로그아웃 됨");
                        }
                        //
                        Toast.makeText(MainActivity.this, "성공적으로 로그아웃하였습니다",
                                Toast.LENGTH_SHORT).show();
                        finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)

                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

                // "아니오" 버튼을 누르면 실행되는 리스너
                alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });
                alBuilder.setTitle("로그아웃");
                alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
            }
        });

        //탈퇴 메소드
        //DB에 정보 다 지우고, 인증도 앞으로 못하게 된다. 완성
        btn5_signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alBuilder = new AlertDialog.Builder(MainActivity.this);
                alBuilder.setMessage("탈퇴하시겠습니까?");

                // "예" 버튼을 누르면 실행되는 리스너
                alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("User List").child(ID).removeValue();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    Toast.makeText(MainActivity.this, "성공적으로 탈퇴하였습니다",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

                // "아니오" 버튼을 누르면 실행되는 리스너
                alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });
                alBuilder.setTitle("탈퇴");
                alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
            }
        });

    }






    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("로그아웃 하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                firebaseAuth.signOut();

                //
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, user.getUid() + "사용자 로그아웃 안됨");
                } else {
                    // No user is signed in
                    Log.d(TAG, "사용자 로그아웃 됨");
                }
                //
                Toast.makeText(MainActivity.this, "성공적으로 로그아웃하였습니다",
                        Toast.LENGTH_SHORT).show();
                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)

                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
    }
}

