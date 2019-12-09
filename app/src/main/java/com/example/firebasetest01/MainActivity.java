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
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.firebasetest01.TemperatureActivity.NOTIFICATION_CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    final private String TAG = "Main_Activity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;
    Geocoder geocoder;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final int COUNT_TIME = 60000;
    private int count = 0;

    private boolean firstWarning = false;
    private boolean secondWarning = false;
    private boolean thirdWarning = false;


    String temperatureFromDB = "";
    String motionFromDB = "";
    String ID = "";
    Intent intent;

    Timer timer = new Timer();

    CountDownTimer cdtimer = null;
    boolean isTimerEnded;

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
                // firebase로부터 계속 motion값을 수신해서 위험 여부를 판단하는 부분이다.
                motionFromDB = dataSnapshot.getValue().toString();
                Log.d(TAG, "motionFromDB: " + motionFromDB);

                // DB로부터 읽어온 모션값이 false인 경우
                if (motionFromDB.equals("false")) {
                    // firstWarning이 갔는지 안갔는지 판단하는 조건문
                    if (firstWarning == true) {
                        // 타이머 남은 시간 체크하는 조건문
                        if (isTimerEnded == true) {
                            // 시간 남아있지 않았으면,
                            initWarning();
                            Log.d("FirstWarning", "fw = " + firstWarning);
                        } else {
                            // 시간 남아있으면, pass
                        }
                    }

                    // DB로부터 읽어온 값이 True인 경우
                } else if (motionFromDB.equals("true")) {
                    // 1차경고 처음인지 아닌지 판단하는 조건문, 첫번째 경고가 아니라면
                    if (firstWarning == true) {
                        giveFirstWarning();

                        Log.d("timer", "타이머 초기화 후 재생성");
                        cdtimer.cancel();
                        //isTimerEnded = false;
                        cdtimer = new CountDownTimer(COUNT_TIME, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.d("timer", "재생성된 타이머 작동중");
                                Log.d("timer", "재생성 fw = " + firstWarning);
                            }

                            @Override
                            public void onFinish() {
                                Log.d("timer", "재생성된 타이머 종료");
                                isTimerEnded = true;
                                initWarning();
                            }
                        }.start();
                    }

                    // 1차 경고가 처음인 경우
                    else {
                        firstWarning = true;
                        isTimerEnded = false;

                        giveFirstWarning();

                        // 타이머 생성, 실행
                        Log.d("timer 생성", "타이머 생성됨");
                        cdtimer = new CountDownTimer(COUNT_TIME, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.d("timer", "생성된 타이머 작동중");
                                Log.d("timer", "생성 fw = " + firstWarning);
                            }

                            @Override
                            public void onFinish() {
                                Log.d("timer", "생성된 타이머 시간 다 돼서 종료");
                                isTimerEnded = true;
                                initWarning();
                            }
                        }.start();
                    }

                } else {
                    // 아직 연결 안됨
                    // 구현할 필요 없음
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
                    if (checkTemperature(temperatureFromDB) == 2 && firstWarning) {
                        giveSecondWarning();
                    }
                    // 여전히 움직임 감지되고, 온도가 40도 이상인 경우 3번째 경고
                    else if (checkTemperature(temperatureFromDB) == 3 && firstWarning) {
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
        notificationIntent.putExtra("notificationId", count); //전달할 값MaMaasd
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.application_icon)) //BitMap 이미지 요구
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
        } else
            return false;
    }


    public int checkTemperature(String temperature) {
        float temp = Float.parseFloat(temperature);
        if (temp < 20.00) {
            return 1;
        } else if (temp < 24.00)
            return 2;
        else
            return 3;
    }


    public void giveFirstWarning() {
        firstWarning = true;
        notifySomething("차량 내부에 움직임이 감지되었습니다.");
    }

    public void giveSecondWarning() {
        secondWarning = true;
        notifySomething("차량 내부에 움직임이 감지된 상태에서, 차량 내부 온도가 30도까지 상승했습니다.");
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
        String destPhoneNum = "01024075776";

        String name = userData.getName();
        String carType = userData.getCarType();
        String carNum = userData.getCarNum();
        //double lat = Double.parseDouble(userData.getLat());
        //double lon = Double.parseDouble(userData.getLon());
        double lat = 37.284374;
        double lon = 127.044471;

        String location = "";
        location = getAddress(this, lat, lon);

        String msg1 = "어반세이프]" + name + "님의 차량 내부에 어린이/반려견이 높은 온도에 방치되어 있습니다.";
        String msg2 = "차종: " + carType + "\n" +
                "차량 번호: " + carNum + "\n" +
                "현재 차량 내부 온도: " + temperatureFromDB;
        String msg3 = "현재 위치: " + location + "\n";
        if (!TextUtils.isEmpty(msg1) && !TextUtils.isEmpty(destPhoneNum)) {
            if (checkPermission()) {
                //Get the default SmsManager//
                SmsManager smsManager = SmsManager.getDefault();
                //Send the SMS//
                smsManager.sendTextMessage(destPhoneNum, null, msg1, null, null);
                smsManager.sendTextMessage(destPhoneNum, null, msg2, null, null);
                smsManager.sendTextMessage(destPhoneNum, null, msg3, null, null);

                Toast.makeText(MainActivity.this, "신고 메시지를 전송했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied at main", Toast.LENGTH_SHORT).show();
            }
        } //문자 여기까지
    }

    public String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;
                }
            }

        } catch (Exception e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return nowAddress;
    }

    @Override
    public void onBackPressed() {

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