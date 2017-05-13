package com.burning.smile.schoolhelper.myfunk;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.schoolhelper.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smile on 2017/4/26.
 */
public class MyFunkActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.viewPagerTabs)
    TabLayout viewPagerTabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    @Override
    protected void init() {
        ButterKnife.bind(this);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加页卡视图
        mViewList.add(new TextView(this));
        mViewList.add(new TextView(this));

        mTitleList.add("我出售的");
        mTitleList.add("我购买的");
        viewPagerTabs.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        viewPagerTabs.addTab(viewPagerTabs.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        viewPagerTabs.addTab(viewPagerTabs.newTab().setText(mTitleList.get(1)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        viewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        viewPagerTabs.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        viewPagerTabs.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_funk_act;
    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }
}
