package com.example.mediatest;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mediatest.adapter.PlayAdapter;
import com.example.mediatest.base.BaseActivity;
import com.example.mediatest.commit.Comment;
import com.example.mediatest.commit.FilesCom;
import com.example.mediatest.play.MusicPlayerActivity;
import com.example.mediatest.play.VideoPlayerActivity;
import com.example.mediatest.utils.VMFileUtils;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends BaseActivity implements PlayAdapter.onAdapterClickInterface {
    private GridView mPlayList;
    private static final String TAG = "MainActivity";
    private PlayAdapter playAdapter;
    private String type = Comment.VIDEO;
    private ArrayList<File> sdFiles = new ArrayList<>();

    @Override
    public void initView() {
        mPlayList = (GridView) findViewById(R.id.play_list);

        Spinner spinner = (Spinner) findViewById(R.id.media_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.mediaType);
                if (languages[pos].equals(Comment.AUDIO)) {
                    initFiles(Comment.AUDIO);
                } else {
                    initFiles(Comment.VIDEO);
                }
                ArrayList<File> sdFiles = FilesCom.getInstance().getSdFiles();
                playAdapter.addDataAll(sdFiles,languages[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    void initAdapter() {
        playAdapter = new PlayAdapter();
        playAdapter.setContext(this);
        playAdapter.setFiles(sdFiles, type);
        playAdapter.setOnAdapterClickInterface(this);
        mPlayList.setAdapter(playAdapter);

    }


    @Override
    public void initData() {
        initAdapter();
        initFiles(Comment.VIDEO);
        playAdapter.addDataAll(sdFiles,type);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    private void initFiles(String video) {
        if (lacksPermission()) {
            type = Comment.VIDEO.equals(video) ? Comment.VIDEO : Comment.AUDIO;
            sdFiles = VMFileUtils.getSDFiles(type);
            FilesCom.getInstance().setSdFiles(sdFiles);
        } else {
            initPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }
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
    }


    @Override
    public void onclick(int position) {
        Intent intent;
        if (type.equals(Comment.VIDEO)) {
            intent = new Intent(this, VideoPlayerActivity.class);
        } else {
            intent = new Intent(this, MusicPlayerActivity.class);
        }
        intent.putExtra("index", position);
        startActivity(intent);
    }
}