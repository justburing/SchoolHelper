package com.burning.smile.schoolhelper.funksearch;

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
import com.burning.smile.schoolhelper.data.FunkListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.funkdetail.FunkDetailActiviity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

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

/**
 * Created by smile on 2017/4/7.
 */
public class FunkSearchActivity extends BaseActivity {
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


    private String[] lvOne = new String[]{"排序方式", "货物类型", "交易状态"};
    private String[] lvTwoOrderBy = new String[]{"更新时间", "发布时间", "评论量", "浏览量", "价格"};
    private String[] lvTwoFunkType = new String[]{"全部", "玩偶", "图书"};
    private String[] lvTwoFunkStatus = new String[]{"全部", "在售", "已售"};
    private SearchPanelAdapter lvOneAdapter;
    private SearchPanelAdapter lvTwoAdapter;
    private List<Map<String, Object>> lvOneData;
    private List<Map<String, Object>> lvTwoOrderByData;
    private List<Map<String, Object>> lvTwoFunkTypeData;
    private List<Map<String, Object>> lvTwosFunkStatusData;
    private int positonOne;
    private String orderByText = "updated_time desc";
    private String funkTypeText = "";
    private String funkStatusText = "";
    private ListViewAdapter mAdapter;
    private List<FunkListBean.Funk> mFunks;
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
                        new Intent(FunkSearchActivity.this, FunkDetailActiviity.class)
                                .putExtra("funkId", mFunks.get(position).getId())
                );
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.funk_search_act;
    }

    public void initSearchData() {
        lvOneData = new ArrayList<>();
        lvTwoOrderByData = new ArrayList<>();
        lvTwoFunkTypeData = new ArrayList<>();
        lvTwosFunkStatusData = new ArrayList<>();
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
        Observable.from(lvTwoFunkType)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
                        if (s.equals("全部")) {
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
                        lvTwoFunkTypeData.add(map);
                    }
                });
        Observable.from(lvTwoFunkStatus)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
                        if (s.equals("全部")) {
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
                        lvTwosFunkStatusData.add(map);
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
                        lvTwoAdapter.setData(lvTwoFunkTypeData);
                        showSpinnerRight();
                        break;
                    case 2:
                        lvTwoAdapter.setData(lvTwosFunkStatusData);
                        showSpinnerRight();
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
                                orderByText = "hits desc";
                                break;
                            case 4:
                                orderByText = "price desc";
                                break;
                        }
                        // keyWordText = lvTwoAdapter.getData().get(position).get("itemName").toString();
                        break;
                    case 1:
                        switch (position) {
                            case 0:
                                funkTypeText = "";
                                break;
                            case 1:
                                funkTypeText = "1";
                                break;
                            case 2:
                                funkTypeText = "2";
                                break;

                        }
                        break;
                    case 2:
                        switch (position) {
                            case 0:
                                funkStatusText = "";
                                break;
                            case 1:
                                funkStatusText = "2";
                                break;
                            case 2:
                                funkStatusText = "3";
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
         orderby false string updated_time desc 排序（目前支持created_time, updated_time, post_num, hits, price）排序
         title false string 根据标题检索
         category_id false string 根据分类检索
         status false string 1 (0:未发布， 1:已发布，2: 已关闭)
         */
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getFunk(bean.getToken(), String.valueOf(start), String.valueOf(limit), orderByText, keyWord, funkTypeText, funkStatusText,"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkListBean>() {
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
                    public void onNext(FunkListBean funkListBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (!isLoadMore) {
                            mFunks = funkListBean.getResources();
                            mAdapter.notifyDataSetChanged();
                            if (mFunks != null && mFunks.size() > 0) {
                                noDataView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }
                        } else {
                            isLoadMore = false;
                            if (funkListBean.getResources() != null && funkListBean.getResources().size() > 0) {
                                mFunks.addAll(funkListBean.getResources());
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
            return mFunks == null ? 0 : mFunks.size();
        }

        @Override
        public FunkListBean.Funk getItem(int position) {
            return mFunks == null ? null : mFunks.get(position);
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
            final FunkListBean.Funk funk = mFunks.get(position);
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
                Glide.with(FunkSearchActivity.this)
                        .load(imgs.get(0))
                        .crossFade()
                        .error(R.mipmap.ic_test)
                        .thumbnail(0.1f)
                        .into(viewHolder.item_funk_img);
            } else {
                viewHolder.item_funk_img.setVisibility(View.GONE);
            }
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

