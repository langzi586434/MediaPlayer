package com.example.utilemodule;



import android.os.Environment;
import android.util.Log;

import com.example.commontmodule.commit.Comment;
import com.example.utilemodule.LogToot;

import java.io.File;
import java.util.ArrayList;

import static com.example.commontmodule.commit.Comment.USB_PATH;


public abstract class VMFileUtils {
    private static final String TAG = "FileUtils";

    private static ArrayList<File> filesName;

    public static ArrayList<File> getSDFiles(String fileType) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(fileType.equals(Comment.VIDEO) ? Comment.MOVIES_PATH : Comment.MUSIC_PATH);
            File[] files = path.listFiles();// 读取
            getFileName(files, fileType);
        }
        return filesName;
    }

    public static ArrayList<File> getUSBorSDFiles(String pathType, String fileType) {
        if (filesName == null) {
            filesName = new ArrayList<>();
        } else {
            filesName.clear();
        }
        if (pathType.equals(Comment.DATA_SD)) {
            filesName = getSDFiles(fileType);
        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = new File(USB_PATH.equals("") ? searchPaths() : USB_PATH);
                File[] files = path.listFiles();// 读取
                getFileName(files, fileType);
            }
        }

        return filesName;
    }

    private static String searchPaths() {
        String path = "";
        File storage = new File("/storage");
        File[] files = storage.listFiles();
        assert files != null;
        for (final File file : files) {
            if (file.canRead()) {
                Log.d(TAG, "searchPaths: "+file.getPath());
                if (!file.getName().equals(Environment.getExternalStorageDirectory().getName())) {
                    Log.d(TAG, "searchPaths: "+path);
                    path = file.getPath();
                }
            }
        }
        return path;
    }

    private static void getFileName(File[] files, String fileType) {
        if (files != null) {// 先判断目录是否为空
            for (File file : files) {
                if (file.isDirectory()) {
                    LogToot.e(TAG, "文件夹  " + file.getPath());
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
