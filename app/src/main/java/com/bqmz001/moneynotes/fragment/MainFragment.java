package com.bqmz001.moneynotes.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bqmz001.moneynotes.EditNoteActivity;
import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.assembly.ViewPagerFragment;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.uilibaray.CircleProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class MainFragment extends ViewPagerFragment {

    CircleProgress circleProgress;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton fab_add;
    TextView todayCost, monthSurp, dayUseAvg, daysRemaining, nowTime;
    Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.setLayerType(View.LAYER_TYPE_NONE, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nowTime = view.findViewById(R.id.textView_nowTime);
        circleProgress = view.findViewById(R.id.circleProgress);
        refreshLayout = view.findViewById(R.id.swiperefresh);
        fab_add = view.findViewById(R.id.fab_add);
        todayCost = view.findViewById(R.id.textView_todayCost);
        monthSurp = view.findViewById(R.id.textVew_monthSurp);
        dayUseAvg = view.findViewById(R.id.textVew_dayUseAvg);
        daysRemaining = view.findViewById(R.id.textView_daysRemaining);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditNoteActivity.class);
                intent.putExtra("note_id", -1);
                intent.putExtra("from","app");
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(DataCenter.getNowUser());
                refreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        refresh(DataCenter.getNowUser());
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            refresh(DataCenter.getNowUser());


        }
    }

    void refresh(final User user) {

        disposable = Observable.just(new ArrayList<Note>())
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<Note>, List<Float>>() {
                    @Override
                    public List<Float> apply(ArrayList<Note> notes) throws Exception {
                        List<Float> floatList = new ArrayList<>();
                        long ts = DateTimeUtil.getNowDayStartTimeStamp();
                        long te = DateTimeUtil.getNowDayEndTimeStamp();
                        List<Note> noteList1 = DataCenter.getNoteList(user, DateTimeUtil.getFirstTimeOfThisMonth(), DateTimeUtil.getLastTimeOfThisMonth());

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
                        float tc = floatList.get(1);
                        float mc = floatList.get(0);
                        int budget = user.getBudget();
                        int dr = DateTimeUtil.getLastDayOfMonth(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth()) - DateTimeUtil.getNowDay();
                        todayCost.setText(new BigDecimal(tc).setScale(2, RoundingMode.HALF_UP).toString() + "元");
                        monthSurp.setText(new BigDecimal((budget - mc)).setScale(2, RoundingMode.HALF_UP).toString() + "元");
                        dayUseAvg.setText(new BigDecimal((budget - mc) / dr).setScale(2, RoundingMode.HALF_UP).toString() + "元");
                        daysRemaining.setText(dr + "天");
                        circleProgress.setProgress(mc / budget);
                        nowTime.setText("\uD83D\uDCC5当前时间：" + DateTimeUtil.timestampToDate(DateTimeUtil.getNow()));
                        disposable.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }
}
