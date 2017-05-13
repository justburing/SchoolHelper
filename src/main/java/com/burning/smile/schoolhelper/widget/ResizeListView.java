package com.burning.smile.schoolhelper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by BurNing on 2016/8/22.
 */
public class ResizeListView extends ListView {

    public ResizeListView(Context context) {
        super(context);
    }

    public ResizeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeListView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}