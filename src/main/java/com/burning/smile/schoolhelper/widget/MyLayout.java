package com.burning.smile.schoolhelper.widget;

import android.content.Context;
import android.util.AttributeSet;

import sj.keyboard.widget.AutoHeightLayout;

/**
 * Created by smile on 2017/5/1.
 */
public class MyLayout extends AutoHeightLayout {
    private int popupWindowHeight;

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int i) {
        popupWindowHeight = i;
    }

    public int getPopupWindowHeight() {
        return popupWindowHeight;
    }

}
