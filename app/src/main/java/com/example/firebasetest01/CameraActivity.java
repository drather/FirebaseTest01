package com.example.firebasetest01;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CameraActivity extends AppCompatActivity {

    private WebView mWebView;
    //private final String camAddr = "http://google.com";
    private final String camAddr = "http://172.20.10.10/stream";
    private final String customNoNetworkHTML = "<html><body style=\"padding:15px; text-align:center; font-size: 24px \">Go to wifi settings and chose CamRide to connect <br><br><br> <input type=\"button\" style=\"width: 80%; padding: 10px;\" value=\"Refresh App\" onClick=window.location.replace('" + camAddr + "');></body></html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mWebView = (WebView) findViewById(R.id.webView);

        //adding custom error page (for when wifi is off)
        /*mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Log.i("BOBO## WEB_VIEW_TEST", "error code:" + errorCode);
                //Toast.makeText(getApplicationContext(), "Error occured, please check newtwork connectivity", Toast.LENGTH_SHORT).show();
                view.loadData(customNoNetworkHTML, "text/html", "UTF-8");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });*/

        //WebSettings webSettings = mWebView.getSettings();
        // webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(camAddr);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}