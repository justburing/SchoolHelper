package com.burning.smile.schoolhelper.express;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.burning.smile.schoolhelper.addexpress.AddExpressActivity;
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.expressdetail.ExpressDetailActivity;
import com.burning.smile.schoolhelper.expresssearch.ExpressSearchActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.widget.SegmentedGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/13.
 */
public class ExpressFragment extends Fragment implements ExpressContract.View, OnRefreshListener {
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
    @BindView(R.id.expressLv)
    RefreshLoadListView expressLv;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.noDataImage)
    ImageView noDataImage;


    private ExpressContract.Presenter mPresenter;
    private ListViewAdapter mAdapter;
    private List<ExpressListBean.Express> mExpresses;
    private DrawerOpreator mDrawerOpreator;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int start = 0;
    private int limit = 10;
    private int loadStart = limit;
    private String status = "";
    private String is_urgent = "";

    public static ExpressFragment newInstance() {
        ExpressFragment fragment = new ExpressFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.express_frag, null);
        ButterKnife.bind(this, view);
        mAdapter = new ListViewAdapter(getActivity());
        expressLv.setAdapter(mAdapter);
        expressLv.setPullRefreshEnable(true);
        expressLv.setPullLoadEnable(true);
        expressLv.setOnRefreshListener(this);
        expressLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(
                        new Intent(getActivity(), ExpressDetailActivity.class)
                                .putExtra("expressId", mExpresses.get(position - 1).getId())
                );
            }
        });
        segmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.newsest:
                        is_urgent = "";
                        mPresenter.getExpresses(start, limit, is_urgent);
                        break;
                    case R.id.hostest:
                        is_urgent = "2";
                        mPresenter.getExpresses(start, limit, is_urgent);
                        break;
                }
            }
        });
        new ExpressPresenter(this).start();
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

    @Override
    public void showLoadingView() {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
    }

    @Override
    public void dimissLoadingView() {
        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
    }

    @Override
    public void getDataSuccess(String msg, List<ExpressListBean.Express> expresses) {
        mExpresses = expresses;
        if (mExpresses != null && mExpresses.size() > 0) {
            mAdapter.setData(mExpresses);
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
    public void loadMoreDataSuccess(String msg, List<ExpressListBean.Express> expresses) {
        mExpresses.addAll(expresses);
        mAdapter.setData(mExpresses);
    }

    @Override
    public void loadMoreDataFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshDataSuccess(String msg, List<ExpressListBean.Express> expresses) {

    }

    @Override
    public void refreshDataFailure(String msg) {

    }


    @Override
    public void onLoad() {
        expressLv.setRefreshTime(RefreshTime.getRefreshTime(getActivity()));
        expressLv.stopRefresh();
        expressLv.stopLoadMore();
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
    public String getIsUrgent() {
        return is_urgent;
    }

    @Override
    public void onRefresh() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getActivity(), df.format(new Date()));
        mPresenter.getExpresses(start, limit, is_urgent);
    }

    @Override
    public void onLoadMore() {
        isLoadMore = true;
        mPresenter.loadMoreData(loadStart, limit, is_urgent);
    }

    @Override
    public void setPresenter(ExpressContract.Presenter presenter) {
        this.mPresenter = presenter;
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
                startActivity(new Intent(getActivity(), AddExpressActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(getActivity(), ExpressSearchActivity.class));
                break;
            case R.id.refresh:
                mPresenter.getExpresses(start, limit, is_urgent);
                break;
        }
    }


    private class ListViewAdapter extends BaseAdapter {
        private List<ExpressListBean.Express> expresses;
        private Context mContext;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<ExpressListBean.Express> expresses) {
            this.expresses = expresses;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return expresses == null ? 0 : expresses.size();
        }

        @Override
        public ExpressListBean.Express getItem(int position) {
            return expresses == null ? null : expresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_express_lv, null);
                viewHolder.item_userAvatar = (CircleImageView) convertView.findViewById(R.id.item_userAvatar);
                viewHolder.item_userNickname = (TextView) convertView.findViewById(R.id.item_userNickname);
                viewHolder.item_express_coin = (TextView) convertView.findViewById(R.id.item_express_coin);
                viewHolder.item_express_time = (TextView) convertView.findViewById(R.id.item_express_time);
                viewHolder.item_express_title = (TextView) convertView.findViewById(R.id.item_express_title);
                viewHolder.item_express_isUrgent = (TextView) convertView.findViewById(R.id.item_express_isUgrent);
                viewHolder.item_express_content = (TextView) convertView.findViewById(R.id.item_express_content);
                viewHolder.item_express_type = (TextView) convertView.findViewById(R.id.item_express_type);
                viewHolder.item_express_address = (TextView) convertView.findViewById(R.id.item_express_address);
                viewHolder.item_expres_status = (ImageView) convertView.findViewById(R.id.item_status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ExpressListBean.Express express = expresses.get(position);
            String avatar = express.getPublisher().getAvatar();
            if (avatar != null && !avatar.equals("")) {
                Glide.with(mContext).load(avatar).into(viewHolder.item_userAvatar);
            }
            viewHolder.item_userNickname.setText(express.getPublisher().getNickname());
            viewHolder.item_express_coin.setText("悬赏:" + express.getOffer() + "流币");
            viewHolder.item_express_time.setText(express.getCreated_time());
            viewHolder.item_express_title.setText(express.getTitle());
            viewHolder.item_express_content.setText(express.getDetail());
            //1:小包裹，2：中包裹，3：大包裹
            viewHolder.item_express_type.setText(express.getType().equals("1") ? "小包裹" : (express.getType().equals("2") ? "中包裹" : "大包裹"));
            // 1：生活一区 2：生活二区 3：报刊亭
            viewHolder.item_express_address.setText(express.getAddress_id().equals("0") ? "生活二区" : (express.getAddress_id().equals("1") ? "生活二区" : "报刊亭"));
            //1：待认领 2：被接单 3：已送达 4：已确认
            if (express.getIs_urgent().equals("1")) {
                viewHolder.item_express_isUrgent.setVisibility(View.GONE);
            } else {
                viewHolder.item_express_isUrgent.setVisibility(View.VISIBLE);
            }
            if (express.getStatus().equals("2")) {
                viewHolder.item_expres_status.setVisibility(View.VISIBLE);
                viewHolder.item_expres_status.setImageResource(R.mipmap.ic_status_2);
            } else if (express.getStatus().equals("3")) {
                viewHolder.item_expres_status.setVisibility(View.VISIBLE);
                viewHolder.item_expres_status.setImageResource(R.mipmap.ic_status_3);
            } else if (express.getStatus().equals("4")) {
                viewHolder.item_expres_status.setVisibility(View.VISIBLE);
                viewHolder.item_expres_status.setImageResource(R.mipmap.ic_status_4);
            } else {
                viewHolder.item_expres_status.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            private CircleImageView item_userAvatar;
            private TextView item_userNickname;
            private TextView item_express_coin;
            private TextView item_express_time;
            private TextView item_express_title;
            private TextView item_express_isUrgent;
            private TextView item_express_content;
            private TextView item_express_type;
            private TextView item_express_address;
            private ImageView item_expres_status;

        }
    }

}
