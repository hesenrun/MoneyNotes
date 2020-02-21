package com.bqmz001.moneynotes.fragment;

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
    TextView todayCost, monthSurp, dayUseAvg, daysRemaining;
    List<Note> noteList = new ArrayList<>();

    Disposable disposable, disposable2;

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
        circleProgress = view.findViewById(R.id.circleProgress);
        refreshLayout = view.findViewById(R.id.swiperefresh);
        fab_add = view.findViewById(R.id.fab_add);
        todayCost = view.findViewById(R.id.textView_todayCost);
        monthSurp = view.findViewById(R.id.textVew_monthSurp);
        dayUseAvg = view.findViewById(R.id.textVew_dayUseAvg);
        daysRemaining = view.findViewById(R.id.textView_daysRemaining);
        DataCenter.getNowUser();
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditNoteActivity.class);
                intent.putExtra("note_id", -1);
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
                .map(new Function<ArrayList<Note>, Float>() {
                    @Override
                    public Float apply(ArrayList<Note> notes) throws Exception {
                        List<Note> noteList1 = DataCenter.getNoteList(user, DateTimeUtil.getNowDayStartTimeStamp(), DateTimeUtil.getNowDayEndTimeStamp());

                        Float i = new Float(0);
                        for (Note note : noteList1) {
                            i += note.getCost();
                        }
                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(Float aFloat) throws Exception {
                        todayCost.setText(new BigDecimal(aFloat).setScale(2, RoundingMode.HALF_UP).toString() +"元");
                        disposable.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

        disposable2 = Observable.just(new ArrayList<Note>())
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<Note>, Float>() {
                    @Override
                    public Float apply(ArrayList<Note> notes) throws Exception {
                        List<Note> noteList1 = DataCenter.getNoteList(user, DateTimeUtil.getFirstTimeOfThisMonth(), DateTimeUtil.getLastTimeOfThisMonth());

                        Float i = new Float(0);
                        for (Note note : noteList1) {
                            i += note.getCost();
                        }
                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(Float aFloat) throws Exception {
                        float f = aFloat;
                        int budget = DataCenter.getNowUser().getBudget();
                        int dr = DateTimeUtil.getLastDayOfMonth(DateTimeUtil.getNowYear(), DateTimeUtil.getNowMonth()) - DateTimeUtil.getNowDay();
                        monthSurp.setText(new BigDecimal((budget - f)).setScale(2,RoundingMode.HALF_UP).toString()+"元");
                        dayUseAvg.setText(new BigDecimal((budget - f) / dr ).setScale(2,RoundingMode.HALF_UP).toString()+"元");
                        daysRemaining.setText(dr +"天");
                        circleProgress.setProgress(f / budget);
                        disposable2.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
//        refreshLayout.setRefreshing(false);
    }
}
