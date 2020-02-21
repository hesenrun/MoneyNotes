package com.bqmz001.moneynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.Classification;
import com.bqmz001.moneynotes.private_ui.ColorDialog;
import com.bqmz001.moneynotes.util.ToastUtil;

public class EditClassificationActivity extends BaseActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    Button chooseColor;
    View colorPreview;
    ColorDialog colorDialog;
    EditText editText_classificationName;
    int thisColor;
    Classification classification;
    TextView title_small;
    boolean colorseleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_classification);
        toolbar = findViewById(R.id.toolbar);
        chooseColor = findViewById(R.id.button_choose_color);
        colorPreview = findViewById(R.id.color_preview);
        title_small = findViewById(R.id.textView_title_small);
        colorDialog = new ColorDialog(EditClassificationActivity.this);
        editText_classificationName = findViewById(R.id.editText_classification_name);
        if (getIntent().getIntExtra("classification_id", -2) > -1) {
            classification = DataCenter.getClassification(getIntent().getIntExtra("classification_id", 0));
            editText_classificationName.setText(classification.getName());
            colorPreview.setBackgroundColor(classification.getColor());
            thisColor = classification.getColor();
            colorseleted = true;
            toolbar.setTitle("编辑分类");
            title_small.setText("编辑分类");
        } else {
            toolbar.setTitle("新建分类");
            title_small.setText("新建分类");
            colorseleted = false;
        }


        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        colorDialog.setOnClickListener(new ColorDialog.OnClickListener() {
            @Override
            public void onOKClick(int color, View view) {
                colorseleted = true;
                colorPreview.setBackgroundColor(color);
                thisColor = color;
            }

            @Override
            public void onCancelClick(View view) {
            }
        });


        chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog.show();
            }
        });

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
                if (check()) {
                    saveClassification();
                    finish();
                }
                break;

        }
        return true;
    }

    private boolean check() {
        if (editText_classificationName.getText().toString().trim().length() <= 0) {
            ToastUtil.show(ToastUtil.NO_NAME);
            return false;
        }
        if (colorseleted==false){
            ToastUtil.show(ToastUtil.NO_COLOR);
            return false;
        }
        return true;
    }

    private void saveClassification() {
        if (classification != null) {
            classification.setName(editText_classificationName.getText().toString().trim());
            classification.setColor(thisColor);
            classification.setUser(DataCenter.getNowUser());
            DataCenter.saveClassification(classification);
        } else {
            classification = new Classification();
            classification.setName(editText_classificationName.getText().toString().trim());
            classification.setColor(thisColor);
            classification.setUser(DataCenter.getNowUser());
            DataCenter.saveClassification(classification);
        }
    }
}
