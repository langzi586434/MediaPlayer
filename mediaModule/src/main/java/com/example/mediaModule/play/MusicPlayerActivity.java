package com.example.mediaModule.play;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.commontmodule.commit.Comment;
import com.example.mediaModule.R;
import com.example.basemodule.base.BaseMediaActivity;
import com.example.mediaModule.service.MediaSessionService;

import java.util.List;

public class MusicPlayerActivity extends BaseMediaActivity  {

    private int index = 0;
    private static final String TAG = "MediaPlayerActivity";

    @Override
    protected void onServerPlaybackStateChanged(PlaybackStateCompat state) {
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_PLAYING");
                mPlayPause.setBackgroundResource(R.drawable.pause);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_STOPPED");
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_PAUSED");
                mPlayPause.setBackgroundResource(R.drawable.play);
                break;
            case PlaybackStateCompat.STATE_NONE:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_NONE");
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_BUFFERING");
                break;
            case PlaybackStateCompat.STATE_CONNECTING:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_CONNECTING");
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_ERROR");
                break;
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_FAST_FORWARDING");
                break;
            case PlaybackStateCompat.STATE_REWINDING:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_REWINDING");
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_SKIPPING_TO_NEXT");
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_SKIPPING_TO_PREVIOUS");
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                Log.e(TAG, "onServerPlaybackStateChanged: STATE_SKIPPING_TO_QUEUE_ITEM");
                break;
            default:


        }
    }

    @Override
    protected void onServerMetadataChanged(MediaMetadataCompat metadata) {
        updateDuration(metadata);
    }

    @Override
    protected void onServerBrowserCallback(String parentId, List<MediaBrowserCompat.MediaItem> children) {
        Uri mediaUri = children.get(index).getDescription().getMediaUri();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls().playFromUri(mediaUri, bundle);
    }

    @Override
    protected void onServerSessionEvent(String event, Bundle bundle) {
        Message message = handler.obtainMessage();
        message.what = Comment.SEEK_TO;
        message.obj = bundle;
        handler.sendMessage(message);
    }

    @Override
    protected void onBaseClickListener(View view) {

    }

    public void buildTransportControls() {
        // Grab the view for the play/pause button
        mPlaySeek = (SeekBar) findViewById(R.id.play_seek);
        mPlayBackL = (ImageView) findViewById(R.id.back_play_layout);
        mPlayNext = (ImageView) findViewById(R.id.play_next);
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        mPlayBack = (ImageView) findViewById(R.id.play_back);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndTime = (TextView) findViewById(R.id.end_time);
        mPlayName = (TextView) findViewById(R.id.play_name);
        super.buildTransportControls();

        mPlayBackL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pbState = MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getPlaybackState().getState();
                Log.e(TAG, "onClick: " + pbState + "   " + PlaybackStateCompat.STATE_PLAYING);
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls().pause();
                } else {
                    MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls().play();
                }
            }
        });
        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls().skipToNext();
            }
        });
        mPlayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls().skipToPrevious();
            }
        });
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        mPlayName.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mPlaySeek.setMax(duration);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        super.initData();
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaSessionService.class),
                mConnectionCallback,
                null);
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
    }

    @Override
    protected int setLayout() {
        return R.layout.play_music_layout;
    }
}
