package com.bqmz001.uilibaray;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;

public class CircleProgress extends View {

    private Paint paint; //默认画笔
    private float side_width = 100; //长宽都这个
    private int background_color = Color.parseColor("#fefefe"); //背景色
    private int circle_background_color = Color.parseColor("#dddddd"); //圆环背景色
    private int circle_foreground_color = Color.parseColor("#03A9F4"); //圆环前景色
    private int font_color = Color.parseColor("#03A9F4"); //文字颜色
    private float progress = 0.00001f;//进度
    private Rect rect;
    private String text = "0.0%";

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        background_color = array.getColor(R.styleable.CircleProgress_backgroundColor, Color.parseColor("#00000000"));
        circle_background_color = array.getColor(R.styleable.CircleProgress_circleBackgroundColor, Color.parseColor("#dddddd"));
        circle_foreground_color = array.getColor(R.styleable.CircleProgress_circleForegroundColor, Color.parseColor("#03A9F4"));
        font_color = array.getColor(R.styleable.CircleProgress_fontColor, Color.parseColor("#03A9F4"));
        array.recycle();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        if (progress >= 0)
            this.progress = progress;
        else if (progress >= 1)
            this.progress = 1;
        else
            this.progress = 0.00001f;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int hMode=MeasureSpec.getMode(heightMeasureSpec);


        if (w > h) {
            side_width = h;
            setMeasuredDimension(h, h);
        } else {
            side_width = w;
            setMeasuredDimension(w, w);
        }
        if (hMode==MeasureSpec.UNSPECIFIED&&h==0){
            side_width = w;
            setMeasuredDimension(w, w);
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(background_color);
        paint.setShadowLayer(10, 0, 1, Color.parseColor("#dedede"));
        canvas.drawRect(side_width * 0.05f, side_width * 0.05f, side_width * 0.95f, side_width * 0.95f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(side_width * 0.07f);
        paint.setShadowLayer(0, 0, 0, 0);
        paint.setColor(circle_background_color);
        canvas.drawCircle(side_width * 0.5f, side_width * 0.5f, side_width * 0.3f, paint);
        paint.setColor(circle_foreground_color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(side_width * 0.2f, side_width * 0.2f, side_width * 0.8f, side_width * 0.8f, -90, 360 * progress, false, paint);
        }

        rect = new Rect();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(font_color);
        paint.setTextSize(side_width * 0.12f);
        paint.setStrokeWidth(0);

        text = Double.parseDouble(new DecimalFormat("#.0").format(progress * 100)) + "%";
        paint.getTextBounds(text, 0, text.length(), rect);
        paint.setFakeBoldText(true);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, side_width / 2 - rect.width() / 2, baseline, paint);

    }
}
