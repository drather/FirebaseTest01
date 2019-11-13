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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;


    String ID = "";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//    //notification01
//    NotificationManager notificationManager;
//    NotificationChannel notificationChannel;
//    PendingIntent intent1;
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    //notification01 여기까지
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //notification2
//        notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
//        intent1 = PendingIntent.getActivity(this, 0,
//                new Intent(getApplicationContext(), NotificationTestActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification.Builder builder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.logo2) // 아이콘 설정하지 않으면 오류남
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setContentTitle("제발 떠라") // 제목 설정
//                .setContentText("제발 떠라") // 내용 설정
//                .setTicker("제발 떠라") // 상태바에 표시될 한줄 출력
//                .setAutoCancel(true)
//                .setContentIntent(intent1);
//        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());
//        //notification2 여기까지


        //notification03 시작
        NotificationSomethings();

        Button btn_temperature =  findViewById(R.id.btn_temperature);
        Button btn_showProfile = findViewById(R.id.btn_showProfile);
        Button btn_camera = findViewById(R.id.btn_camera);
        TextView textView_test2 = findViewById(R.id.textView_test2);

        Button btn_makeNotification = findViewById(R.id.btn_makeNotification);

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

        btn_makeNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 버튼을 누를때마다 count 를 증가시며 최근에 보낸 노티피케이션만 사용자의 탭 대기중인지 테스트
                count++;
                NotificationSomethings();
            }
        });
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

        public void NotificationSomethings() {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, NotificationTestActivity.class);
        notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }
}

