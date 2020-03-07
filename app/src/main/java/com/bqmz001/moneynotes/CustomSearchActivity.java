package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class CustomSearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    EditText startYear, startMonth, startDay, endYear, endMonth, endDay;
    int iStartYear, iStartMonth, iStartDay, iEndYear, iEndMonth, iEndDay;
    long iStartTime, iEndTime;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
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
        classifications=new ArrayList<>();
        Classification c = new Classification();
        c.setId(-2);
        c.setColor(Color.parseColor("#efefef"));
        c.setName("全部");
        classifications.add(c);
        classifications.addAll(DataCenter.getClassificationList(user));

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
        textView = findViewById(R.id.textView);
        recyclerView=findViewById(R.id.recyclerView_history);
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
                                intent.putExtra("from","app");
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
                                                EventUtil.postEvent(0,"update","update");
                                                if(check()){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_question, menu);
        return true;
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
                        if (noteList.size()>0&&recyclerView.getVisibility()==View.VISIBLE){
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

}
