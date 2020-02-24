package com.bqmz001.moneynotes.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bqmz001.moneynotes.CustomSearchActivity;
import com.bqmz001.moneynotes.EditNoteActivity;
import com.bqmz001.moneynotes.EditUserActivity;
import com.bqmz001.moneynotes.ManageActivity;
import com.bqmz001.moneynotes.NoteDetailActivity;
import com.bqmz001.moneynotes.R;
import com.bqmz001.moneynotes.adapter.FlowAdapter;
import com.bqmz001.moneynotes.assembly.ViewPagerFragment;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends ViewPagerFragment {

    RecyclerView recyclerView;
    FlowAdapter adapter;
    List<Note> noteList;
    SwipeRefreshLayout swipeRefreshLayout;
    Intent intent;
    Disposable disposable, disposable2, disposable3, disposable4;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textView;
    RelativeLayout relativeLayout;
    Button button;
    int type = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView_history);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        radioGroup = view.findViewById(R.id.radioGroup_type);
        radioButton = view.findViewById(R.id.radioButton_today);
        textView = view.findViewById(R.id.textView_total);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        button = view.findViewById(R.id.button);
        noteList = new ArrayList<>();
        adapter = new FlowAdapter(noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setClickListener(new FlowAdapter.OnClickListener() {
            @Override
            public void onClick(int position, View v) {
                intent = new Intent(getContext(), NoteDetailActivity.class);
                intent.putExtra("note_id", position);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomSearchActivity.class));
            }
        });
        adapter.setLongClickListener(new FlowAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, View v) {
                final int p = position;
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu3, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.viewthis:
                                intent = new Intent(getContext(), NoteDetailActivity.class);
                                intent.putExtra("note_id", p);
                                startActivity(intent);
                                break;
                            case R.id.edit:
                                intent = new Intent(getContext(), EditNoteActivity.class);
                                intent.putExtra("note_id", p);
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setTitle("提示")
                                        .setMessage("确定要删除吗？")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DataCenter.deleteNote(DataCenter.getNote(p));
                                                refresh();
                                                Toast.makeText(getContext(), "删除完成", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_today:
                        swipeRefreshLayout.setRefreshing(true);
                        getTodayNote();
                        break;
                    case R.id.radioButton_thisWeek:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisWeekNote();
                        break;
                    case R.id.radioButton_thisMonth:
                        swipeRefreshLayout.setRefreshing(true);
                        getThisMonthNote();
                        break;
                }
            }
        });
        radioButton.setChecked(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refreshView(List<Note> _noteList) {
        noteList.clear();
        noteList.addAll(_noteList);
        adapter.notifyDataSetChanged();
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        switch (type) {
            case 1:
                swipeRefreshLayout.setRefreshing(true);
                getTodayNote();
                break;
            case 2:
                swipeRefreshLayout.setRefreshing(true);
                getThisWeekNote();
                break;
            case 3:
                swipeRefreshLayout.setRefreshing(true);
                getThisMonthNote();
                break;
        }
    }


    private void getTodayNote() {

        type = 1;
        disposable = Observable.just(new ArrayList<Note>())
                .map(new Function<List<Note>, List<Note>>() {
                    @Override
                    public List<Note> apply(List<Note> noteList) throws Exception {
                        noteList = DataCenter.getNoteList(DataCenter.getNowUser(), DateTimeUtil.getNowDayStartTimeStamp(), DateTimeUtil.getNowDayEndTimeStamp());
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

    private void getThisWeekNote() {
        type = 2;
        disposable3 = Observable.just(DataCenter.getNoteList(DataCenter.getNowUser(), DateTimeUtil.getFirstTimeOfThisWeek(), DateTimeUtil.getLastTimeOfThisWeek()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> noteList) throws Exception {
                        refreshView(noteList);
                        calcTotal();
                        disposable3.dispose();


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

    private void getThisMonthNote() {
        type = 3;
        disposable4 = Observable.just(DataCenter.getNoteList(DataCenter.getNowUser(), DateTimeUtil.getFirstTimeOfThisMonth(), DateTimeUtil.getLastTimeOfThisMonth()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> noteList) throws Exception {
                        refreshView(noteList);
                        calcTotal();
                        disposable4.dispose();


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
                            textView.setText("支出：" + f + "元");
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            textView.setText("支出：" + f + "元");
                            recyclerView.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                        swipeRefreshLayout.setRefreshing(false);
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
