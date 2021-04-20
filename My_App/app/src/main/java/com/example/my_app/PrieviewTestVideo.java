package com.example.my_app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class PrieviewTestVideo extends AppCompatActivity {
    TextView token;
    Button buttonStop;
    VideoView TestvideoView;
    public String mVideoUrl;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prieview_test_video);
        token = findViewById(R.id.tokenNumVid);
        buttonStop = findViewById(R.id.Stop);
        TestvideoView = findViewById(R.id.TestvideoView);
        progress = findViewById(R.id.progressBar1);
        progress.setVisibility(View.VISIBLE);

        if(getIntent().hasExtra("vidUrl")){
            mVideoUrl = (getIntent().getExtras()).getString("vidUrl");
            Log.i("url : ", "GOT: "+mVideoUrl);
        }
        playVideo();
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void playVideo() {
        TestvideoView = findViewById(R.id.TestvideoView);
        Uri uri = Uri.parse(mVideoUrl);
        TestvideoView.setVideoURI(uri);
        TestvideoView.setMediaController(new MediaController(this));
        TestvideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progress.setVisibility(View.GONE);
                finish();
                return false;
            }
        });

        TestvideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progress.setVisibility(View.GONE);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,int arg2) {
                        progress.setVisibility(View.GONE);
                    }
                });
            }
        });

        TestvideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        TestvideoView.requestFocus();
        TestvideoView.start();
    }
}
