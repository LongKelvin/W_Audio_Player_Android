package com.e.w_audio_player.ListSongs;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    // Constructor
    public SongsManager(){

    }

    public ArrayList<HashMap<String, String>> getPlayList(){
        songsList.clear();
        // lấy file nhạc từ DIRECTORY download và DIRECTORY music
        addMusicFileFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC )));
        addMusicFileFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS )));
        addMusicFileFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES )));

        return songsList;
    }

    private void addMusicFileFrom(String dirPath){

        final File musicDir = new File(dirPath);
        if(!musicDir.exists()){
            musicDir.mkdir();
            return;
        }
        final File[] files = musicDir.listFiles();
        for(File file : files){
            HashMap<String, String> song = new HashMap<String, String>();
            song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
            song.put("songPath", file.getPath());

            // Add file mp3
            if(file.getPath().endsWith(".mp3") || file.getPath().endsWith(".wav")){
                songsList.add(song);
            }

        }
    }
    public int getIndex(String dataPath){
        int i = -1;
        dataPath = dataPath.substring(0, (dataPath.length() - 4));
        SongsManager songsManager = new SongsManager();
        dataPath = dataPath.toLowerCase();
        for(HashMap<String, String> song: songsManager.getPlayList()){
            i++;
            Log.v("songIndex __ test", String.valueOf(i));
            String temp = song.get("songPath").substring(0, (song.get("songPath").length() - 4));
            Log.v("songIndex __ test", temp);
            Log.v("songIndex __ test", dataPath);
            if(temp.toLowerCase().contains(dataPath)){
                return i;
            }
        }
        return -1;
    }
}
