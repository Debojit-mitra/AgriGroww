package com.dmsskbm.agrigroww.expert;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    String videoURL;
    StyledPlayerView videoPlayer;
    ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoURL = getIntent().getStringExtra("videoURL");

        videoPlayer = findViewById(R.id.videoPlayer);

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
        exoPlayer.setPlayWhenReady(true);





    }

    @Override
    public void onBackPressed() {
        exoPlayer.release();
        finish();
        super.onBackPressed();
    }
}