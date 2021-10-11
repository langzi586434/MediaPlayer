package com.example.mediaModule.test;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.basemodule.base.BaseActivity;
import com.example.basemodule.base.BaseBroadcastReceiver;
import com.example.basemodule.baseAdapter.RBaseAdapter;
import com.example.commontmodule.commit.Comment;
import com.example.mediaModule.R;
import com.example.mediaModule.adapter.PlayAdapter;
import com.example.mediaModule.play.MusicPlayerActivity;
import com.example.mediaModule.play.VideoPlayerActivity;
import com.example.utilemodule.FilesUtile;
import com.example.utilemodule.LogToot;
import com.example.utilemodule.VMFileUtils;


import java.io.File;
import java.util.ArrayList;


public class MainActivity extends BaseActivity implements RBaseAdapter.OnAdapterItemListener {
    private GridView mPlayList;
    private PlayAdapter playAdapter;
    private String type = Comment.VIDEO;
    private String pathType = "SD";
    private ArrayList<File> sdFiles = new ArrayList<>();

    @Override
    public void initView() {
        mPlayList = (GridView) findViewById(R.id.play_list);
        findViewById(R.id.play_type_R).setOnClickListener(this);
        findViewById(R.id.play_type_Y).setOnClickListener(this);
        findViewById(R.id.activity_back).setOnClickListener(this);
        findViewById(R.id.finish_act).setOnClickListener(this);
        ((Spinner) findViewById(R.id.media_type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.mediaType);
                if (languages[pos].equals(Comment.AUDIO)) {
                    type = Comment.AUDIO;
                } else {
                    type = Comment.VIDEO;
                }
                initFiles();
                ArrayList<File> sdFiles = FilesUtile.getInstance().getSdFiles();
                playAdapter.changeData(sdFiles);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        ((Spinner) findViewById(R.id.data_type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.sdorusb);
                if (languages[pos].equals(Comment.DATA_SD)) {
                    pathType = Comment.DATA_SD;
                } else {
                    pathType = Comment.DATA_USB;
                }
                initFiles();
                ArrayList<File> sdFiles = FilesUtile.getInstance().getSdFiles();
                playAdapter.changeData(sdFiles);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }


    void initAdapter() {
        if (playAdapter == null)
            playAdapter = new PlayAdapter(this, R.layout.adapter_play_item_layout);
        playAdapter.changeData(sdFiles);
        playAdapter.setOnAdapterItemListener(this);
        mPlayList.setAdapter(playAdapter);
    }


    @Override
    public void initData() {
        registerUsbBroadcast();
        initAdapter();
        initFiles();
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }


    private void initFiles() {
        if (lacksPermission()) {
            sdFiles = VMFileUtils.getUSBorSDFiles(pathType, type);
            FilesUtile.getInstance().setSdFiles(sdFiles);
        } else {
            initPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }
        playAdapter.changeData(sdFiles);
    }

    void initPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
                break;
            }
        }
    }

    private boolean lacksPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// 授权被允许
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogToot.e("授权请求被允许");
                initFiles();
            } else {
                LogToot.e("授权请求被拒绝");
            }
        }

    }

    //注册广播的方法
    private void registerUsbBroadcast() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addDataScheme("file");
        registerReceiver(mBroadcastReceiver, iFilter);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BaseBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                //USB设备移除，更新UI
                LogToot.d("onReceive: USB设备移除，更新UI");
                Comment.USB_PATH = "";
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                //USB设备挂载，更新UI
                Comment.USB_PATH = intent.getDataString().substring(7);
                LogToot.d("onReceive: USB设备移除，更新UI"
                        + Comment.USB_PATH);
            } else {
                super.onReceive(context, intent);
            }
            initFiles();
        }
    };


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.activity_back) {
            moveTaskToBack(true);
        } else if (id == R.id.finish_act) {
            finish();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onItemClick(int i, View view) {
        Intent intent;
        if (type.equals(Comment.VIDEO)) {
            intent = new Intent(this, VideoPlayerActivity.class);
        } else {
            intent = new Intent(this, MusicPlayerActivity.class);
        }
        intent.putExtra("index", i);
        startActivity(intent);
    }
}