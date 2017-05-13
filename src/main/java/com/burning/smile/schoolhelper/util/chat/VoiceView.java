package com.burning.smile.schoolhelper.util.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.burning.smile.androidtools.widget.record.RecordButton;
import com.burning.smile.schoolhelper.R;

public class VoiceView extends LinearLayout {

    protected View view;
    private Context mContext;
    public RecordButton recordButton;

    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_voice, this);
        recordButton = (RecordButton) view.findViewById(R.id.record);
    }

    public RecordButton getRecordBtn() {
        return recordButton;
    }
}
