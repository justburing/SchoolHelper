package com.burning.smile.schoolhelper.util.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.burning.smile.schoolhelper.R;

public class MoreFuctionView extends LinearLayout {

    protected View view;
    public LinearLayout location;
    public LinearLayout file;

    public MoreFuctionView(Context context) {
        this(context, null);
    }

    public MoreFuctionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_more_func, this);
        location = (LinearLayout) view.findViewById(R.id.location);
        file = (LinearLayout) view.findViewById(R.id.file);
    }

    public LinearLayout getLocationView() {
        return location;
    }

    public LinearLayout getFileView() {
        return file;
    }
}
