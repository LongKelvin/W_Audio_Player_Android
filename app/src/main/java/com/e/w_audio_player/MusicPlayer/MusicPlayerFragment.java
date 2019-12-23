package com.e.w_audio_player.MusicPlayer;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.e.w_audio_player.ListSongs.SongsManager;
import com.e.w_audio_player.MainActivity;
import com.e.w_audio_player.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{


    public MusicPlayerFragment() {
        // Required empty public constructor
    }


    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private ImageButton btnPlayList;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private  MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    Handler handler = new Handler();




    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 1000;
    private int seekBackwardTime = 1000;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.player, container, false);
//       // All player button
        btnPlay = view.findViewById(R.id.btnPlay);
        btnForward = view.findViewById(R.id.btnForward);
        btnBackward = view.findViewById(R.id.btnBackward);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnRepeat = view.findViewById(R.id.btnRepeat);
        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnPlayList = view.findViewById(R.id.btnPlaylist);
        songProgressBar = view.findViewById(R.id.songProgressBar);
        songTitleLabel = view.findViewById(R.id.songTitle);
        songCurrentDurationLabel = view.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = view.findViewById(R.id.songTotalDurationLabel);

        utils = new Utilities();
        mp = ((MainActivity) getActivity()).getMediaPlayer();
        songManager = new SongsManager(); // thông tin bài hát
        // Listener
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        // Load toàn bộ bài hát lên
        songsList = new ArrayList<>();
        songsList = songManager.getPlayList();
        Log.e("currentSongIndex1", String.valueOf(currentSongIndex));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mp == null)
            return;


        Log.e("currentSongIndex2", String.valueOf(currentSongIndex));
        if(((MainActivity) getActivity()).IsChange() == true)
            try {
                currentSongIndex =((MainActivity) getActivity()).getPos();
                Log.e("currentSongIndex2", String.valueOf(currentSongIndex));
                if(currentSongIndex != -1) {
                    playSong(currentSongIndex);
                }
                ((MainActivity) getActivity()).ResetChange();
            }catch (Exception e){
                e.printStackTrace();
        }
        if(mp.isPlaying()){
            btnPlay.setImageResource(R.drawable.btn_pause);

        }else{
            // Resume song
            {
                btnPlay.setImageResource(R.drawable.btn_play);
            }
        }

        btnPlayList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                ((MainActivity) getActivity()).getTabLayout().getTabAt(0).select();

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    mp.pause();
                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.btn_play);

                }else{
                    // Resume song
                    {
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });
        // kéo đoạn nhạc đi một khoảng thời gian
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // lấy vị trí hiện tại trên thanh
                int currentPosition = mp.getCurrentPosition();
                // kiểm tra nếu kéo nữa thì hết bài chưa
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // kéo tới vị trì kế tiếp
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // kéo tới hết bài
                    mp.seekTo(mp.getDuration());
                }
            }
        });



        // kéo đoạn nhạc trở lại một khoảng thời gian
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // lấy vị trí hiện tại trên thanh
                int currentPosition = mp.getCurrentPosition();
                // kiểm tra nếu kéo nữa thì về bắt đầu chưa
                if(currentPosition - seekBackwardTime >= 0){
                    // kéo về vị trì kế tiếp
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // kéo về bắt đầu
                    mp.seekTo(0);
                }

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Kiểm tra có bài hát kế tiếp ko, có thì chọn, ko thì về bài đầu tiên
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Kiểm tra có bài hát trước đó ko, có thì chọn, ko thì tới bài cuối
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    //lặp lại bài hát đang nghe
                    isRepeat = true;
                    Toast.makeText(getContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle thành false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
    }


    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };
    public void  playSong(int songIndex){
        // Play song
        if(mp==null)
            return;
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar


            updateProgressBar();

            //updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mp == null)
            return;
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }


}
