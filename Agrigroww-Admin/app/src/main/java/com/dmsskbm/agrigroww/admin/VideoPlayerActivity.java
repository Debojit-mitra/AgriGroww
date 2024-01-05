package com.dmsskbm.agrigroww.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    String videoURL;
    StyledPlayerView videoPlayer;
    ExoPlayer exoPlayer;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoURL = getIntent().getStringExtra("videoURL");

        videoPlayer = findViewById(R.id.videoPlayer);
        progressBar = findViewById(R.id.progressBar);

        videoPlayer.setShowFastForwardButton(false);
        videoPlayer.setShowRewindButton(false);
        videoPlayer.setShowNextButton(false);
       videoPlayer.setShowPreviousButton(false);
        videoPlayer.setControllerHideOnTouch(true);
        exoPlayer = new ExoPlayer.Builder(VideoPlayerActivity.this).build();
        videoPlayer.setPlayer(exoPlayer);
        com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(VideoPlayerActivity.this, Util.getUserAgent(VideoPlayerActivity.this, "app"));
        MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(videoURL)));
        exoPlayer.prepare(audioSource);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState == ExoPlayer.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        exoPlayer.setPlayWhenReady(true);





    }

    @Override
    public void onBackPressed() {
        exoPlayer.release();
        finish();
        super.onBackPressed();
    }
}