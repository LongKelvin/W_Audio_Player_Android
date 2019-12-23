package com.e.w_audio_player.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.e.w_audio_player.ListSongs.SongsManager;
import com.e.w_audio_player.MainActivity;
import com.e.w_audio_player.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.e.w_audio_player.Notification.App.CHANNEL_ID_1;

public class MusicService extends Service {
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    //private MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        SongsManager plm = new SongsManager();
        songsList = plm.getPlayList();
        //mp = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int input = Integer.parseInt(intent.getStringExtra("songIndex"));

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(songsList.get(input).get("songTitle"))
                .addAction(R.drawable.ic_skip_previous, "prev", null)
                .addAction(R.drawable.ic_pause, "pause", null)
                .addAction(R.drawable.ic_skip_next, "next", null)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(0,1,2))
                .build();
        Log.v("helooo1111111111",songsList.get(input).get("songPath"));

        startForeground(1, notification);
        Log.v("helooo222222222222",songsList.get(input).get("songPath"));

        //playSong(input);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mp.stop();
    }
    /*public void  playSong(int songIndex){
        if(mp == null)
            return;
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            Log.v("helooo",songsList.get(songIndex).get("songPath"));
            // Displaying Song title
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}