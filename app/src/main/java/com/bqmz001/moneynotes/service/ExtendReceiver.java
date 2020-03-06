package com.bqmz001.moneynotes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

public class ExtendReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.showContent("yes");
        EventUtil.postEvent(0, "update", "update");
        EventUtil.postEvent(2, "updateAlarm", "updateAlarm");
    }
}
