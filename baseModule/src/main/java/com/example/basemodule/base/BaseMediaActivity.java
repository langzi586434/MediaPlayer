package com.example.basemodule.base;


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
     * 媒体控制器控制播放过程中的回调接口
     * 这里主要是根据当前播放的状态决定PlaybackControlsFragment是否显示
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
     * 连接媒体浏览服务成功后的回调接口
     */
    protected final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    super.onConnected();
                    LogToot.e("onConnected------");
//                    必须在确保连接成功的前提下执行订阅的操作
                    if (mMediaBrowser.isConnected()) {
                        //mediaId即为MediaBrowserService.onGetRoot的返回值
                        //若Service允许客户端连接，则返回结果不为null，其值为数据内容层次结构的根ID
                        //若拒绝连接，则返回null
                        String mediaId = mMediaBrowser.getRoot();

                        //Browser通过订阅的方式向Service请求数据，发起订阅请求需要两个参数，其一为mediaId
                        //而如果该mediaId已经被其他Browser实例订阅，则需要在订阅之前取消mediaId的订阅者
                        //虽然订阅一个 已被订阅的mediaId 时会取代原Browser的订阅回调，但却无法触发onChildrenLoaded回调
                        //只要发送订阅请求就会触发onChildrenLoaded回调
                        //所以无论怎样我们发起订阅请求之前都需要先取消订阅
                        mMediaBrowser.unsubscribe(mediaId);
                        //之前说到订阅的方法还需要一个参数，即设置订阅回调SubscriptionCallback
                        //当Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
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
     * 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口
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
