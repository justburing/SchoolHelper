package com.burning.smile.schoolhelper.myexpress;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
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
public class MyExpressActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.viewPagerTabs)
    TabLayout viewPagerTabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private List<Fragment> mViewList = new ArrayList<>();//页卡视图集合

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
        mViewList.add(MinePublishFragment.newInstance());
        mViewList.add(MineReceiverFragment.newInstance());

        mTitleList.add("我发起的");
        mTitleList.add("我接受的");
        viewPagerTabs.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        viewPagerTabs.addTab(viewPagerTabs.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        viewPagerTabs.addTab(viewPagerTabs.newTab().setText(mTitleList.get(1)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        viewPagerTabs.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        viewPagerTabs.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_express_act;
    }


    class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mViewList.get(position);
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
