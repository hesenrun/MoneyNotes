package com.bqmz001.moneynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bqmz001.moneynotes.data.DataCenter;
import com.bqmz001.moneynotes.entity.User;
import com.bqmz001.moneynotes.private_ui.ColorDialog;
import com.bqmz001.moneynotes.util.EventUtil;
import com.bqmz001.moneynotes.util.ToastUtil;

public class EditUserActivity extends BaseActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    Button chooseColor;
    View colorPreview;
    ColorDialog colorDialog;
    TextView title_small;
    EditText editText_userName, editText_budget;
    CheckBox checkBox;
    User user;
    int thiscolor;
    boolean colorseleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);


        toolbar = findViewById(R.id.toolbar);
        chooseColor = findViewById(R.id.button_choose_color);
        colorPreview = findViewById(R.id.color_preview);
        title_small = findViewById(R.id.textView_title_small);
        editText_userName = findViewById(R.id.editText_username);
        editText_budget = findViewById(R.id.editText_budget);
        checkBox = findViewById(R.id.checkBox);

        if (getIntent().getIntExtra("user_id", -2) > -1) {
            user = DataCenter.getUser(getIntent().getIntExtra("user_id", 0));
            editText_userName.setText(user.getName());
            editText_budget.setText(user.getBudget() + "");
            colorPreview.setBackgroundColor(user.getColor());
            checkBox.setChecked(user.isDefault());
            thiscolor = user.getColor();
            toolbar.setTitle("编辑用户");
            title_small.setText("编辑用户");
            colorseleted = true;
        } else {
            title_small.setText("新建用户");
            toolbar.setTitle("新建用户");
            colorseleted = false;
        }
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }


        colorDialog = new ColorDialog(EditUserActivity.this);
        colorDialog.setOnClickListener(new ColorDialog.OnClickListener() {
            @Override
            public void onOKClick(int color, View view) {
                colorseleted = true;
                colorPreview.setBackgroundColor(color);
                thiscolor = color;
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

    private void saveUser() {
        if (user != null) {
            user.setName(editText_userName.getText().toString());
            user.setBudget(Integer.parseInt(editText_budget.getText().toString()));
            user.setColor(thiscolor);
            user.setDefault(checkBox.isChecked());
            DataCenter.saveUser(user);
        } else {
            user = new User();
            user.setName(editText_userName.getText().toString());
            user.setBudget(Integer.parseInt(editText_budget.getText().toString()));
            user.setDefault(checkBox.isChecked());
            user.setColor(thiscolor);
            DataCenter.saveUser(user);
            DataCenter.newUserCreateClassification(user);
        }
        if (user.isDefault() == true) {
            DataCenter.reloadUser();
            EventUtil.postEvent(0, "update", "update");
        }

    }

    private boolean check() {
        if (editText_userName.getText().toString().trim().length() <= 0) {
            ToastUtil.show(ToastUtil.NO_NAME);
            return false;
        }
        if (editText_budget.getText().toString().trim().length() == 0) {
            ToastUtil.show(ToastUtil.NO_BUDGET);
            return false;
        }

        if (Integer.parseInt(editText_budget.getText().toString()) <= 0) {
            ToastUtil.show(ToastUtil.NO_BUDGET);
            return false;
        }
        if (colorseleted == false) {
            ToastUtil.show(ToastUtil.NO_COLOR);
            return false;
        }
        return true;
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
                    saveUser();
                    finish();
                }
                break;

        }
        return true;
    }
}
