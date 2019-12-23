package com.e.w_audio_player;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.e.w_audio_player.ListSongs.PagerAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    public static final String LOG ="THONG TIN DEBUG : ";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tabItemSong;
    private TabItem tabItemArtist;
    private TabItem tabItemAlbum;
    public PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MakeTransparentStatusBar();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tabLayout = findViewById(R.id.tabLayout);
        tabItemSong = findViewById(R.id.tabItem_songs);
        tabItemArtist = findViewById(R.id.tabItem_artists);
        tabItemAlbum = findViewById(R.id.tabItem_album);
        viewPager = findViewById(R.id.viewpager);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 2){
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.setting) {
            Toast.makeText(MainActivity.this, "Action Settings clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        if(id == R.id.equalizer){
            Toast.makeText(MainActivity.this, "Action EQ click", Toast.LENGTH_LONG).show();
            return true;
        }

        if(id == R.id.action_search){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);

        }
        if(id == R.id.recognizeSongs){
            Intent intent = new Intent(this,RecognizeSongsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }



}
