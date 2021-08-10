package com.example.mediatest.commit;

public class Comment {
    public static final int PLAY = 0x01;
    public static final int PAUSE = 0x02;
    public static final int NEXT = 0x03;
    public static final int BACK = 0x04;
    public static final int SEEKTO = 0x05;
    public static final String USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String SEEKTO_CURRENT_POSITION = "seek_to_current_position";
    public static final String SEEKTO_DURATION = "seek_to_duration";
    public static final String SEEKTO_EVENT = "seek_to_event";
    public static final String[] VIDEO_STRINGS = {"wmv", "asf", "asx", "rm", "rmvb", "mpg", "mpeg", "mpe", "3gp", "mov", "mp4", "m4v", "avi", "dat", "mkv", "flv", "vob"};
    public static final String[] AUDIO_STRINGS = {"cd", "wave", "aiff", "mpeg", "mp3", "mpeg-4", "midi", "wma", "realaudio", "vqf", "oggvorbis", "amr", "ape", "flac", "aac"};
    public static final String VIDEO = "movies";
    public static final String AUDIO = "music";
    public static final String MOVIES_PATH = "/sdcard/Movies";
    public static final String MUSIC_PATH = "/sdcard/Music";
}
