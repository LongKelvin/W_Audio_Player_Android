package com.e.w_audio_player;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.w_audio_player.ListSongs.RecyclerItemClickListener;
import com.e.w_audio_player.ListSongs.SongsAdapter;
import com.e.w_audio_player.ListSongs.SongsManager;
import com.e.w_audio_player.MusicPlayer.MusicPlayerFragment;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    SongsAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view_2);
        adapter= new SongsAdapter();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                recyclerView.setAdapter(adapter);
                return true;
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever


                    }
                })
        );
    }
    public void initView(){

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SongsManager songsManager = new SongsManager();
        SongsAdapter songAdapter = new SongsAdapter(songsManager.getPlayList());
        recyclerView.setAdapter(songAdapter);
    }
}
