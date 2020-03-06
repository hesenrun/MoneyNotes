package com.bqmz001.moneynotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bqmz001.moneynotes.adapter.ClassificationAdapter;
import com.bqmz001.moneynotes.adapter.UserAdapter;
import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends BaseActivity {

    List<User> userList;
    List<Classification> classificationList;
    FloatingActionButton fab;
    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView recyclerView;
    String type;
    LinearLayoutManager manager;
    UserAdapter adapter_u;
    ClassificationAdapter adapter_c;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        setResult(1);
        type = getIntent().getStringExtra("type");
        if (type.equals("user"))
            loadUser();
        else if (type.equals("classification"))
            loadClassification();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DataCenter.refreshUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DataCenter.refreshUser();
                finish();
                break;
        }
        return true;
    }


    private void loadUser() {
        userList = DataCenter.getUserList();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("管理用户");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        manager = new LinearLayoutManager(ManageActivity.this);
        recyclerView = findViewById(R.id.recyclerView_manage);
        recyclerView.setLayoutManager(manager);
        adapter_u = new UserAdapter(userList);
        recyclerView.setAdapter(adapter_u);
        adapter_u.setClickListener(new UserAdapter.OnClickListener() {
            @Override
            public void onClick(int position, View v) {
                DataCenter.setNowUser(DataCenter.getUser(position));
                setResult(1);
                finish();
            }
        });
        adapter_u.setLongClickListener(new UserAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, View v) {
                final int p = position;
//                Toast.makeText(ManageActivity.this,"long position:"+position,Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(ManageActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuitem_setDefault:
                                DataCenter.setDefaultUser(DataCenter.getUser(p));
                                forceRefresh();
                                DataCenter.reloadUser();
                                EventUtil.postEvent(0,"update","update");
                                break;
                            case R.id.menuitem_edit:
                                intent = new Intent(ManageActivity.this, EditUserActivity.class);
                                intent.putExtra("user_id", p);
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.menuitem_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this)
                                        .setTitle("提示")
                                        .setMessage("确定要删除吗？")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (DataCenter.getUser(p).isDefault() || p == DataCenter.getNowUser().getId()) {
                                                    ToastUtil.show(ToastUtil.DO_NOT_DELETE_DEFAULT_OR_NOW);
                                                    dialog.dismiss();
                                                } else {
                                                    DataCenter.deleteUser(DataCenter.getUser(p));
                                                    forceRefresh();
                                                    ToastUtil.show(ToastUtil.SUCCESS_DEL);
                                                    dialog.dismiss();
                                                }
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
                return true;
            }
        });


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ManageActivity.this, EditUserActivity.class);
                intent.putExtra("user_id", -1);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceRefresh();
    }

    private void loadClassification() {
        classificationList = DataCenter.getClassificationList(DataCenter.getNowUser());
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("管理分类");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        manager = new LinearLayoutManager(ManageActivity.this);
        recyclerView = findViewById(R.id.recyclerView_manage);
        recyclerView.setLayoutManager(manager);
        adapter_c = new ClassificationAdapter(classificationList);
        recyclerView.setAdapter(adapter_c);
        adapter_c.setClickListener(new ClassificationAdapter.OnClickListener() {
            @Override
            public void onClick(int position, View v) {
                intent = new Intent(ManageActivity.this, EditClassificationActivity.class);
                intent.putExtra("classification_id", position);
                startActivity(intent);
            }
        });
        adapter_c.setLongClickListener(new ClassificationAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, View v) {
                final int p = position;
                PopupMenu popupMenu = new PopupMenu(ManageActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu2, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuitem_edit:
                                intent = new Intent(ManageActivity.this, EditClassificationActivity.class);
                                intent.putExtra("classification_id", p);
                                startActivity(intent);
                                break;
                            case R.id.menuitem_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this)
                                        .setTitle("提示")
                                        .setMessage("确定要删除吗？")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (classificationList.size() < 2) {
                                                    ToastUtil.show(ToastUtil.AT_LEAST_ONE_CLASSIFICATION);
                                                    dialog.dismiss();
                                                } else {
                                                    DataCenter.deleteClassification(DataCenter.getClassification(p));
                                                    forceRefresh();
                                                    ToastUtil.show(ToastUtil.SUCCESS_DEL);
                                                    dialog.dismiss();
                                                }
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
                return true;
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ManageActivity.this, EditClassificationActivity.class);
                intent.putExtra("classification_id", -1);
                startActivity(intent);
            }
        });
    }

    private void forceRefresh() {
        if (type.equals("user")) {
            userList.clear();
            userList.addAll(DataCenter.getUserList());
            adapter_u.notifyDataSetChanged();
        } else if (type.equals("classification")) {
            classificationList.clear();
            classificationList.addAll(DataCenter.getClassificationList(DataCenter.getNowUser()));
            adapter_c.notifyDataSetChanged();
        }
    }


}
