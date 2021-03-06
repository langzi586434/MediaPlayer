package com.example.mylibrary.base;


import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.commontmodule.commit.Comment;
import com.example.utilemodule.LogToot;

import java.util.List;


public abstract class BaseMediaActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BaseMediaActivity";
    protected MediaBrowserCompat mMediaBrowser;
    protected MediaControllerCompat mMediaControllerCompat;
    protected ImageView mPlayPause, mPlayBackL;
    protected SeekBar mPlaySeek;
    protected ImageView mPlayNext;
    protected ImageView mPlayBack;
    protected TextView mStartTime;
    protected TextView mPlayName;


    protected TextView mEndTime;
    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Comment.PAUSE:
                    break;
                case Comment.PLAY:
                    break;
                case Comment.NEXT:
                    break;
                case Comment.BACK:
                    break;
                case Comment.SEEK_TO:
                    Bundle state = (Bundle) msg.obj;
                    long currentPosition = state.getLong(Comment.SEEK_TO_CURRENT_POSITION, 0);
                    long duration = state.getLong(Comment.SEEK_TO_DURATION, 0);
                    mEndTime.setText(stringForTime((int) duration));
                    mPlaySeek.setMax((int) duration);
                    mStartTime.setText(stringForTime((int) currentPosition));
                    mPlaySeek.setProgress((int) currentPosition);
                    break;
            }
        }
    };

    @Override
    public void initData() {


    }


    protected abstract void onServerPlaybackStateChanged(PlaybackStateCompat state);

    protected abstract void onServerMetadataChanged(MediaMetadataCompat metadata);

    protected abstract void onServerBrowserCallback(String parentId, List<MediaBrowserCompat.MediaItem> children);

    protected abstract void onServerSessionEvent(String event, Bundle bundle);

    protected abstract void onBaseClickListener(View view);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mMediaBrowser != null && !mMediaBrowser.isConnected())
            mMediaBrowser.connect();
    }

    /**
     * ???????????????????????????????????????????????????
     * ????????????????????????????????????????????????PlaybackControlsFragment????????????
     */
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    onServerPlaybackStateChanged(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    onServerMetadataChanged(metadata);
                }

                @Override
                public void onSessionEvent(String event, Bundle extras) {
                    super.onSessionEvent(event, extras);
                    onServerSessionEvent(event, extras);
                }
            };

    protected void buildTransportControls() {
        mMediaControllerCompat.registerCallback(mMediaControllerCallback);
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
                mMediaControllerCompat.getTransportControls().seekTo(progress);
            }
        });

    }

    /**
     * ????????????????????????????????????????????????
     */
    protected final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    super.onConnected();
                    LogToot.e("onConnected------");
//                    ????????????????????????????????????????????????????????????
                    if (mMediaBrowser.isConnected()) {
                        //mediaId??????MediaBrowserService.onGetRoot????????????
                        //???Service?????????????????????????????????????????????null??????????????????????????????????????????ID
                        //???????????????????????????null
                        String mediaId = mMediaBrowser.getRoot();

                        //Browser????????????????????????Service???????????????????????????????????????????????????????????????mediaId
                        //????????????mediaId???????????????Browser?????????????????????????????????????????????mediaId????????????
                        //?????????????????? ???????????????mediaId ???????????????Browser????????????????????????????????????onChildrenLoaded??????
                        //????????????????????????????????????onChildrenLoaded??????
                        //????????????????????????????????????????????????????????????????????????
                        mMediaBrowser.unsubscribe(mediaId);
                        //????????????????????????????????????????????????????????????????????????SubscriptionCallback
                        //???Service?????????????????????????????????????????????????????????SubscriptionCallback.onChildrenLoaded??????
                        mMediaBrowser.subscribe(mediaId, onBrowserSubscriptionCallback);
                    }
                    try {
                        MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
                        mMediaControllerCompat = new MediaControllerCompat(BaseMediaActivity.this, token);
                        MediaControllerCompat.setMediaController(BaseMediaActivity.this, mMediaControllerCompat);
                        buildTransportControls();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailed() {
                    super.onConnectionFailed();
                    LogToot.e("onConnectionFailed: ");

                }

                @Override
                public void onConnectionSuspended() {
                    super.onConnectionSuspended();
                    LogToot.e("onConnectionSuspended: ");
                }
            };


    /**
     * ????????????????????????(MediaBrowserService)???????????????????????????????????????
     */
    private final MediaBrowserCompat.SubscriptionCallback onBrowserSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    LogToot.e( "onChildrenLoaded------");
                    onServerBrowserCallback(parentId, children);
                }
            };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(this);
        if (controllerCompat != null) {
            controllerCompat.getTransportControls().stop();
            controllerCompat.unregisterCallback(mMediaControllerCallback);
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected())
            mMediaBrowser.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
    public void onClick(View v) {
        onBaseClickListener(v);
    }
}
