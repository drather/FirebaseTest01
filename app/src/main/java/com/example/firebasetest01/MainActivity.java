package com.example.firebasetest01;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

import java.util.Timer;

import static com.example.firebasetest01.TemperatureActivity.NOTIFICATION_CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    final private String TAG = "Main_Activity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;

    private boolean firstWarning = false;
    private boolean secondWarning = false;
    private boolean thirdWarning = false;

    String temperatureFromDB = "";
    String motionFromDB = "";
    String ID = "";
    Intent intent;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ImageView_btn1_temperature = (ImageView) findViewById(R.id.ImageView_btn1_temperature);
        ImageView ImageView_btn2_camera = (ImageView) findViewById(R.id.ImageView_btn2_camera);
        ImageView ImageView_btn3_profile = (ImageView) findViewById(R.id.ImageView_btn3_profile);
        ImageView btn4_logOut = (ImageView) findViewById(R.id.btn4_logOut);

        intent = getIntent();
        ID = intent.getStringExtra("userEmail");
        ID = ID.replace('@', '_');
        ID = ID.replace('.', '_');

        // 온도 확인과 리스너
        ImageView_btn1_temperature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, TemperatureActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        // 차량 내부 확인과 리스너
        ImageView_btn2_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        // 사용자 정보 버튼과 리스너
        ImageView_btn3_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userEmail", ID);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼과 리스너
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
        // 모션 값 받아오는 부분
        databaseReference.child("User List").child(ID).child("motion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                motionFromDB = dataSnapshot.getValue().toString();
                Log.d(TAG, "motionFromDB: " + motionFromDB);

                if (motionFromDB.equals("false")) {
                    // 인체 감지 X
                    // initWarning();
                } else if (motionFromDB.equals("true")) {
                    // 인체가 감지된 경우 첫번째 경고
                    giveFirstWarning();
                } else {
                    // 아직 연결 안됨
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // 온도 값 받아오는 부분
        databaseReference.child("User List").child(ID).child("temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temperatureFromDB = dataSnapshot.getValue().toString();
                if (temperatureFromDB.equals("Not Connected")) {
                    // 아직 연결 안됨
                } else {
                    // 여전히 움직임 감지되고, 온도가 30 ~ 40인 경우 2번째 경고
                    if (motionFromDB.equals("true") && checkTemperature(temperatureFromDB) == 2  && firstWarning) {
                        giveSecondWarning();
                    }
                    // 여전히 움직임 감지되고, 온도가 40도 이상인 경우 3번째 경고
                    else if (motionFromDB.equals("true") && checkTemperature(temperatureFromDB)== 3 && secondWarning) {
                        giveThirdWarning();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void notifySomething(String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, NotificationTestActivity.class);
        notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) //BitMap 이미지 요구
                .setContentTitle("어반세이프") //상태바 드래그시 보이는 타이틀
                .setContentText(msg) // 상태바 드래그시 보이는 서브타이틀
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(false);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        } else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkMotion(String motion) {
        if (motion.equals("true")) {
            return true;
        }
        else
            return false;
    }

    public int checkTemperature(String temperature) {
        float temp = Float.parseFloat(temperature);
        if (temp < 30.0) {
            return 1;
        }
        else if (temp < 40.0)
            return 2;
        else
            return 3;
    }


    public void giveFirstWarning() {
        notifySomething("차량 내부에 움직임이 감지되었습니다.");
        firstWarning = true;
    }

    public void giveSecondWarning() {
            notifySomething("차량 내부에 움직임이 감지된 상태에서, 차량 내부 온도가 30도까지 상승했습니다.");
            secondWarning = true;
    }

    public void giveThirdWarning() {
        thirdWarning = true;
        databaseReference.child("User List").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            // UserData 정보 받아오기
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData ud = dataSnapshot.getValue(UserData.class);
                sendSMS(ud);
                notifySomething("차량 내 온도가 위험수준에 도달하여 신고 메시지가 119에 전송되었습니다.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void initWarning() {
        firstWarning = false;
        secondWarning = false;
        thirdWarning = false;
    }



    public void sendSMS(UserData userData) {
        // 어반세이프] 차량 내부 온도가 위험수준에 도달하여 ' + gps + carType + 차량번호 + carNum + 신고 문자가 119에 전송되었습니다.
        String name = userData.getName();
        String carType = userData.getCarType();
        //String gps = userData.getGps();
        String gps = "경기도 수원시 영통구 월드컵로 206";
        String carNum = userData.getCarNum();

        String destPhoneNum = "01024075776";
//
        String msg1 = "어반세이프]" + name + "님의 차량 내부에 어린이/반려견이 높은 온도에 방치되어 있습니다.";
//        String msg1 = "어반세이프]" + gps + " 에 " + carType + " 차량번호 " + carNum + " 에 아이/반려견이 높은 온도에 방치되어 있습니다.";

        String msg2 = "현재 위치: " + gps + "\n" +
                "차종: " + carType + "\n" +
                "차량 번호; " + carNum + "\n" +
                "현재 차량 내부 온도: " + temperatureFromDB;

        if(!TextUtils.isEmpty(msg1) && !TextUtils.isEmpty(destPhoneNum)) {
            if(checkPermission()) {
                //Get the default SmsManager//
                SmsManager smsManager = SmsManager.getDefault();
                //Send the SMS//
                smsManager.sendTextMessage(destPhoneNum, null, msg1, null, null);
                smsManager.sendTextMessage(destPhoneNum, null, msg2, null, null);

                Toast.makeText(MainActivity.this, "신고 메시지를 전송했습니다.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Permission denied at main", Toast.LENGTH_SHORT).show();
            }
        } //문자 여기까지
    }

//    //뒤로가기 눌렀을 떄 무슨 행동 할지인데, 없어도 되지 싶다.
//    @Override
//    public void onBackPressed() {
//        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
//        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
//        alBuilder.setMessage("로그아웃 하시겠습니까?");
//
//        // "예" 버튼을 누르면 실행되는 리스너
//        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                firebaseAuth.signOut();
//                //
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, user.getUid() + "사용자 로그아웃 안됨");
//                } else {
//                    // No user is signed in
//                    Log.d(TAG, "사용자 로그아웃 됨");
//                }
//                //
//                Toast.makeText(MainActivity.this, "성공적으로 로그아웃하였습니다",
//                        Toast.LENGTH_SHORT).show();
//                finish(); // 현재 액티비티를 종료한다. (MainActivity에서 작동하기 때문에 애플리케이션을 종료한다.)
//
//                intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//        // "아니오" 버튼을 누르면 실행되는 리스너
//        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                return; // 아무런 작업도 하지 않고 돌아간다
//            }
//        });
//        alBuilder.setTitle("프로그램 종료");
//        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
//    }
}