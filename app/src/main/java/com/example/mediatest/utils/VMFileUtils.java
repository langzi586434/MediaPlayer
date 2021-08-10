package com.example.mediatest.utils;


import android.os.Environment;
import android.util.Log;

import com.example.mediatest.commit.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public abstract class VMFileUtils {
    private static final String TAG = "FileUtils";

    private static ArrayList<File> filesName;

    public static ArrayList<File> getSDFiles(String fileType) {
        if (filesName == null) {
            filesName = new ArrayList<>();
        } else {
            filesName.clear();
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(fileType.equals(Comment.VIDEO) ? Comment.MOVIES_PATH : Comment.MUSIC_PATH);
            File[] files = path.listFiles();// 读取
            getFileName(files, fileType);
        }
        return filesName;
    }

    public ArrayList<File> getUSBFiles(String fileType) {
        ArrayList<File> fileName = new ArrayList<>();

        File path = new File("/mnt/usbhost1");//外置U盘路径
        File[] files = path.listFiles();// 读取
        Executors.newCachedThreadPool().submit(new Runnable() {
            @Override
            public void run() {
            }
        });
        return fileName;
    }

    private static void getFileName(File[] files, String fileType) {
        if (files != null) {// 先判断目录是否为空
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.e(TAG, "文件夹  " + file.getPath().toString());
                    getFileName(file.listFiles(), fileType);
                } else {
                    for (String string : fileType.equals(Comment.VIDEO) ? Comment.VIDEO_STRINGS : Comment.AUDIO_STRINGS) {
                        if (file.getName().endsWith(string)) {
                            filesName.add(file);
                            Log.e(TAG, "文件  " + file.getName());
                            break;
                        }
                    }
                }
            }
        }
    }


}
