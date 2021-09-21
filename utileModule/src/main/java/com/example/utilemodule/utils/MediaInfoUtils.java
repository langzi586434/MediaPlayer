package com.example.utilemodule.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.example.utilemodule.LogToot;

import java.io.IOException;

public class MediaInfoUtils {

    private static final String TAG = "MediaInfoUtils";
    private StringBuilder strBuilder;
    private int trackCount = 0;
    private MediaExtractor mediaExtractor;

    public MediaInfoUtils() {
        if (strBuilder == null) {
            strBuilder = new StringBuilder();
        }
    }

    public String deal(String url) {
        if (mediaExtractor == null) {
            mediaExtractor = new MediaExtractor();
        }
        strBuilder.delete(0, strBuilder.length());
        try {
            mediaExtractor.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        trackCount = mediaExtractor.getTrackCount();
        if (trackCount < 1) {
            LogToot.e(getClass().getName(), url + " has no track!");
        }
        strBuilder.append("\n------------------------------------------------\n");
        MediaFormat mediaFormat = null;
        for (int i = 0; i < trackCount; i++) {
            mediaFormat = mediaExtractor.getTrackFormat(i);
            strBuilder.append(mediaFormat.toString());
        }
        strBuilder.append("\n------------------------------------------------\n");
        String replace = strBuilder.toString()
                .replace("{", "\n")
                .replace(",","\n")
                .replace("}", "");
        LogToot.d(TAG, "deal: "+replace);
        mediaExtractor.release();
        mediaExtractor = null;
        return strBuilder.toString();
    }
}
