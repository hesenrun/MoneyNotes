package com.bqmz001.moneynotes.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bqmz001.moneynotes.bus.RxBus;
import com.bqmz001.moneynotes.entity.EventBean;
import com.bqmz001.moneynotes.util.ToastUtil;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class BackgroundService extends Service {
    Notification notification;

    public BackgroundService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        notification=new Notification.Builder(getApplicationContext()).build();
//        startForeground(1,notification);
        receiveEvent();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void receiveEvent(){
        RxBus.getInstance().toObservable().map(new Function<Object, EventBean>() {
            @Override
            public EventBean apply(Object o) throws Exception {
                return (EventBean)o;
            }
        }).subscribe(new Consumer<EventBean>() {
            @Override
            public void accept(EventBean eventBean) throws Exception {
                if (eventBean!=null){
                    ToastUtil.showContent("hello!");
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
