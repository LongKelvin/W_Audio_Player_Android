package com.e.w_audio_player;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StartupScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

    }
    //Xin quyền truy cập từ người dùng
    private static final String[] PERMISIONS={Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISIONS =12345;
    private static final int PERMISIONS_COUNT =1;
    @SuppressLint("NewApi")
    private boolean arePermisionDenied(){
        for(int i = 0; i<PERMISIONS_COUNT;i++){
            if(checkSelfPermission(PERMISIONS[i]) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(arePermisionDenied()){
            ((ActivityManager)(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        }else{
            onResume();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermisionDenied()) {
            requestPermissions(PERMISIONS, REQUEST_PERMISIONS);
        }

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(StartupScreenActivity.this, MainActivity.class));
                finish();
            }
        }, secondsDelayed * 3000);
    }
}
