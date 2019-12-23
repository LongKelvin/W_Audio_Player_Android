package com.e.w_audio_player;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.*;
import com.acrcloud.rec.utils.ACRCloudLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RecognizeSongsActivity extends AppCompatActivity implements IACRCloudListener, IACRCloudRadioMetadataListener {

    private final String HOST_API = "identify-ap-southeast-1.acrcloud.com";
    private final String ACCESS_KEY = "a23047fba79dc36e36a9ee9fe708795f";
    private final String ACCESS_SECRET = "F4OcWt6bkerRzrQ6FsuQZ13BgHWWBAoZHHVNY45a";

    private final static String TAG = "RecognizeActivity";
    private TextView mVolume, mResult, tv_time, time, status_view;
    private TextView songTitle_tview, songArtist_tview, songReleaseDate_tview;
    private boolean mProcessing = false;
    private boolean mAutoRecognizing = false;
    private boolean initState = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    private final int PRINT_MSG = 1001;
    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;

    private String songTitle = ("");
    private String songAlbum = ("");
    private String songArtist = ("");
    private String youtubeVidID = ("");
    private String track_ID = ("");

    public static final String LOG = "THONG TIN DEBUG : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_song);

        if (haveNetworkConnection() == false) {
            findViewById(R.id.status_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.start).setVisibility(View.INVISIBLE);
            findViewById(R.id.auto_switch).setVisibility(View.INVISIBLE);
            findViewById(R.id.auto_switch_label).setVisibility(View.INVISIBLE);
        }
        verifyPermissions();

        path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud";
        Log.e(TAG, path);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        mVolume = (TextView) findViewById(R.id.volume);
        // mResult = (TextView) findViewById(R.id.result);
        tv_time = (TextView) findViewById(R.id.time);
        time = (TextView) findViewById(R.id.time);
        status_view = (TextView) findViewById(R.id.status);

        songTitle_tview = (TextView) findViewById(R.id.songTitle_tview);
        songArtist_tview = (TextView) findViewById(R.id.songArtitst_tview);
        songReleaseDate_tview = (TextView) findViewById(R.id.result_release_tview);
        LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);


        final Button btn_start = findViewById(R.id.start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
                btn_start.setVisibility(View.GONE);
                status_view.setVisibility(View.VISIBLE);
                findViewById(R.id.cancel).setVisibility(View.VISIBLE);
                LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
                result_layout.setVisibility(View.INVISIBLE);
            }
        });


        final Button btn_stop = findViewById(R.id.cancel);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                btn_stop.setVisibility(View.GONE);
                status_view.setVisibility(View.GONE);
                btn_start.setVisibility(View.VISIBLE);
                reset();
            }
        });

        findViewById(R.id.request_radio_meta).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                requestRadioMetadata();
            }
        });

        Switch sb = findViewById(R.id.auto_switch);
        sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    openAutoRecognize();
                    time.setText("");
                } else {
                    closeAutoRecognize();
                }
            }
        });


        this.mConfig = new ACRCloudConfig();

        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;

        // Please create project in "http://console.acrcloud.cn/service/avr".
        this.mConfig.host = HOST_API;
        this.mConfig.accessKey = ACCESS_KEY;
        this.mConfig.accessSecret = ACCESS_SECRET;

        // auto recognize access key
        this.mConfig.hostAuto = "";
        this.mConfig.accessKeyAuto = "";
        this.mConfig.accessSecretAuto = "";

        this.mConfig.recorderConfig.rate = 8000;
        this.mConfig.recorderConfig.channels = 1;

        // If you do not need volume callback, you set it false.
        this.mConfig.recorderConfig.isVolumeCallback = true;

        this.mClient = new ACRCloudClient();
        ACRCloudLogger.setLog(true);

        this.initState = this.mClient.initWithConfig(this.mConfig);
    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(this, "ACRCloud can't start", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            mVolume.setText("");
            // mResult.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                // mResult.setText("An error occurs when starting this feature!");
            }
            startTime = System.currentTimeMillis();
        }
    }

    public void cancel() {
        if (mProcessing && this.mClient != null) {
            this.mClient.cancel();
        }

        findViewById(R.id.cancel).setVisibility(View.GONE);
        findViewById(R.id.start).setVisibility(View.VISIBLE);
        findViewById(R.id.status).setVisibility(View.GONE);
        this.reset();
    }

    public void openAutoRecognize() {
        String str = this.getString(R.string.suss);
        tv_time.setText("0");
        if (!mAutoRecognizing) {
            mAutoRecognizing = true;
            if (this.mClient == null || !this.mClient.runAutoRecognize()) {
                mAutoRecognizing = true;
                str = this.getString(R.string.error);
            }
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void closeAutoRecognize() {
        String str = this.getString(R.string.suss);
        if (mAutoRecognizing) {
            mAutoRecognizing = false;
            this.mClient.cancelAutoRecognize();
            str = this.getString(R.string.error);
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    // callback IACRCloudRadioMetadataListener
    public void requestRadioMetadata() {
        String lat = "39.98";
        String lng = "116.29";
        List<String> freq = new ArrayList<>();
        freq.add("88.7");
        if (!this.mClient.requestRadioMetadataAsyn(lat, lng, freq,
                ACRCloudConfig.RadioType.FM, this)) {
            String str = this.getString(R.string.error);
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
    }

    public void reset() {
        tv_time.setText("");
        // mResult.setText("");
        mProcessing = false;
        mVolume.setText("");
    }

    @Override
    public void onResult(ACRCloudResult results) {
        this.reset();
        String track_id = "";

        String result = results.getResult();

        Log.e(LOG, "RESULT :: " + result);
        youtubeVidID = "";
        track_ID = "";
        String tres = "\n";

        Log.e(LOG, "START RECOGNIZING SONGS OK");

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            Log.e(LOG, "STATUS :  ");
            int j2 = j1.getInt("code");
            Log.e(LOG, String.valueOf(j2));


            if (j2 == PRINT_MSG) {

                songTitle_tview.setText(R.string.NotificationSongNULL);
                LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
                result_layout.setVisibility(View.VISIBLE);
                findViewById(R.id.songArtitst_tview).setVisibility(View.GONE);
                findViewById(R.id.result_release_tview).setVisibility(View.GONE);
                this.cancel();

            }
            if (j2 == 0) {
                Log.e(LOG, "IF STATUS OK -> TRUE");
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    Log.e(LOG, "METADATA LOADING MUSIC JSON_OBJECT OK");
                    for (int i = 0; i < 1; i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        songTitle = tt.getString("title");
                        String release_date = tt.getString("release_date");
                        Log.e(LOG, "METADATA LOADING TITLE JSON_OBJECT OK");
                        JSONArray artists = tt.getJSONArray("artists");
                        Log.e(LOG, "METADATA LOADING ARTIST OK");
                        JSONObject art = (JSONObject) artists.get(0);
                        songArtist = art.getString("name");
                        Log.e(LOG, "METADATA LOADING ARTIST NAME OK");

                        Log.e(LOG, "GET DATA OKAY");

                        try {

                            if (tt.has("external_metadata")) {
                                Log.e(LOG, " START GET EXTERNAL META_DATA :: STATUS OK ");
                                JSONObject external_metadata = tt.getJSONObject("external_metadata");
                                Log.e(LOG, " GET ETERNAL_METADATA OK ");
                                if (external_metadata.has("spotify")) {
                                    JSONObject spotify = (JSONObject) external_metadata.getJSONObject("spotify");
                                    Log.e(LOG, " GET SPOTIFY OBJECT OK ");
                                    JSONObject track = (JSONObject) spotify.getJSONObject("track");
                                    Log.e(LOG, " GET SPOTIFY  TRACK_ID OK ");
                                    track_ID = track.getString("id");
                                }
                                if (external_metadata.has("youtube")) {

                                    JSONObject youtube = (JSONObject) external_metadata.getJSONObject("youtube");
                                    Log.e(LOG, " START YOUTUBE VIDEO RECOGNIZING ");
                                    try {
                                        youtubeVidID = youtube.getString("vid");
                                        Log.e(LOG, " GET YOUTUBE VIDEO ID OK ");
                                    } catch (Exception ex) {
                                        Log.e(LOG, " JSON EXCEPTION :: START YOUTUBE VIDEO RECOGNIZING ");
                                        JSONObject vid = (JSONObject) youtube.getJSONObject("vid");
                                        Log.e(LOG, " GET YOUTUBE VID FILED OK ");
                                        youtubeVidID = vid.getString("vid");
                                        Log.e(LOG, " GET YOUTUBE VID FILED ID OKAY! ->> COMPLETED JSON ATTACH ");
                                    }
                                } else if (!external_metadata.has("youtube") && !external_metadata.has("spotify")) {
                                    youtubeVidID = "WE CAN NOT FIND ANY LINK FOR THIS SONGS";
                                    Toast.makeText(this, "WE CAN NOT FIND ANY LINK FOR THIS SONGS", Toast.LENGTH_SHORT).show();
                                }
                            }

                            songTitle_tview.setText("Title: " + songTitle);
                            songArtist_tview.setText("Artist: " + songArtist);
                            songReleaseDate_tview.setText("Release date: " + release_date);
                            LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
                            result_layout.setVisibility(View.VISIBLE);

                            tres = tres + (i + 1) + "\nCUSTOM RESULT:: \nTITLE :: " + songTitle + "\nARTIST :: " + songArtist + "\nSPOTIFY TRACK ID :: "
                                    + track_ID + "\nYOUTUBE VIDEO ID :: " + youtubeVidID + "\n\n";

                        } catch (Exception ex) {
                            Log.e(LOG, " ERROR OCCURS WHEN TRYING TO GET EXTERNAL META_DATA");
                        }
                    }
                }
                cancel();
                tres = tres + "\n\n";//+ result;
            } else {
                LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
                result_layout.setVisibility(View.VISIBLE);
                songTitle_tview.setText(R.string.NotificationSongNULL);
                findViewById(R.id.songArtitst_tview).setVisibility(View.GONE);
                findViewById(R.id.result_release_tview).setVisibility(View.GONE);
                this.cancel();
            }
        } catch (JSONException e) {
            LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
            result_layout.setVisibility(View.VISIBLE);
            songTitle_tview.setText(R.string.NotificationSongNULL);
            findViewById(R.id.songArtitst_tview).setVisibility(View.GONE);
            findViewById(R.id.result_release_tview).setVisibility(View.GONE);
            e.printStackTrace();
        }

        //  mResult.setText(tres);
        startTime = System.currentTimeMillis();

    }


    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        mVolume.setText(getResources().getString(R.string.volume) + volume + "\n\nTime: " + time + " s");
        tv_time.setText("" + time + " s");


    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };

    public void verifyPermissions() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS[i]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS,
                        REQUEST_EXTERNAL_STORAGE);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("RecognizeSongs", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }

    @Override
    public void onRadioMetadataResult(String s) {
        mResult.setText(s);
    }


    public void songsClick(View view) {

        LinearLayout result_layout = (LinearLayout) findViewById(R.id.result_layout);
        result_layout.setVisibility(View.VISIBLE);
        result_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "";
                Log.e(LOG, " START OPEN TRACK IN BROWSER");
                if (!track_ID.isEmpty()) {
                    URL = "https://open.spotify.com/track/";
                    URL = URL + track_ID;
                } else if (!youtubeVidID.isEmpty()) {
                    URL = "https://www.youtube.com/watch?v=";
                    URL = URL + youtubeVidID;
                }


                Log.e(LOG, "GET URL VALUE -> STRING ::");
                Log.e(LOG, URL);

                Log.e(LOG, "GET EVENT CLICK ON BTN_BROWSER OK");

                {
                    Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                    Log.e(LOG, "GET INTENT OK");
                    Log.e(LOG, "START BROWSER ACTIVITY OK");
                    startActivity(implicit);
                    Log.e(LOG, "COMPLETED");
                }
            }
        });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
