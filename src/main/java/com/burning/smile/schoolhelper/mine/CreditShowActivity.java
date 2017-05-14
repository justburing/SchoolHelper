package com.burning.smile.schoolhelper.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.widget.GradeProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smile on 2017/5/14.
 */
public class CreditShowActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.toolbarLL)
    LinearLayout toolbarLL;
    @BindView(R.id.gradeProgressView)
    GradeProgressView gradeProgressView;

    private int progress = 0;
    private int credit;
    private Thread thread;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        toolbarLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        credit = getIntent().getIntExtra("credit", 0);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress<=credit){
                    try {
                        Thread.sleep(30);
                        gradeProgressView.setProgress(progress);
                        progress += 3;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.credit_show_act;
    }

}
