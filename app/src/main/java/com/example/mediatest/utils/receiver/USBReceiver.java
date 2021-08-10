package com.example.mediatest.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mediatest.commit.Comment;

public class USBReceiver extends BroadcastReceiver {
    private String usbPath = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {//usb插入
            Log.e("===", "usb插入");
            if (intent.getData() != null && intent.getData().getPath() != null) {
                usbPath = intent.getData().getPath();
                Log.e("===", "usb插入=usbPath==" + usbPath);
            }
        }
        if (Comment.USB_DEVICE_DETACHED.equals(action)) {//usb拔出
            Log.e("===", "usb拔出");
        }
    }

    public String getUsbPath() {
        return usbPath;
    }

}
