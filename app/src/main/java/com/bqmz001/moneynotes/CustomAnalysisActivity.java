package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.ClassificationFakeCount;
import com.bqmz001.moneynotes.entity.DailyNoteFakeCount;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

public class CustomAnalysisActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    EditText startYear, startMonth, startDay, endYear, endMonth, endDay;
    PieChartView pieChartView;
    PieChartData pieChartData;
    ColumnChartView columnChartView;
    ColumnChartData columnChartData;
    TextView pieChartContent, dailycost;
    Disposable disposable, disposable2;
    RelativeLayout relativeLayout;
    NestedScrollView scrollView;
    Button button;
    User user;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    int iStartYear, iStartMonth, iStartDay, iEndYear, iEndMonth, iEndDay;
    long iStartTime, iEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_analysis);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("自定义查询");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        builder = new AlertDialog.Builder(this)
                .setTitle("为什么会这样？")
                .setMessage("    因为没有找到合适的滚轮控件，所以变成现在的这个样子。不好意思给大家添麻烦了！\n" +
                        "    尽管我自己看起来也是非常的不适，但在找到可替代方案之前，目前只能这样子了。\n" +
                        "    求大佬推荐好的控件！最好是加载不出错速度非常快的那种。")
                .setNegativeButton("朕已阅", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在处理");
        progressDialog.setMessage("请稍后...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        startYear = findViewById(R.id.editText_stratYear);
        startMonth = findViewById(R.id.editText_startMonth);
        startDay = findViewById(R.id.editText_startDay);
        endYear = findViewById(R.id.editText_endYear);
        endMonth = findViewById(R.id.editText_endMonth);
        endDay = findViewById(R.id.editText_endDay);

        pieChartView = findViewById(R.id.pieChartView);
        pieChartContent = findViewById(R.id.textView_pieChartContent);
        columnChartView = findViewById(R.id.columnChartView);
        dailycost = findViewById(R.id.textView_dailyCost);
        relativeLayout = findViewById(R.id.relativeLayout);
        scrollView = findViewById(R.id.scrollView);
        button = findViewById(R.id.button_ok);

        user = DataCenter.getNowUser();

        scrollView.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (check()) {
                    init(iStartTime, iEndTime);
                } else {
                    ToastUtil.show(ToastUtil.TIME_WRITE_ERROR);
                    progressDialog.dismiss();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.question:
                builder.create().show();
                break;
        }
        return true;
    }

    boolean check() {
        //文本框非空检查
        if (startYear.getText().toString().trim().length() > 0 &&
                startMonth.getText().toString().trim().length() > 0 &&
                startDay.getText().toString().trim().length() > 0 &&
                endYear.getText().toString().trim().length() > 0 &&
                endMonth.getText().toString().trim().length() > 0 &&
                endDay.getText().toString().trim().length() > 0
        ) {
            iStartYear = Integer.parseInt(startYear.getText().toString().trim());
            iStartMonth = Integer.parseInt(startMonth.getText().toString().trim());
            iStartDay = Integer.parseInt(startDay.getText().toString().trim());
            iEndYear = Integer.parseInt(endYear.getText().toString().trim());
            iEndMonth = Integer.parseInt(endMonth.getText().toString().trim());
            iEndDay = Integer.parseInt(endDay.getText().toString().trim());
            //年份检查
            if ((iStartYear > 1900 && iStartYear < 2100) && (iEndYear > 1900 && iEndYear < 2100)) {
                //月份检查
                if ((iStartMonth > 0 && iStartMonth < 13) && (iEndMonth > 0 && iEndMonth < 13)) {
                    //日期检查第一步——看看有没有超过31天
                    if ((iStartDay > 0 && iStartDay < 32) && (iEndDay > 0 && iEndDay < 32)) {
                        //日期检查第二步——看看有没有超出当月最大
                        if (iStartDay <= DateTimeUtil.getLastDayOfMonth(iStartYear, iStartMonth) && iEndDay <= DateTimeUtil.getLastDayOfMonth(iEndYear, iEndMonth)) {
                            long s = DateTimeUtil.getFirstTimeOfDay(iStartYear, iStartMonth, iStartDay);
                            long e = DateTimeUtil.getLastTimeOfDay(iEndYear, iEndMonth, iEndDay);
                            //开始日期是否超过结束日期
                            if (s < e) {
                                iStartTime = s;
                                iEndTime = e;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_question, menu);
        return true;
    }
//
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
                                column.setHasLabelsOnlyForSelected(true);
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
                        progressDialog.dismiss();
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
