package com.example.utilemodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.commontmodule.commit.Comment;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class IBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            Toast.makeText(context, "网络连接不可用，请稍后重试", Toast.LENGTH_SHORT).show();
            Comment.NET_TYPE = "NULL";
        } else {
//            onConnect.onConnectUpData();
            Comment.NET_TYPE = mobNetInfo.isConnected() ? "MOBILE" : "WIFI";
        }
    }

    private onConnect onConnect;

    public void setOnConnect(IBroadcastReceiver.onConnect onConnect) {
        this.onConnect = onConnect;
    }

    public interface onConnect {
        void onConnectUpData();
    }
}
