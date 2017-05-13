package com.burning.smile.schoolhelper.photoshower;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.widget.photoview.OnSingleTapConfirmedListener;
import com.burning.smile.androidtools.widget.photoview.PhotoView;
import com.burning.smile.schoolhelper.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoShowAdapter extends PagerAdapter {
    private List<String> mPaths;
    private Context mContext;

    public PhotoShowAdapter(Context c, List<String> paths) {
        mContext = c;
        mPaths = paths;
    }

    public void addItem(Object obj) {
        if (obj instanceof String) {
            if (mPaths == null) {
                mPaths = new ArrayList<>();
            }
            mPaths.add((String) obj);
        }
    }

    public Object remove(int position) {
        return mPaths.remove(position);
    }

    @Override
    public int getItemPosition(Object object) {
//        return super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        RelativeLayout rl = new RelativeLayout(mContext);
        //MatrixImageView iv = new MatrixImageView(mContext);
        final PhotoView iv = new PhotoView(mContext);
        TextView tv = new TextView(mContext);
        ImageView iv_del = new ImageView(mContext);

        int h = 180;
        int margin = 30;
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setPadding(5, 5, margin, 5);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setText((position + 1) + "/" + getCount());
        //tv.setBackgroundColor(Color.parseColor("#24ffffff"));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tv.setLayoutParams(lp);


        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(lp);

        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(vl);

        iv.setOnSingleTapConfirmedListener(new OnSingleTapConfirmedListener() {
            @Override
            public void onSingleTapConfirmed(MotionEvent e) {
                ((Activity) mContext).finish();
            }
        });

        rl.addView(iv);
        rl.addView(tv);
        rl.addView(iv_del);
        (container).addView(rl, 0);
        if (mPaths == null || mPaths.get(position) == null || mPaths.get(position).equals("")) {
            iv.setImageResource(R.mipmap.ic_test);
        } else {
            if (mPaths.get(position).endsWith(".jpg") || mPaths.get(position).endsWith(".png") || mPaths.get(position).endsWith(".gif")) {
                if (mPaths.get(position).contains("http")) {
                    Glide.with(mContext).load(mPaths.get(position)).into(iv);
                } else {
                    Glide.with(mContext).load(new File(mPaths.get(position))).error(R.mipmap.ic_launcher).crossFade().into(iv);
                }
            } else {
                Glide.with(mContext).load(R.mipmap.ic_file).crossFade().into(iv);
            }
        }
        return rl;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
