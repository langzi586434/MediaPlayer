package com.example.mediaModule.service;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import com.example.commontmodule.commit.Comment;
import com.example.utilemodule.FilesUtile;
import com.example.utilemodule.utils.VMFileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaSessionService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private final String TAG = "MediaSessionService";
    private MediaPlayer mMediaPlayer;
    private PlaybackStateCompat stateCompat;
    private int index = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(getBaseContext(), getPackageName());

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        stateCompat = stateBuilder.build();
        mediaSession.setPlaybackState(stateCompat);

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(onSessionCallback);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        initMediaPlayer();
    }

    @SuppressLint("WrongConstant")
    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "onError: ");
                return true;
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(Comment.SEEK_TO_CURRENT_POSITION, mMediaPlayer.getCurrentPosition());
                            bundle.putLong(Comment.SEEK_TO_DURATION, mMediaPlayer.getDuration());
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


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.e(TAG, "onLoadChildren--------");
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach();
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
//        ArrayList<File> sdFiles = FilesCom.getInstance().getSdFiles();
        ArrayList<File> sdFiles = VMFileUtils.getSDFiles(Comment.AUDIO);
        for (int i = 0; i < sdFiles.size(); i++) {
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, sdFiles.get(i).getPath())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, sdFiles.get(i).getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, sdFiles.get(i).getName())
                    .build();

            mediaItems.add(createMediaItem(metadata));
        }
        //向Browser发送数据
        result.sendResult(mediaItems);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        return new MediaBrowserCompat.MediaItem(
                metadata.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    /**
     * 监听播放结束的事件
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion: play end");
        stateCompat = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build();
        mediaSession.setPlaybackState(stateCompat);
        mMediaPlayer.reset();
    }

    /**
     * 监听MediaPlayer.prepare()
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        stateCompat = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                .build();
        mediaSession.setPlaybackState(stateCompat);
    }

    /**
     * 响应控制器指令的回调
     */
    private final MediaSessionCompat.Callback onSessionCallback = new MediaSessionCompat.Callback() {
        /**
         * 响应MediaController.getTransportControls().play
         */
        @Override
        public void onPlay() {
            Log.e(TAG, "onPlay");
            if (stateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                mMediaPlayer.start();
                stateCompat = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build();
                mediaSession.setPlaybackState(stateCompat);
            }
        }

        /**
         * 响应MediaController.getTransportControls().onPause
         */
        @Override
        public void onPause() {
            Log.e(TAG, "onPause");
            if (stateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mMediaPlayer.pause();
                stateCompat = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                        .build();
                mediaSession.setPlaybackState(stateCompat);
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            Log.e(TAG, "onPause");
            mMediaPlayer.pause();
            mMediaPlayer.seekTo((int) pos);
            mMediaPlayer.start();
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
        }

        /**
         * 响应MediaController.getTransportControls().playFromUri
         * @param uri
         * @param extras
         */
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(TAG, "onPlayFromUri");
            setOnPlay(null, uri, extras);

        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            Log.e(TAG, "onPlayFromSearch");
            setOnPlay(query, null, extras);
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
            setOnPlay(sdFiles.get(index).getPath(), null, null);
            mMediaPlayer.start();
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
            setOnPlay(sdFiles.get(index).getPath(), null, null);
            mMediaPlayer.start();
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop");
            if (stateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mMediaPlayer.stop();
                stateCompat = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                        .build();
                mediaSession.setPlaybackState(stateCompat);
            }
        }
    };

    private void setOnPlay(String query, Uri uri, Bundle extras) {
        try {
            if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            mMediaPlayer.reset();
            if (uri == null) {
                mMediaPlayer.setDataSource(query);

            } else {
                mMediaPlayer.setDataSource(MediaSessionService.this, uri);
            }
            mMediaPlayer.prepare();//准备同步
            stateCompat = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_CONNECTING, 0, 1.0f)
                    .build();
            mediaSession.setPlaybackState(stateCompat);
            if (extras != null)
                index = extras.getInt("index");
            //我们可以保存当前播放音乐的信息，以便客户端刷新UI
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, FilesUtile.getInstance().getSdFiles().get(index).getName())
                    .build()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
