package com.bqmz001.moneynotes.private_ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bqmz001.moneynotes.R;
import com.shixia.colorpickerview.ColorPickerView;
import com.shixia.colorpickerview.OnColorChangeListener;

public class ColorDialog extends Dialog {

    Context context;
    int color2;
    ColorPickerView colorPickerView;
    TextView textView_color;
    Button ok, cancel;

    private ColorDialog.OnClickListener onClickListener;

    public interface OnClickListener {
        void onOKClick(int color, View view);

        void onCancelClick(View view);
    }


    public ColorDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);//设置dialog显示居中
        setContentView(R.layout.dialog_color);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        final Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 9 / 10;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失

        colorPickerView = findViewById(R.id.colorpickerView);
        textView_color = findViewById(R.id.textView_colorHex);
        ok = findViewById(R.id.button_ok);
        cancel = findViewById(R.id.button_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onOKClick(color2,v);
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCancelClick(v);
                dismiss();
            }
        });
        colorPickerView.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void colorChanged(int color) {
                color2 = color;
                textView_color.setText(color+"");

            }
        });


    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
