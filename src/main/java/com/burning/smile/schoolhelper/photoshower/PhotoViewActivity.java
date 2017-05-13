package com.burning.smile.schoolhelper.photoshower;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.schoolhelper.R;

import java.util.ArrayList;


public class PhotoViewActivity extends BaseActivity {
    ArrayList<String> pics = new ArrayList<>();

    @Override
    protected void init() {
        LinearLayout back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        PhotoShowAdapter mAdapter;
        pics = getIntent().getStringArrayListExtra("pics");
        mAdapter = new PhotoShowAdapter(this, pics);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.photo_view;
    }
}
