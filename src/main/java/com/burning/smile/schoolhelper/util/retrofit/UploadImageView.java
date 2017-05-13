package com.burning.smile.schoolhelper.util.retrofit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class UploadImageView extends ImageView {

    private int mPercent = 0;   //上传百分比

    private Paint mPaint;   //表层的颜色画布
    private int mColor = Color.parseColor("#66000000");


    public UploadImageView(Context context) {
        this(context, null);
    }

    public UploadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UploadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight() * (100 - mPercent) / 100, mPaint);
    }

    public void updatePercent(int percent) {
        this.mPercent = percent;
        if (mPercent < 0) mPercent = 0;
        if (mPercent > 100) mPercent = 100;
        invalidate();
    }


}