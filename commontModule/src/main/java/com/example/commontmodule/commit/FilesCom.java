package com.example.commontmodule.commit;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FilesCom {
    private static final String TAG = "FilesCom";
    private ArrayList<File> sdFiles;
    private static FilesCom filesCom;

    public static FilesCom getInstance() {
        if (filesCom == null) {
            synchronized (FilesCom.class) {
                if (null == filesCom) {
                    filesCom = new FilesCom();
                }
            }
        }
        return filesCom;
    }

    public ArrayList<File> getSdFiles() {
        if (sdFiles == null) sdFiles = new ArrayList<>();
        return sdFiles;
    }

    public void setSdFiles(ArrayList<File> files) {
        if (null == sdFiles) sdFiles = new ArrayList<>();else sdFiles.clear();
        sdFiles.addAll(files);
        Log.e(TAG, "setSdFiles: "+ sdFiles.size() );
    }

    public void onFilesDestroy() {
        if (null != sdFiles) sdFiles = null;
        if (null != filesCom) filesCom = null;
    }
}
