package com.bqmz001.moneynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bqmz001.moneynotes.adapter.SpCfMenuAdapter;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.private_ui.DateTimeDialog;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNoteActivity extends BaseActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    Spinner spinner;
    SpCfMenuAdapter adapter;
    Button getNow, setTime;
    TextView title_small;
    EditText editText_time, editText_cost, editText_content, editText_summary;
    List<Classification> classifications;
    //    DateTimeDialog dialog;
    TimePickerView tp;
    User user;


    Classification classification;
    Note note;
    long thisTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        if (getIntent().getStringExtra("from").equals("widget") || getIntent().getStringExtra("from").equals("notification")) {
            user = DataCenter.getDefaultUser();
        } else if (getIntent().getStringExtra("from").equals("app")) {
            user = DataCenter.getNowUser();
        }

        classifications = DataCenter.getClassificationList(user);
        getNow = findViewById(R.id.button_now);
        setTime = findViewById(R.id.button_choose_time);
        title_small = findViewById(R.id.textView_title_small);
        editText_time = findViewById(R.id.editText_time);
        editText_cost = findViewById(R.id.editText_cost);
        editText_content = findViewById(R.id.editText_content);
        editText_summary = findViewById(R.id.editText_summary);
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner_classification);
        initTimePicker();

//        dialog = new DateTimeDialog(EditNoteActivity.this);
        adapter = new SpCfMenuAdapter(EditNoteActivity.this, classifications);
        editText_time.setFocusable(false);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classification = classifications.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (getIntent().getIntExtra("note_id", -2) > -1) {
            note = DataCenter.getNote(getIntent().getIntExtra("note_id", -2));
            editText_time.setText(DateTimeUtil.timestampToDate(note.getTime()));
            editText_cost.setText(note.getCost() + "");
            editText_content.setText(note.getNote());
            editText_summary.setText(note.getSummary());
            for (int i = 0; i < classifications.size(); i++) {
                if (note.getClassification().getId() == classifications.get(i).getId()) {
                    spinner.setSelection(i, true);
                    break;
                }

            }
            thisTime = note.getTime();
            toolbar.setTitle("编辑帐单");
            title_small.setText("编辑帐单");
        } else {
            toolbar.setTitle("记一笔");
            title_small.setText("记一笔");
        }

//        dialog.setOnClickListener(new DateTimeDialog.OnClickListener() {
//            @Override
//            public void ok(long time, View view) {
//                thisTime = time;
//                editText_time.setText(DateTimeUtil.timestampToDate(thisTime));
//            }
//
//            @Override
//            public void cancel(View view) {
//
//            }
//        });
        getNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisTime = DateTimeUtil.getNow();
                editText_time.setText(DateTimeUtil.timestampToDate(thisTime));
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.show();
                tp.show();
            }
        });

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ok:
                if (check())
                    if (getIntent().getIntExtra("note_id", -2) > -1) {
                        note.setNote(editText_content.getText().toString());
                        note.setCost(Float.parseFloat(editText_cost.getText().toString()));
                        note.setClassification(classification);
                        note.setUser(DataCenter.getNowUser());
                        note.setTime(thisTime);
                        if (editText_summary.getText().toString().trim().length() == 0) {
                            note.setSummary("暂无备注");
                        } else {
                            note.setSummary(editText_summary.getText().toString());
                        }
                        DataCenter.saveNote(note);
                        if (user.isDefault() == true) {
                            EventUtil.postEvent(0, "update", "update");
                        }
                        finish();
                    } else {
                        note = new Note();
                        note.setNote(editText_content.getText().toString());
                        note.setCost(Float.parseFloat(editText_cost.getText().toString()));
                        note.setClassification(classification);
                        note.setUser(user);
                        note.setTime(thisTime);
                        if (editText_summary.getText().toString().trim().length() == 0) {
                            note.setSummary("暂无备注");
                        } else {
                            note.setSummary(editText_summary.getText().toString());
                        }
                        DataCenter.saveNote(note);
                        if (user.isDefault() == true) {
                            EventUtil.postEvent(0, "update", "update");
                        }
                        finish();

                    }


//                Toast.makeText(EditNoteActivity.this, "name:" + editText_content.getText() + "\n" +
//                        "time:" + thisTime + "\n" +
//                        "cost:" + editText_cost.getText() + "\n" +
//                        "classification:" + classification.getName() + "\n" +
//                        "summary:" + editText_summary.getText(), Toast.LENGTH_SHORT).show();
//                finish();

                break;

        }
        return true;
    }

    private void initTimePicker() {
        tp = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                thisTime = new DateTime(date).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
                editText_time.setText(DateTimeUtil.timestampToDate(thisTime));
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
                                tp.returnData();
                                tp.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tp.dismiss();
                            }
                        });
                    }
                })

                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2.5f)
                .setItemVisibleCount(5)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .isDialog(true)
                .build();
        Dialog dialog = tp.getDialog();
        if (dialog != null) {

            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);//改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f);
        }

    }


    public boolean check() {
        if (editText_time.getText().length() == 0) {
            ToastUtil.show(ToastUtil.NO_TIME);
            return false;
        }
        if (editText_cost.getText().toString().trim().length() == 0 || Float.parseFloat(editText_cost.getText().toString().trim()) < 0) {
            ToastUtil.show(ToastUtil.NO_COST);
            return false;
        }
        if (editText_content.getText().toString().trim().length() <= 0) {
            ToastUtil.show(ToastUtil.NO_CONTENT);
            return false;
        }
        return true;
    }
}
