package com.bqmz001.moneynotes.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import com.bqmz001.moneynotes.EditNoteActivity;
import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.SplashActivity;
import com.bqmz001.moneynotes.bus.RxBus;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.EventBean;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BackgroundService extends Service {
    NotificationManager manager;
    RemoteViews remoteViews;
    Notification.Builder builder;

    NotificationManager manager2;

    Disposable disposable;
    private String notificationId = "com.bqmz001.moneynotes";
    private String notificationName = "moneynotes.BackgroundService";

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("Notification", false)) {
            startNotification();
        } else {
            startNullNotification();
        }
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

    private void startNullNotification() {
        manager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_LOW);
            manager2.createNotificationChannel(channel);
        }
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle("后台服务正在运行")
                .setContentIntent(null)
                .setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }

        manager2.notify(0, builder.build());
        updateData();
    }
    private void stopNullNotification(){
        manager2.cancel(0);
        manager2=null;
    }


    private void startNotification() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        Notification notification = createNotification();
        manager.notify(0, notification);
        updateData();
    }

    private void closeNotification() {
        manager.cancel(0);
        manager = null;
        remoteViews = null;
        builder = null;
    }

    private Notification createNotification() {
        remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        Intent mainIntent = new Intent(getApplicationContext(), SplashActivity.class);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent buttonIntent = new Intent(getApplicationContext(), EditNoteActivity.class);
        buttonIntent.putExtra("note_id", -1);
        buttonIntent.putExtra("from", "notification");
        PendingIntent pendingIntentButton = PendingIntent.getActivity(getApplicationContext(), 1, buttonIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.textView_quickAdd, pendingIntentButton);
        builder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.btn_star)
                .setTicker("记账本")
                .setContentTitle("记账本")
                .setContent(remoteViews)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        return builder.build();


    }

    @SuppressLint("CheckResult")
    private void receiveEvent() {
        RxBus.getInstance().toObservable().map(new Function<Object, EventBean>() {
            @Override
            public EventBean apply(Object o) throws Exception {
                return (EventBean) o;
            }
        }).subscribe(new Consumer<EventBean>() {
            @Override
            public void accept(EventBean eventBean) throws Exception {
                if (eventBean != null) {
                    ToastUtil.showContent(eventBean.getMsgId() + " " + eventBean.getMsg() + " " + eventBean.getParameter());
                    if (eventBean.getMsgId() == 1) {
                        if (eventBean.getParameter().equals("true")) {
                            stopNullNotification();
                            startNotification();
                        } else if (eventBean.getParameter().equals("false")) {
                            closeNotification();
                            startNullNotification();

                        }
                    }
                    if (eventBean.getMsgId() == 0)
                        updateData();
                }
            }
        });
    }

    private void updateData() {
        disposable = Observable.just(new ArrayList<Note>())
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<Note>, List<Float>>() {
                    @Override
                    public List<Float> apply(ArrayList<Note> notes) throws Exception {
                        List<Float> floatList = new ArrayList<>();
                        long ts = DateTimeUtil.getNowDayStartTimeStamp();
                        long te = DateTimeUtil.getNowDayEndTimeStamp();
                        List<Note> noteList1 = DataCenter.getNoteList(DataCenter.getDefaultUser(), DateTimeUtil.getFirstTimeOfThisMonth(), DateTimeUtil.getLastTimeOfThisMonth());

                        Float i = new Float(0);
                        Float j = new Float(0);
                        for (Note note : noteList1) {
                            i += note.getCost();
                            if (note.getTime() >= ts && note.getTime() <= te)
                                j += note.getCost();
                        }
                        floatList.add(i);
                        floatList.add(j);
                        return floatList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Float>>() {
                    @Override
                    public void accept(List<Float> floats) throws Exception {
                        List<Float> floatList = floats;
                        float todayCost = floatList.get(1);
                        float monthCost = floatList.get(0);
                        int budget = DataCenter.getNowUser().getBudget();
                        int dr = DateTimeUtil.getLastDayOfMonth(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth()) - DateTimeUtil.getNowDay();
                        if (remoteViews != null) {
                            remoteViews.setTextViewText(R.id.textView_todayCost, new BigDecimal(todayCost).setScale(2, RoundingMode.HALF_UP).toString() + "元");
                            remoteViews.setTextViewText(R.id.textView_progress, Double.parseDouble(new DecimalFormat("#.0").format((monthCost / budget) * 100)) + "%");
                            remoteViews.setTextViewText(R.id.textView_daysRemaining, dr + "天");
                            manager.notify(0, builder.build());
                        }
                        disposable.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
