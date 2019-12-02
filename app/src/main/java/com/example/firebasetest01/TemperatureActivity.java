package com.example.firebasetest01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TemperatureActivity extends AppCompatActivity {
    final private String TAG = "Temperature_Activity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    public static final int PERMISSION_REQUEST_CODE= 1;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent = getIntent();
        String ID = intent.getStringExtra("userEmail");
        Log.d(TAG, "userEmail: " + ID);

//        // 퍼미션 받는 부분
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkPermission()) {
//                Log.e("permission", "Permission already granted.");
//            } else {
//                requestPermission();
//            }
//        }

        databaseReference.child("User List").child(ID).child("temperature").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dataFromDB = dataSnapshot.getValue().toString();
                TextView textView_temperature = findViewById(R.id.textView_temperature);
                textView_temperature.setText("\n\n" + dataFromDB + "도");

                if (dataFromDB.equals("Not Connected")) {
                    textView_temperature.setText("\n\n" + "아직 연결되지 않았습니다");
                }

                else {
                    textView_temperature.setText("\n\n" + dataFromDB + "도");
//                    float data = Float.parseFloat(dataFromDB);
//                    if (data > 5) {
//                        notifySomething("차 내 온도가 너무 높습니다");
//
////                        // 문자 여기부터
////                        String sms = "온도 너무높아";
////                        String phoneNum = "01024075776";
////
////                        if(!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(phoneNum)) {
////                            if(checkPermission()) {
////                                //Get the default SmsManager//
////                                SmsManager smsManager = SmsManager.getDefault();
////                                //Send the SMS//
////                                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
////                            }else {
////                                Toast.makeText(TemperatureActivity.this, "Permission denied at main", Toast.LENGTH_SHORT).show();
////                            }
////                        } //문자 여기까지
//                    }
                }
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
//                    notifySomething("차 내에 움직임이 감지되고 있습니다");
            }
                else {
                    textView_motion.setText("아직 연결되지 않았습니다");
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


//    public void notifySomething(String msg) {
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent notificationIntent = new Intent(this, NotificationTestActivity.class);
//        notificationIntent.putExtra("notificationId", count); //전달할 값
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) //BitMap 이미지 요구
//                .setContentTitle(msg) //상태바 드래그시 보이는 타이틀
//                .setContentText(msg) // 상태바 드래그시 보이는 서브타이틀
//                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
//                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
//                .setAutoCancel(false);
//
//        //OREO API 26 이상에서는 채널 필요
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
//            CharSequence channelName  = "노티페케이션 채널";
//            String description = "오레오 이상을 위한 것임";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
//            channel.setDescription(description);
//
//            // 노티피케이션 채널을 시스템에 등록
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//
//        }
//        else
//            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
//
//        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
//    }

    /////////////////////////////////////////////////////////
    // 이 밑으로는 문자메시지 전송을 위한 메소드 들어갈 것 //
    /////////////////////////////////////////////////////////


//    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(TemperatureActivity.this, Manifest.permission.SEND_SMS);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(TemperatureActivity.this,
//                            "Permission accepted", Toast.LENGTH_LONG).show();
//
//                } else {
//                    Toast.makeText(TemperatureActivity.this,
//                            "Permission denied at function", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }
}
