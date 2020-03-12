package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    TimePickerView stp, etp;
    TextView tv_sp, tv_ep;
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在处理");
        progressDialog.setMessage("请稍后...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        tv_sp = findViewById(R.id.textView_startTime);
        tv_ep = findViewById(R.id.textView_endTime);
        initStartTimePicker();
        initEndTimePicker();
        iStartTime = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
        iEndTime = new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).getMillis();
        tv_sp.setText(DateTimeUtil.timestampToDate(iStartTime).substring(0, 10));
        tv_ep.setText(DateTimeUtil.timestampToDate(iEndTime).substring(0, 10));

        tv_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stp.show();
            }
        });
        tv_ep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etp.show();
            }
        });

        pieChartView = findViewById(R.id.pieChartView);
        pieChartContent = findViewById(R.id.textView_pieChartContent);
        columnChartView = findViewById(R.id.columnChartView);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            float x0 = 0f;
            float y0 = 0f;
            float ratio = 2.5f;

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
                        //防ScrollView
                        scrollView.requestDisallowInterceptTouchEvent(dx * ratio > dy);
                        break;
                }
                return false;
            }
        };
        columnChartView.setOnTouchListener(touchListener);
        pieChartView.setOnTouchListener(touchListener);

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

        }
        return true;
    }

    boolean check() {
        if (iStartTime != 0 && iEndTime != 0)
            return iStartTime < iEndTime;
        return false;

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
                            dailyNoteFakeCount.setTime(DateTimeUtil.timestampToDate((startTime + ((long) i * 86400000))).substring(5, 10));
                            dailyNoteFakeCount.setStartTime(startTime + ((long) i * 86400000));
                            dailyNoteFakeCount.setStopTime(startTime + (((long) i + 1) * 86400000) - 1);
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
                        List<DailyNoteFakeCount> d = new ArrayList<>();
                        for (DailyNoteFakeCount dnf : dailyNoteFakeCounts) {
                            if (dnf.getCount() > 0) {
                                d.add(dnf);
                            }
                        }

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
                        if (scrollView.getVisibility() == View.VISIBLE) {
                            scrollView.smoothScrollTo(0, 0);
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

    private void initStartTimePicker() {
        stp = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                iStartTime = new DateTime(date).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
                tv_sp.setText(DateTimeUtil.timestampToDate(iStartTime).substring(0, 10));
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        Button ok = v.findViewById(R.id.button_ok);
                        Button cancel = v.findViewById(R.id.button_cancel);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stp.returnData();
                                stp.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stp.dismiss();
                            }
                        });
                    }
                })

                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMinuteOfHour(0).toCalendar(Locale.getDefault()))
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2.5f)
                .setItemVisibleCount(5)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .isDialog(true)
                .build();
        Dialog dialog = stp.getDialog();
        if (dialog != null) {

            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);//改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f);
        }

    }

    private void initEndTimePicker() {
        etp = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                iEndTime = new DateTime(date).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).getMillis();
                tv_ep.setText(DateTimeUtil.timestampToDate(iEndTime).substring(0, 10));
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        Button ok = v.findViewById(R.id.button_ok);
                        Button cancel = v.findViewById(R.id.button_cancel);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                etp.returnData();
                                etp.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                etp.dismiss();
                            }
                        });
                    }
                })
                .setDate(new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).toCalendar(Locale.getDefault()))
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2.5f)
                .setItemVisibleCount(5)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .isDialog(true)
                .build();
        Dialog dialog = etp.getDialog();
        if (dialog != null) {

            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);//改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f);
        }

    }
}
