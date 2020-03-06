package com.bqmz001.moneynotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Note;
import com.bqmz001.moneynotes.util.DateTimeUtil;
import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

public class NoteDetailActivity extends BaseActivity {
    TextView name, cost, time, classification_name, summary;
    View classification_color;
    Note note;
    int note_id;
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        name = findViewById(R.id.textView_name);
        cost = findViewById(R.id.textView_cost);
        time = findViewById(R.id.textView_time);
        classification_name = findViewById(R.id.textView_classification_name);
        summary = findViewById(R.id.textView_summary);
        classification_color = findViewById(R.id.view_classification_color);

        note_id = getIntent().getIntExtra("note_id", -2);
        note = DataCenter.getNote(note_id);

        name.setText(note.getNote());
        cost.setText(note.getCost() + "");
        time.setText(DateTimeUtil.timestampToDate(note.getTime()));
        classification_color.setBackgroundColor(note.getClassification().getColor());
        summary.setText(note.getSummary());
        classification_name.setText(note.getClassification().getName());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.edit:
                Intent intent = new Intent(NoteDetailActivity.this, EditNoteActivity.class);
                intent.putExtra("note_id", note_id);
                intent.putExtra("from","app");
                startActivity(intent);
                finish();
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要删除吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCenter.deleteNote(note);
                                finish();
                                ToastUtil.show(ToastUtil.SUCCESS_DEL);
                                EventUtil.postEvent(0,"update","update");
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
        return true;
    }
}
