package com.example.mediaModule.play;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.example.commontmodule.commit.Comment;
import com.example.basemodule.base.BaseActivity;
import com.example.mediaModule.R;
import com.example.utilemodule.FilesUtile;
import com.example.utilemodule.utils.MediaInfoUtils;
import com.example.utilemodule.utils.VMFileUtils;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, View.OnClickListener {
    private static final String TAG = "VideoPlayActivity";
    private VideoView mPlayVideo;
    private SeekBar mPlaySeek;
    private TextView mStartTime;
    private MediaSessionCompat mediaSession;
    private TextView mEndTime, mPlayName;
    private PlaybackStateCompat.Builder stateBuilder;
    private PlaybackStateCompat stateCompat;
    int index = 0;
    private MediaControllerCompat mediaControllerCompat;
    MediaInfoUtils mediaInfoUtils;
    ImageView mPlayPause;

    @Override
    protected void initView() {
        mPlaySeek = (SeekBar) findViewById(R.id.play_seek);
        ImageView mPlayNext = (ImageView) findViewById(R.id.play_next);
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        ImageView mPlayBack = (ImageView) findViewById(R.id.play_back);
        ImageView mPlayBackL = (ImageView) findViewById(R.id.back_play_layout);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mPlayName = (TextView) findViewById(R.id.play_name);
        mEndTime = (TextView) findViewById(R.id.end_time);
        mPlayVideo = (VideoView) findViewById(R.id.play_video);
        mPlayBackL.setOnClickListener(this);
        mPlayVideo.setOnCompletionListener(this);
        mPlayPause.setOnClickListener(this);
        mPlayBack.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mPlaySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaControllerCompat.getTransportControls().seekTo(progress);
            }
        });
    }


    @Override
    protected int setLayout() {
        return R.layout.play_video_layout;
    }

    @Override
    public void initData() {
        if (mediaInfoUtils == null) {
            mediaInfoUtils = new MediaInfoUtils();
        }
        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(this, "LOG_TAG");

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible
        mediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller
        mediaSession.setCallback(onSessionCallback);

        // Create a MediaControllerCompat
        mediaControllerCompat =
                new MediaControllerCompat(this, mediaSession);

        MediaControllerCompat.setMediaController(this, mediaControllerCompat);

        mediaControllerCompat.registerCallback(mMediaControllerCallback);

        MediaControllerCompat.getMediaController(this).getTransportControls().playFromSearch(VMFileUtils.getSDFiles(Comment.VIDEO).get(index).getPath(), null);
        mediaInfoUtils.deal(VMFileUtils.getSDFiles(Comment.VIDEO).get(index).getPath());
    }

    private final MediaSessionCompat.Callback onSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            mPlayVideo.start();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            mPlayPause.setBackgroundResource(R.drawable.pause);
        }

        @Override
        public void onPause() {
            super.onPause();
            mPlayVideo.pause();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            mPlayPause.setBackgroundResource(R.drawable.play);
        }

        @Override
        public void onStop() {
            super.onStop();
            mPlayVideo.stopPlayback();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);
            mPlayVideo.setVideoPath(query);
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            mPlayPause.setBackgroundResource(R.drawable.pause);
            mPlayVideo.start();
            initThread();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            mPlayVideo.seekTo((int) pos);
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            ArrayList<File> sdFiles = FilesUtile.getInstance().getSdFiles();
            if (index >= sdFiles.size() - 1) {
                index = 0;
            } else {
                ++index;
            }
            mPlayVideo.setVideoPath(sdFiles.get(index).getPath());
            mediaInfoUtils.deal(sdFiles.get(index).getPath());

            mPlayVideo.start();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);

        }


        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            ArrayList<File> sdFiles = FilesUtile.getInstance().getSdFiles();
            if (index == 0) {
                index = sdFiles.size() - 1;
            } else {
                --index;
            }
            mPlayVideo.setVideoPath(sdFiles.get(index).getPath());
            mPlayVideo.start();
            mediaInfoUtils.deal(sdFiles.get(index).getPath());

        }
    };


    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    super.onSessionEvent(event, extras);
                    long currentPosition = extras.getLong(Comment.SEEK_TO_CURRENT_POSITION, 0);
                    long duration = extras.getLong(Comment.SEEK_TO_DURATION, 0);
                    String playingName = extras.getString(Comment.PLAYING_NAME);
                    mEndTime.setText(stringForTime((int) duration));
                    mPlaySeek.setMax((int) duration);
                    mPlayName.setText(playingName);
                    mStartTime.setText(stringForTime((int) currentPosition));
                    mPlaySeek.setProgress((int) currentPosition);
                }
            };

    private void initThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mPlayVideo != null && mPlayVideo.isPlaying()) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(Comment.SEEK_TO_CURRENT_POSITION, mPlayVideo.getCurrentPosition());
                            bundle.putLong(Comment.SEEK_TO_DURATION, mPlayVideo.getDuration());
                            Log.d(TAG, "Duration: " + mPlayVideo.getDuration() + "  CurrentPosition" + mPlayVideo.getCurrentPosition());
                            bundle.putString(Comment.PLAYING_NAME, FilesUtile.getInstance().getSdFiles().get(index).getName());
                            mediaSession.sendSessionEvent(Comment.SEEK_TO_EVENT, bundle);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPlayVideo.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayVideo.resume();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stateCompat = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build();
        mediaSession.setPlaybackState(stateCompat);
        MediaControllerCompat.getMediaController(this).getTransportControls().skipToNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_pause) {
            int pbState = MediaControllerCompat.getMediaController(this).getPlaybackState().getState();
            if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                MediaControllerCompat.getMediaController(this).getTransportControls().pause();
            } else {
                MediaControllerCompat.getMediaController(this).getTransportControls().play();
            }
        } else if (id == R.id.play_back) {
            MediaControllerCompat.getMediaController(this).getTransportControls().skipToPrevious();
        } else if (id == R.id.play_next) {
            MediaControllerCompat.getMediaController(this).getTransportControls().skipToNext();
        } else if (id == R.id.back_play_layout) {
            finish();
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return hours + ":" + minutes + ":" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaSession != null) mediaSession = null;
        if (stateBuilder != null) stateBuilder = null;
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mMediaControllerCallback);
            mediaControllerCompat = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
