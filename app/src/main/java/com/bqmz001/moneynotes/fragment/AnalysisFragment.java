package com.bqmz001.moneynotes.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bqmz001.moneynotes.CustomAnalysisActivity;
import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.assembly.ViewPagerFragment;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.ClassificationFakeCount;
import com.bqmz001.moneynotes.entity.DailyNoteFakeCount;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.util.DateTimeUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class AnalysisFragment extends ViewPagerFragment {

    PieChartView pieChartView;
    PieChartData pieChartData;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ColumnChartView columnChartView;
    ColumnChartData columnChartData;
    TextView pieChartContent, dailycost;
    Disposable disposable, disposable2;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayout;
    NestedScrollView scrollView;
    Button button;
    User user;


    int type = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_analysis, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = DataCenter.getNowUser();
        button = view.findViewById(R.id.button);
        pieChartView = view.findViewById(R.id.pieChartView);
        pieChartContent = view.findViewById(R.id.textView_pieChartContent);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        columnChartView = view.findViewById(R.id.columnChartView);
        radioGroup = view.findViewById(R.id.radioGroup_type);
        dailycost = view.findViewById(R.id.textView_dailyCost);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        radioButton = view.findViewById(R.id.radioButton_today);
        scrollView = view.findViewById(R.id.scrollView);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            float ratio = 6.5f; //水平和竖直方向滑动的灵敏度,偏大是水平方向灵敏
            float x0 = 0f;
            float y0 = 0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0 = event.getX();
                        y0 = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(event.getX() - x0);
                        float dy = Math.abs(event.getY() - y0);
                        x0 = event.getX();
                        y0 = event.getY();
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }
        };

        columnChartView.setOnTouchListener(touchListener);
        pieChartView.setOnTouchListener(touchListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                button.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
                switch (checkedId) {
                    case R.id.radioButton_today:
                        swipeRefreshLayout.setRefreshing(true);
                        getToday();
                        break;
                    case R.id.radioButton_thisWeek:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisWeek();
                        break;
                    case R.id.radioButton_thisMonth:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisMonth();
                        break;
                }
            }
        });
        radioButton.setChecked(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                switch (type) {
                    case 1:
                        swipeRefreshLayout.setRefreshing(true);
                        getToday();
                        break;
                    case 2:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisWeek();
                        break;
                    case 3:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisMonth();
                        break;


                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomAnalysisActivity.class));
            }
        });

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            user = DataCenter.getNowUser();
            swipeRefreshLayout.setRefreshing(true);
            switch (type) {
                case 1:
                    swipeRefreshLayout.setRefreshing(true);
                    getToday();
                    break;
                case 2:
                    swipeRefreshLayout.setRefreshing(true);
                    getThisWeek();
                    break;
                case 3:
                    swipeRefreshLayout.setRefreshing(true);
                    getThisMonth();
                    break;
            }
        }

    }

    void getToday() {
        type = 1;
        init(DateTimeUtil.getNowDayStartTimeStamp(), DateTimeUtil.getNowDayEndTimeStamp());
    }

    void getThisWeek() {
        type = 2;
        init(DateTimeUtil.getFirstTimeOfThisWeek(), DateTimeUtil.getLastTimeOfThisWeek());
    }

    void getThisMonth() {
        type = 3;
        init(DateTimeUtil.getFirstTimeOfThisMonth(), DateTimeUtil.getLastTimeOfThisMonth());
    }



    private void init(long startTime, long stopTime) {
        getPieChartData(startTime, stopTime, user);
        getColumnChartData(startTime, stopTime, user);

    }

    private void getPieChartData(final long startTime, final long stopTime, final User user) {
        disposable = Observable.just(new ArrayList<ClassificationFakeCount>())
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<ClassificationFakeCount>, ArrayList<ClassificationFakeCount>>() {
                    @Override
                    public ArrayList<ClassificationFakeCount> apply(ArrayList<ClassificationFakeCount> classificationFakeCounts) throws Exception {
                        classificationFakeCounts = (ArrayList<ClassificationFakeCount>) DataCenter.getPieChartData(startTime, stopTime, user);
                        return classificationFakeCounts;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<ClassificationFakeCount>>() {
                    @Override
                    public void accept(ArrayList<ClassificationFakeCount> classificationFakeCounts) throws Exception {
                        List<ClassificationFakeCount> list = classificationFakeCounts;
                        int numValue = list.size();
                        if (numValue > 0) {
                            scrollView.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.GONE);
                            float allCount = 0;
                            for (ClassificationFakeCount c : list) {
                                allCount += c.getSum();
                            }

                            List<SliceValue> sliceValueList = new ArrayList<>();
                            for (int i = 0; i < numValue; i++) {
                                SliceValue sliceValue = new SliceValue(((list.get(i).getSum()) / allCount) * 360, list.get(i).getColor());
                                sliceValue.setLabel(list.get(i).getName() + " " + new BigDecimal(list.get(i).getSum()).setScale(2, RoundingMode.HALF_UP).floatValue());
                                sliceValueList.add(sliceValue);
                            }
                            pieChartData = new PieChartData(sliceValueList);
                            pieChartData.setHasLabels(true);
                            pieChartData.setHasLabelsOnlyForSelected(false);
                            pieChartData.setHasLabelsOutside(false);
                            pieChartData.setHasCenterCircle(false);
                            pieChartView.setChartRotationEnabled(true);
                            pieChartView.setValueSelectionEnabled(false);
                            pieChartView.setCircleFillRatio(0.95f);
                            pieChartView.setPieChartData(pieChartData);
                            String str1 = DateTimeUtil.timestampToDate(startTime);
                            String str2 = DateTimeUtil.timestampToDate(stopTime);

                            String s = "　　从" + str1.substring(0, 10) + "到" + str2.substring(0, 10) + "，共计花费" + allCount + "元。其中:";
                            for (ClassificationFakeCount c : list) {
                                s += c.getName() + "分类下消费了" + c.getSum() + "元，占比" + new BigDecimal((c.getSum() / allCount) * 100).setScale(2, RoundingMode.HALF_UP).floatValue() + "%。";
                            }
                            s += "其余未显示的分类中没有支出。";
                            pieChartContent.setText(s);
                        } else {
                            scrollView.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                        disposable.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }

    private void getColumnChartData(final long startTime, final long stopTime, final User user) {
        List<DailyNoteFakeCount> list = new ArrayList<>();
        disposable2 = Observable.just(list)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<DailyNoteFakeCount>, List<DailyNoteFakeCount>>() {
                    @Override
                    public List<DailyNoteFakeCount> apply(List<DailyNoteFakeCount> dailyNoteFakeCounts) throws Exception {
                        List<DailyNoteFakeCount> dnfcs = dailyNoteFakeCounts;
                        int days = (int) ((stopTime + 1 - startTime) / 86400000);
                        for (int i = 0; i < days; i++) {
                            DailyNoteFakeCount dailyNoteFakeCount = new DailyNoteFakeCount();
                            dailyNoteFakeCount.setTime(DateTimeUtil.timestampToDate((startTime + ((long)i * 86400000))).substring(5, 10));
                            dailyNoteFakeCount.setStartTime(startTime + ((long)i * 86400000));
                            dailyNoteFakeCount.setStopTime(startTime + (((long)i + 1) * 86400000) - 1);
                            dnfcs.add(dailyNoteFakeCount);
                        }

                        for (DailyNoteFakeCount dnfc : dnfcs) {
                            List<Note> noteList = DataCenter.getNoteList(user, dnfc.getStartTime(), dnfc.getStopTime());
                            float i = 0f;
                            for (Note note : noteList) {
                                i += note.getCost();
                            }
                            dnfc.setCount(i);
                        }
                        return dnfcs;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DailyNoteFakeCount>>() {
                    @Override
                    public void accept(List<DailyNoteFakeCount> dailyNoteFakeCounts) throws Exception {
                        List<DailyNoteFakeCount> d = dailyNoteFakeCounts;
                        int numSubcolumns = 1;
                        int numColumns = d.size();
                        if (numColumns > 0) {
                            dailycost.setVisibility(View.VISIBLE);
                            columnChartView.setVisibility(View.VISIBLE);
                            List<Column> columns = new ArrayList<>();
                            List<SubcolumnValue> values;
                            for (int i = 0; i < numColumns; ++i) {

                                values = new ArrayList<SubcolumnValue>();
                                for (int j = 0; j < numSubcolumns; ++j) {
                                    values.add(new SubcolumnValue(d.get(i).getCount(), ChartUtils.pickColor()));
                                }

                                Column column = new Column(values);
                                column.setHasLabels(true);
                                column.setHasLabelsOnlyForSelected(false);
                                columns.add(column);
                            }

                            columnChartData = new ColumnChartData(columns);

                            Axis axisX = new Axis();
                            Axis axisY = new Axis().setHasLines(true);

                            List<AxisValue> axisValues = new ArrayList<>();
                            for (int i = 0; i < d.size(); i++) {
                                axisValues.add(new AxisValue(i).setLabel(d.get(i).getTime()));
                            }
                            axisX.setValues(axisValues);
                            axisX.setName("时间");
                            axisY.setName("消费金额");

                            columnChartData.setAxisXBottom(axisX);
                            columnChartData.setAxisYLeft(axisY);
                            columnChartView.setColumnChartData(columnChartData);
                        } else {
                            dailycost.setVisibility(View.GONE);
                            columnChartView.setVisibility(View.GONE);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        disposable2.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });


    }

}
