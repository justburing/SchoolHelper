package com.burning.smile.schoolhelper.forumsearch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.pulltorefresh.PullToRefreshListFooter;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.ForumListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.forumdetail.ForumDetailActivity;
import com.burning.smile.schoolhelper.photoshower.PhotoViewActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.emoij.SimpleCommonUtils;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * Created by smile on 2017/4/7.
 */
public class ForumSearchActivity extends BaseActivity {
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.searchLL)
    LinearLayout searchLL;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ivDropDown)
    ImageView ivDropDown;
    @BindView(R.id.toolbarLL)
    LinearLayout toolbarLL;
    @BindView(R.id.leftListView)
    ListView leftListView;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.rightListView)
    ListView rightListView;
    @BindView(R.id.popMenu)
    LinearLayout popMenu;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.refresh)
    Button refresh;


    private String[] lvOne = new String[]{"排序方式", "仅自己所在组"};
    private String[] lvTwoOrderBy = new String[]{"更新时间", "发布时间", "评论量", "浏览量"};
    private SearchPanelAdapter lvOneAdapter;
    private SearchPanelAdapter lvTwoAdapter;
    private List<Map<String, Object>> lvOneData;
    private List<Map<String, Object>> lvTwoOrderByData;
    private int positonOne;
    private String orderByText = "updated_time desc";
    private String gruopId = "";
    private ListViewAdapter mAdapter;
    private List<ForumListBean.Forum> mForums;
    private UserInfoBean bean;
    private int start = 0;
    private int limit = 20;
    private String keyWord = "";
    private boolean isLoadMore = false;


    @Override
    protected void init() {
        ButterKnife.bind(this);
        bean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        mAdapter = new ListViewAdapter(this);
        listView.setAdapter(mAdapter);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyWord = searchEditText.getText().toString();
                    getData();
                    return true;
                }
                return false;
            }
        });
        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSpinnerLeft();
                hideSpinnerRight();
                return false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvOneAdapter = new SearchPanelAdapter(this);
        leftListView.setAdapter(lvOneAdapter);
        lvTwoAdapter = new SearchPanelAdapter(this);
        rightListView.setAdapter(lvTwoAdapter);
        initSearchData();
        ivDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftListView.getVisibility() == View.INVISIBLE) {
                    showSpinnerLeft();
                } else {
                    hideSpinnerLeft();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = 0;
                isLoadMore = false;
                getData();
            }
        });
        PullToRefreshListFooter footer = new PullToRefreshListFooter(this);
        listView.addFooterView(footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = limit;
                isLoadMore = true;
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(
                        new Intent(ForumSearchActivity.this, ForumDetailActivity.class)
                                .putExtra("forumId", mForums.get(position).getId())
                );
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.forum_search_act;
    }

    public void initSearchData() {
        lvOneData = new ArrayList<>();
        lvTwoOrderByData = new ArrayList<>();
        Observable.from(lvOne)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
                        map.put("itemStatus", false);
                        return Observable.just(map);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {
                        lvOneAdapter.setData(lvOneData);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, Object> map) {
                        lvOneData.add(map);
                    }
                });
        Observable.from(lvTwoOrderBy)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
                        if (s.equals("更新时间")) {
                            map.put("itemStatus", true);
                        } else {
                            map.put("itemStatus", false);
                        }
                        return Observable.just(map);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, Object> map) {
                        lvTwoOrderByData.add(map);
                    }
                });
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positonOne = position;
                lvOneData.get(position).put("itemStatus", true);
                lvOneAdapter.notifyDataSetChanged();
                switch (position) {
                    case 0:
                        lvTwoAdapter.setData(lvTwoOrderByData);
                        showSpinnerRight();
                        break;
                    case 1:
                        lvOneData.get(position).put("itemStatus", !((boolean) lvOneData.get(position).get("itemStatus")));
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            gruopId = "";
                        } else {
                            gruopId = "";
                        }
                        start = 0;
                        getData();
                        hideSpinnerLeft();
                        break;
                }
            }
        });
        rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Map map : lvTwoAdapter.getData()) {
                    if (map.get("itemName".toString()).equals(lvTwoAdapter.getData().get(position).get("itemName").toString())) {
                        map.put("itemStatus", true);
                    } else {
                        map.put("itemStatus", false);
                    }
                }
                lvTwoAdapter.notifyDataSetChanged();
                switch (positonOne) {
                    case 0:
                        switch (position) {
                            case 0:
                                orderByText = "updated_time desc";
                                break;
                            case 1:
                                orderByText = "created_time desc";
                                break;
                            case 2:
                                orderByText = "post_num desc";
                                break;
                            case 3:
                                orderByText = "hit_num desc";
                                break;
                        }
                        break;
                }
                start = 0;
                getData();
                hideSpinnerLeft();
            }
        });
    }


    public void showSpinnerLeft() {
        leftListView.setVisibility(View.VISIBLE);
        popMenu.bringToFront();
        //hideSpinnerRight();
    }

    public void hideSpinnerLeft() {
        leftListView.setVisibility(View.INVISIBLE);
        hideSpinnerRight();

    }

    public void showSpinnerRight() {
        rightListView.setVisibility(View.VISIBLE);
        popMenu.bringToFront();
        // hideSpinnerLeft();
    }

    public void hideSpinnerRight() {
        rightListView.setVisibility(View.INVISIBLE);
    }


    public void getData() {
        /**
         orderby false string updated_time desc 排序条件(post_num: 回复数，hit_num :浏览数，created_time, updated_time)
         title false string 根据标题模糊查询
         group_id false string 查询某个小组下的话题
         */
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getForums(bean.getToken(), String.valueOf(start), String.valueOf(limit), orderByText, keyWord, gruopId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ForumListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());

                        if (e instanceof MalformedJsonException) {
                            toast("获取数据出错");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ForumListBean forumListBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (!isLoadMore) {
                            mForums = forumListBean.getResources();
                            mAdapter.notifyDataSetChanged();
                            if (mForums != null && mForums.size() > 0) {
                                noDataView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }
                        } else {
                            isLoadMore = false;
                            if (forumListBean.getResources() != null && forumListBean.getResources().size() > 0) {
                                mForums.addAll(forumListBean.getResources());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                start = 0;
                                toast("没有更多数据了");
                            }
                        }
                    }
                });
    }


    class SearchPanelAdapter extends BaseAdapter {
        private Context mContext;
        private List<Map<String, Object>> mData;

        public SearchPanelAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<Map<String, Object>> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        public List<Map<String, Object>> getData() {
            return mData;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_panel, null);
                holder.item = (TextView) convertView.findViewById(R.id.item);
                holder.itemImg = (ImageView) convertView.findViewById(R.id.item_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> map = mData.get(position);
            if (map != null) {
                holder.item.setText(map.get("itemName").toString());
                if ((boolean) map.get("itemStatus")) {
                    holder.itemImg.setVisibility(View.VISIBLE);
                } else {
                    holder.itemImg.setVisibility(View.INVISIBLE);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView item;
            private ImageView itemImg;
        }
    }


    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mForums == null ? 0 : mForums.size();
        }

        @Override
        public ForumListBean.Forum getItem(int position) {
            return mForums == null ? null : mForums.get(position);
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
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.item_forum_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ForumListBean.Forum forum = mForums.get(position);
            Glide.with(mContext).load(forum.getUser().getAvatar()).into(viewHolder.item_userAvatar);
            viewHolder.item_userNickname.setText(forum.getUser().getNickname());
            viewHolder.item_userLevel.setText("");
            viewHolder.item_forum_view.setText(forum.getHit_num());
            viewHolder.item_forum_message.setText(forum.getPost_num());
            viewHolder.item_forum_title.setText(forum.getTitle());
            viewHolder.item_forum_time.setText(forum.getUpdated_time());
            final List<String> imgs = forum.getImgs();
            PictrueAdapter adapter = new PictrueAdapter(ForumSearchActivity.this);
            viewHolder.item_gridView.setAdapter(adapter);
            adapter.setData(imgs);
            viewHolder.item_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ForumSearchActivity.this, PhotoViewActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("pics", (ArrayList<String>) imgs);
                    startActivity(intent);
                }
            });

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
        public View getView(int position, View convertView, ViewGroup parent) {
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
//            if (map.get("url").toString().contains("http")) {
//                Glide.with(mContext).load(map.get("url").toString()).into(viewHolder.imageView);
//            } else if (map.get("url").toString().contains("Drawable")) {
//                Glide.with(mContext).load(R.mipmap.ic_take_photo).into(viewHolder.imageView);
//            } else {
//                Glide.with(mContext).load(new File(map.get("url").toString())).error(R.mipmap.ic_launcher).crossFade().into(viewHolder.imageView);
//            }
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
            return convertView;
        }

        class ViewHolder {
            private ImageView imageView;
        }
    }
}

