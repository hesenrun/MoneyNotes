package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bqmz001.moneynotes.adapter.FlowAdapter;
import com.bqmz001.moneynotes.adapter.SpCfMenuAdapter;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class CustomSearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    TimePickerView stp, etp;
    TextView tv_sp, tv_ep;
    long iStartTime, iEndTime;
    ProgressDialog progressDialog;

    RelativeLayout relativeLayout;
    RecyclerView recyclerView;
    FlowAdapter adapter;
    List<Note> noteList;
    List<Classification> classifications;
    Classification classification;
    Intent intent;
    Button button;
    TextView textView;
    User user;
    Spinner spinner;
    SpCfMenuAdapter adapter2;
    Disposable disposable, disposable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_search);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("自定义查询");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        user = DataCenter.getNowUser();
        classifications = new ArrayList<>();
        Classification c = new Classification();
        c.setId(-2);
        c.setColor(Color.parseColor("#efefef"));
        c.setName("全部");
        classifications.add(c);
        classifications.addAll(DataCenter.getClassificationList(user));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在处理");
        progressDialog.setMessage("请稍后...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        tv_sp = findViewById(R.id.textView_startTime);
        tv_ep = findViewById(R.id.textView_endTime);
        initStartTimePicker();
        initEndTimePicker();
        iStartTime=new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
        iEndTime=new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).getMillis();
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
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView_history);
        relativeLayout = findViewById(R.id.relativeLayout);
        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.button_ok);

        adapter2 = new SpCfMenuAdapter(CustomSearchActivity.this, classifications);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classification = classifications.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

        noteList = new ArrayList<>();
        adapter = new FlowAdapter(noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomSearchActivity.this));
        adapter.setClickListener(new FlowAdapter.OnClickListener() {
            @Override
            public void onClick(int position, View v) {
                intent = new Intent(CustomSearchActivity.this, NoteDetailActivity.class);
                intent.putExtra("note_id", position);
                startActivity(intent);
            }
        });
        adapter.setLongClickListener(new FlowAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, View v) {
                final int p = position;
                PopupMenu popupMenu = new PopupMenu(CustomSearchActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu3, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.viewthis:
                                intent = new Intent(CustomSearchActivity.this, NoteDetailActivity.class);
                                intent.putExtra("note_id", p);

                                startActivity(intent);
                                break;
                            case R.id.edit:
                                intent = new Intent(CustomSearchActivity.this, EditNoteActivity.class);
                                intent.putExtra("note_id", p);
                                intent.putExtra("from", "app");
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomSearchActivity.this)
                                        .setTitle("提示")
                                        .setMessage("确定要删除吗？")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DataCenter.deleteNote(DataCenter.getNote(p));
                                                progressDialog.show();
                                                Toast.makeText(CustomSearchActivity.this, "删除完成", Toast.LENGTH_SHORT).show();
                                                EventUtil.postEvent(0, "update", "update");
                                                if (check()) {
                                                    getNote(iStartTime, iEndTime, user, classification);
                                                }
                                                dialog.dismiss();
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();


                                break;
                        }
                        return false;
                    }


                });
                popupMenu.show();
                return false;
            }
        });
        recyclerView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (check()) {
                    getNote(iStartTime, iEndTime, user, classification);
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

    private void refreshView(List<Note> _noteList) {
        noteList.clear();
        noteList.addAll(_noteList);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private void getNote(final long startTime, final long endTime, final User user, final Classification classification) {

        disposable = Observable.just(new ArrayList<Note>())
                .map(new Function<List<Note>, List<Note>>() {
                    @Override
                    public List<Note> apply(List<Note> noteList) throws Exception {

                        if (classification.getId() < 0)
                            noteList = DataCenter.getNoteList(user, startTime, endTime);
                        else
                            noteList = DataCenter.getNoteList(user, startTime, endTime, classification);
                        return noteList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> noteList) throws Exception {

                        refreshView(noteList);
                        calcTotal();
                        disposable.dispose();


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }

    private void calcTotal() {
        disposable2 = Observable.just(noteList)
                .subscribeOn(Schedulers.io())
                .map(new Function<List<Note>, Float>() {
                    @Override
                    public Float apply(List<Note> noteList) throws Exception {
                        Float i = new Float(0);
                        for (Note note : noteList) {
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
                        if (f > 0 || noteList.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.GONE);
                            textView.setText("支出：" + new BigDecimal(f).setScale(2, RoundingMode.HALF_UP).toString() + "元");

                        } else {
                            textView.setText("支出：" + new BigDecimal(f).setScale(2, RoundingMode.HALF_UP).toString() + "元");
                            recyclerView.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                        if (noteList.size() > 0 && recyclerView.getVisibility() == View.VISIBLE) {
                            recyclerView.scrollToPosition(0);
                        }
                        disposable2.dispose();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
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
