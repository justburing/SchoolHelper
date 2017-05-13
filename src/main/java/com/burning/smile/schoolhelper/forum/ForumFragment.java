package com.burning.smile.schoolhelper.forum;

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
import android.widget.GridView;
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
import com.burning.smile.schoolhelper.addforum.AddForumActivity;
import com.burning.smile.schoolhelper.data.ForumListBean;
import com.burning.smile.schoolhelper.forumdetail.ForumDetailActivity;
import com.burning.smile.schoolhelper.forumsearch.ForumSearchActivity;
import com.burning.smile.schoolhelper.photoshower.PhotoViewActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.emoij.SimpleCommonUtils;
import com.burning.smile.schoolhelper.widget.SegmentedGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * Created by smile on 2017/3/14.
 */
public class ForumFragment extends Fragment implements ForumContract.View, OnRefreshListener {


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
    @BindView(R.id.forumLv)
    RefreshLoadListView forumLv;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.noDataImage)
    ImageView noDataImage;

    private ForumContract.Presenter mPresenter;
    private ListViewAdapter mAdapter;
    private DrawerOpreator mDrawerOpreator;
    private PictrueAdapter mPictrueAdapter;
    private List<ForumListBean.Forum> mForums;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int start = 0;
    private int limit = 10;
    private int loadStart = limit;
    private String orderBy = "updated_time desc";

    public static ForumFragment newInstance() {
        ForumFragment fragment = new ForumFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum_frag, null);
        ButterKnife.bind(this, view);
        mAdapter = new ListViewAdapter(getActivity());
        forumLv.setAdapter(mAdapter);
        forumLv.setPullRefreshEnable(true);
        forumLv.setPullLoadEnable(true);
        forumLv.setOnRefreshListener(this);
        forumLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(
                        new Intent(getActivity(), ForumDetailActivity.class)
                                .putExtra("forumId", mForums.get(position - 1).getId())
                );
            }
        });
        segmentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.newsest:
                        orderBy = "updated_time desc";
                        mPresenter.getForums(start, limit, orderBy);
                        break;
                    case R.id.hostest:
                        orderBy = "post_num desc";
                        mPresenter.getForums(start, limit, orderBy);
                        break;
                }
            }
        });
//        noDataImage.setImageResource(R.drawable.anim_nodata_view);
//        AnimationDrawable animationDrawable = (AnimationDrawable) noDataImage.getDrawable();
//        animationDrawable.start();
        new ForumPresenter(this).start();
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
                startActivity(new Intent(getActivity(), AddForumActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(getActivity(), ForumSearchActivity.class));
                break;
            case R.id.refresh:
                mPresenter.getForums(start, limit, orderBy);
                break;
        }
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
    public void onRefresh() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getActivity(), df.format(new Date()));
        mPresenter.getForums(start, limit, orderBy);
    }

    @Override
    public void onLoadMore() {
        isLoadMore = true;
        mPresenter.loadMoreData(loadStart, limit, orderBy);
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
    public void getDataSuccess(String msg, List<ForumListBean.Forum> forums) {
        mForums = forums;
        if (forums != null && forums.size() > 0) {
            mAdapter.setData(mForums);
            noDataView.setVisibility(View.GONE);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getDataFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        forumLv.setVisibility(View.GONE);
    }

    @Override
    public void loadMoreDataSuccess(String msg, List<ForumListBean.Forum> forums) {
        mForums.addAll(forums);
        mAdapter.setData(mForums);
    }

    @Override
    public void loadMoreDataFailure(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshDataSuccess(String msg, List<ForumListBean.Forum> forums) {

    }

    @Override
    public void refreshDataFailure(String msg) {

    }

    @Override
    public void onLoad() {
        forumLv.setRefreshTime(RefreshTime.getRefreshTime(getActivity()));
        forumLv.stopRefresh();
        forumLv.stopLoadMore();
    }

    @Override
    public void setPresenter(ForumContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    private class ListViewAdapter extends BaseAdapter {
        private List<ForumListBean.Forum> forums;
        private Context mContext;
        private List<String> imgUrls;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<ForumListBean.Forum> forums) {
            this.forums = forums;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return forums == null ? 0 : forums.size();
        }

        @Override
        public ForumListBean.Forum getItem(int position) {
            return forums == null ? null : forums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_lv, null);
                viewHolder.item = (LinearLayout) convertView.findViewById(R.id.item);
                viewHolder.item_userAvatar = (CircleImageView) convertView.findViewById(R.id.item_userAvatar);
                viewHolder.item_userNickname = (TextView) convertView.findViewById(R.id.item_userNickname);
                viewHolder.item_userLevel = (TextView) convertView.findViewById(R.id.item_userLevel);
                viewHolder.item_forum_view = (TextView) convertView.findViewById(R.id.item_forum_view);
                viewHolder.item_forum_message = (TextView) convertView.findViewById(R.id.item_forum_message);
                viewHolder.item_forum_title = (EmoticonsEditText) convertView.findViewById(R.id.item_forum_title);
                viewHolder.item_gridView = (GridView) convertView.findViewById(R.id.item_gridView);
                viewHolder.item_forum_time = (TextView) convertView.findViewById(R.id.item_forum_time);
                viewHolder.item_forum_content = (EmoticonsEditText) convertView.findViewById(R.id.item_forum_content);
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.item_forum_title);
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.item_forum_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ForumListBean.Forum forum = forums.get(position);
            Glide.with(mContext).load(forum.getUser().getAvatar()).into(viewHolder.item_userAvatar);
            viewHolder.item_userNickname.setText(forum.getUser().getNickname());
            viewHolder.item_userLevel.setText("");
            viewHolder.item_forum_view.setText(forum.getHit_num());
            viewHolder.item_forum_message.setText(forum.getPost_num());
            viewHolder.item_forum_title.setText(forum.getTitle());
            viewHolder.item_forum_time.setText(forum.getUpdated_time());
            final List<String> imgs = forum.getImgs();
            imgUrls = new ArrayList<>();
            if (imgs == null) {
                viewHolder.item_forum_content.setVisibility(View.VISIBLE);
                viewHolder.item_forum_content.setText(forum.getContent());
            } else {
                if (imgs.size() == 0) {
                    viewHolder.item_forum_content.setVisibility(View.VISIBLE);
                    viewHolder.item_forum_content.setText(forum.getContent());
                } else {
                    for (String s : imgs) {
                        if (s.endsWith(".jpg") || s.endsWith(".png") || s.endsWith(".gif")) {
                            imgUrls.add(s);
                        }
                    }
                    if (forum.getContent().length() < 20) {
                        viewHolder.item_forum_content.setText(forum.getContent());
                        viewHolder.item_forum_content.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.item_forum_content.setVisibility(View.GONE);
                    }
                }
            }
            PictrueAdapter adapter = new PictrueAdapter(getActivity());
            viewHolder.item_gridView.setAdapter(adapter);
            adapter.setData(imgUrls);
            return convertView;
        }

        private class ViewHolder {
            private LinearLayout item;
            private CircleImageView item_userAvatar;
            private TextView item_userNickname;
            private TextView item_userLevel;
            private TextView item_forum_view;
            private TextView item_forum_message;
            private EmoticonsEditText item_forum_title;
            private GridView item_gridView;
            private TextView item_forum_time;
            private EmoticonsEditText item_forum_content;
        }
    }


    class PictrueAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> data;

        public PictrueAdapter(Context context) {
            this.mContext = context;
            data = new ArrayList<>();
        }

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public String getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_lv_gv, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_forum_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String url = data.get(position);
            if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif")) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
                if (url.contains("http")) {
                    Glide.with(mContext).load(url).into(viewHolder.imageView);
                } else {
                    Glide.with(mContext).load(new File(url)).error(R.mipmap.ic_launcher).crossFade().into(viewHolder.imageView);
                }
            } else {
                viewHolder.imageView.setVisibility(View.GONE);
            }
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("pics", (ArrayList<String>) data);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private ImageView imageView;
        }
    }
}
