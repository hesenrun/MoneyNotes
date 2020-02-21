package com.bqmz001.moneynotes;

import android.app.Application;
import android.widget.Toast;


import com.bqmz001.moneynotes.util.ToastUtil;

import org.litepal.LitePal;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this.getApplicationContext());
        LitePal.initialize(this);
    }


}
