package com.example.firebasetest01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    String ID = "";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//    UserData user = new UserData();

//    TextView textView_test2 = findViewById(R.id.textView_test2);
//    TextView textView_test3 = findViewById(R.id.textView_test3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn_temperature =  findViewById(R.id.btn_temperature);
        Button btn_showProfile = findViewById(R.id.btn_showProfile);
        Button btn_camera = findViewById(R.id.btn_camera);
        TextView textView_test2 = findViewById(R.id.textView_test2);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            ID = intent.getExtras().getString("userEmail");

            ID = ID.replace('@', '_');
            ID = ID.replace('.', '_');
            textView_test2.setText(ID);
        }
        else {
            Log.d("mainActivity", "intent value is null");
        }

//       UserData currentUser = new UserData();

        //여기부터 데이터 읽어오는 부분
//        databaseReference.child("User List").child(tempID).child("userEmail").addListenerForSingleValueEvent(new ValueEventListener() {
        databaseReference.child("User List").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //이 밑에 5줄은 getValue()시험
                Log.d("userEmail,Main", "user name is: " +"not yet");
                UserData userDataFromDB = dataSnapshot.getValue(UserData.class);
//                TextView textView_test1 = findViewById(R.id.textView_test1);
//                textView_test1.setText(userDataFromDB.getUserEmail());
//
//                TextView textView_test2 = findViewById(R.id.textView_test2);
                TextView textView_test3 = findViewById(R.id.textView_test3);
                textView_test3.setText(userDataFromDB.getName() + "님 안녕하세요?");
//                user = userDataFromDB;
//
//                textView_test1.setText(user.getUserEmail());
//                textView_test2.setText(user.getName());
//                textView_test3.setText(user.getPhoneNum());


//                Log.d("mainActivity", "user email is: " + user.getUserEmail());
//                Log.d("mainActivity", "user address is: " + user.getCarNum());
//                Log.d("mainActivity", "user carType is: " + user.getName());

//                //이 밑에 3줄은 일단 되는거
//                String data = dataSnapshot.getValue().toString();
//                TextView textView_data = findViewById(R.id.textView_data);
//                textView_data.setText(data);

//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("MainActivity", "ValueEventListener : " + snapshot.getValue());
//                    TextView textView_data = findViewById(R.id.textView_data);
//                    textView_data.setText(snapshot.getValue().toString());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // 여기까지

        btn_temperature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TemperatureActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });


        btn_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });


        btn_showProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        //fcm
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.w("FCM Log", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("FCM log", "FCM 토큰: " + token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void runtimeEnableAutoInut(){
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }
    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
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
