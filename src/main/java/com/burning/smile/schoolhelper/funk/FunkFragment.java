package com.burning.smile.schoolhelper.funk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.pulltorefresh.OnRefreshListener;
import com.burning.smile.androidtools.pulltorefresh.RefreshLoadListView;
import com.burning.smile.androidtools.refreshswipe.RefreshTime;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.DrawerOpreator;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.addfunk.AddFunkActivity;
import com.burning.smile.schoolhelper.data.FunkListBean;
import com.burning.smile.schoolhelper.funkdetail.FunkDetailActiviity;
import com.burning.smile.schoolhelper.funksearch.FunkSearchActivity;
import com.burning.smile.schoolhelper.photoshower.PhotoViewActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.widget.SegmentedGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/14.
 */
public class FunkFragment extends Fragment implements FunkContract.View, OnRefreshListener {


    @BindView(R.id.menuIcon)
    ImageView menuIcon;
    @BindView(R.id.newsest)
    RadioButton newsest;
    @BindView(R.id.hostest)
    RadioButton hostest;
    @BindView(R.id.segmentGroup)
    SegmentedGroup segmentGroup;
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.funkLv)
    RefreshLoadListView funkLv;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.noDataImage)
    ImageView noDataImage;


    private ListViewAdapter mAdapter;
    private List<FunkListBean.Funk> mFunks;
    private DrawerOpreator mDrawerOpreator;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int start = 0;
    private int limit = 10;
    private int loadStart = limit;
    private FunkContract.Presenter mPresenter;
    private String orderBy = "updated_time desc";

    public static FunkFragment newInstance() {
        FunkFragment fragment = new FunkFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.funk_frag, null);
        ButterKnife.bind(this, view);
        mAdapter = new ListViewAdapter(getActivity());
        funkLv.setAdapter(mAdapter);
        funkLv.setPullLoadEnable(true);
        funkLv.setOnRefreshListener(this);
        segmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.newsest:
                        orderBy = "updated_time desc";
                        mPresenter.getFunks(start, limit, orderBy);
                        break;
                    case R.id.hostest:
                        orderBy = "hits desc";
                        mPresenter.getFunks(start, limit, orderBy);
                        break;
                }
            }
        });
        new FunkPresenter(this).start();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDrawerOpreator = (DrawerOpreator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DrawerOpreator");
        }
    }


    @OnClick({R.id.menuIcon, R.id.add, R.id.search, R.id.refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuIcon:
                if (mDrawerOpreator.isOpening()) {
                    mDrawerOpreator.closeDrawer();
                } else {
                    mDrawerOpreator.openDrawer();
                }
                break;
            case R.id.add:
                startActivity(new Intent(getActivity(), AddFunkActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(getActivity(), FunkSearchActivity.class));
                break;
            case R.id.refresh:
                mPresenter.getFunks(start, limit, orderBy);
                break;
        }
    }

    @Override
    public void showLoadingView() {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
    }

    @Override
    public void dimissLoadingView() {
        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
    }

    @Override
    public void getDataSuccess(String msg, List<FunkListBean.Funk> funks) {
        mFunks = funks;
        if (mFunks != null && mFunks.size() > 0) {
            mAdapter.setData(mFunks);
            noDataView.setVisibility(View.GONE);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getDataFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadMoreDataSuccess(String msg, List<FunkListBean.Funk> funks) {
        mFunks.addAll(funks);
        mAdapter.setData(mFunks);
    }

    @Override
    public void loadMoreDataFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshDataSuccess(String msg, List<FunkListBean.Funk> funks) {

    }

    @Override
    public void refreshDataFailure(String msg) {

    }

    @Override
    public void onLoad() {
        funkLv.setRefreshTime();
        funkLv.stopRefresh();
        funkLv.stopLoadMore();
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public void setLoadStart(int start) {
        loadStart = start;
    }

    @Override
    public int getLoadStart() {
        return loadStart;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void setPresenter(FunkContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onRefresh() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getActivity(), df.format(new Date()));
        mPresenter.getFunks(start, limit, orderBy);
    }

    @Override
    public void onLoadMore() {
        isLoadMore = true;
        mPresenter.loadMoreData(loadStart, limit, orderBy);
    }


    private class ListViewAdapter extends BaseAdapter {
        private List<FunkListBean.Funk> funks;
        private Context mContext;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<FunkListBean.Funk> funks) {
            this.funks = funks;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return funks == null ? 0 : funks.size();
        }

        @Override
        public FunkListBean.Funk getItem(int position) {
            return funks == null ? null : funks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_funk_lv, null);
                viewHolder.item = (LinearLayout) convertView.findViewById(R.id.item);
                viewHolder.item_userAvatar = (CircleImageView) convertView.findViewById(R.id.item_userAvatar);
                viewHolder.item_userNickname = (TextView) convertView.findViewById(R.id.item_userNickname);
                viewHolder.item_funk_time = (TextView) convertView.findViewById(R.id.item_funk_time);
                viewHolder.item_funk_type = (TextView) convertView.findViewById(R.id.item_funk_type);
                viewHolder.item_funk_title = (TextView) convertView.findViewById(R.id.item_funk_title);
                viewHolder.item_funk_content = (TextView) convertView.findViewById(R.id.item_funk_content);
                viewHolder.item_funk_img = (ImageView) convertView.findViewById(R.id.item_funk_img);
                viewHolder.item_funk_price = (TextView) convertView.findViewById(R.id.item_funk_price);
                viewHolder.item_funk_address = (TextView) convertView.findViewById(R.id.item_funk_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final FunkListBean.Funk funk = funks.get(position);
            String avatar = funk.getPublisher().getAvatar();
            if (avatar != null && !avatar.equals("")) {
                Glide.with(mContext).load(avatar).error(R.mipmap.ic_test_avatart).into(viewHolder.item_userAvatar);
            }
            viewHolder.item_userNickname.setText(funk.getPublisher().getNickname());
            viewHolder.item_funk_type.setText(funk.getCategory());
            viewHolder.item_funk_time.setText(funk.getCreated_time());
            viewHolder.item_funk_title.setText(funk.getTitle());
            viewHolder.item_funk_content.setText(funk.getBody());
            viewHolder.item_funk_price.setText(funk.getPrice() + "元");
            final List<String> imgs = (List<String>) funk.getImgs();
            if (imgs != null && imgs.size() > 0) {
                viewHolder.item_funk_img.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .load(imgs.get(0))
                        .crossFade()
                        .error(R.mipmap.ic_test)
                        .thumbnail(0.1f)
                        .into(viewHolder.item_funk_img);
                viewHolder.item_funk_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                        intent.putExtra("position", 0);
                        intent.putStringArrayListExtra("pics", (ArrayList<String>) imgs);
                        startActivity(intent);
                    }
                });
            } else {
                viewHolder.item_funk_img.setVisibility(View.GONE);
            }
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), FunkDetailActiviity.class).putExtra("funkId", funk.getId()));

                }
            });
            return convertView;
        }

        private class ViewHolder {
            private LinearLayout item;
            private CircleImageView item_userAvatar;
            private TextView item_userNickname;
            private TextView item_funk_type;
            private TextView item_funk_time;
            private TextView item_funk_title;
            private TextView item_funk_content;
            private ImageView item_funk_img;
            private TextView item_funk_price;
            private TextView item_funk_address;

        }
    }
}
