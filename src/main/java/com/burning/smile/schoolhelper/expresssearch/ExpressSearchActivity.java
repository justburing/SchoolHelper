package com.burning.smile.schoolhelper.expresssearch;

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
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.expressdetail.ExpressDetailActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class ExpressSearchActivity extends BaseActivity {
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


    private String[] lvOne = new String[]{"关键词", "时间", "包裹类型", "快递状态", "是否加急"};
    private String[] lvTwoKeyWord = new String[]{"标题", "发布人昵称"};
    private String[] lvTwoTime = new String[]{"全部", "本周", "本月"};
    private String[] lvTwoExType = new String[]{"全部", "小包裹", "中包裹", "大包裹"};
    private String[] lvTwosExStatus = new String[]{"全部", "待认领", "被认领", "已送达", "已确认"};
    private String[] lvTwoIsUrgent = new String[]{"全部", "加急", "不加急"};
    private List<List<String>> searchData;
    private SearchPanelAdapter lvOneAdapter;
    private SearchPanelAdapter lvTwoAdapter;
    private List<Map<String, Object>> lvOneData;
    private List<Map<String, Object>> lvTwoKeyWordData;
    private List<Map<String, Object>> lvTwoTimeData;
    private List<Map<String, Object>> lvTwoExTypeData;
    private List<Map<String, Object>> lvTwosExStatusData;
    private List<Map<String, Object>> lvTwosExUgrentsData;
    private int positonOne;
    private String keyWordText = "title";
    private String timeText;
    private String startDateText = "";
    private String endDateText = "";
    private String expressTypeText = "";
    private String expresStatusText = "";
    private String expressUgrentText = "";
    private ListViewAdapter mAdapter;
    private List<ExpressListBean.Express> mExpresses;
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
                        new Intent(ExpressSearchActivity.this, ExpressDetailActivity.class)
                                .putExtra("expressId", mExpresses.get(position).getId())
                );
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.express_search_act;
    }

    public void initSearchData() {
        searchData = new ArrayList<>();
        lvOneData = new ArrayList<>();
        lvTwoKeyWordData = new ArrayList<>();
        lvTwoTimeData = new ArrayList<>();
        lvTwoExTypeData = new ArrayList<>();
        lvTwosExStatusData = new ArrayList<>();
        lvTwosExUgrentsData = new ArrayList<>();
        Observable.from(lvOne)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
//                        if (s.equals("关键词")) {
//                            map.put("itemStatus", "checked");
//                        } else {
                        map.put("itemStatus", false);
                        //}
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
        Observable.from(lvTwoKeyWord)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Map<String, Object>>>() {
                    @Override
                    public Observable<Map<String, Object>> call(String s) {
                        Map<String, Object> map = new HashMap();
                        map.put("itemName", s);
                        if (s.equals("标题")) {
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
                        lvTwoKeyWordData.add(map);
                    }
                });
        Observable.from(lvTwoTime)
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
                        lvTwoTimeData.add(map);
                    }
                });
        Observable.from(lvTwoExType)
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
                        lvTwoExTypeData.add(map);
                    }
                });
        Observable.from(lvTwosExStatus)
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
                        lvTwosExStatusData.add(map);
                    }
                });
        Observable.from(lvTwoIsUrgent)
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
                        lvTwosExUgrentsData.add(map);
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
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            lvTwoAdapter.setData(lvTwoKeyWordData);
                            showSpinnerRight();
                        } else {
                            keyWordText = "";
                            for (Map map : lvTwoKeyWordData) {
                                if (map.get("itemName").toString().equals(lvTwoKeyWordData.get(0).get("itemName").toString())) {
                                    map.put("itemStatus", true);
                                } else {
                                    map.put("itemStatus", false);
                                }
                            }
                            hideSpinnerRight();
                        }
                        break;
                    case 1:
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            lvTwoAdapter.setData(lvTwoTimeData);
                            showSpinnerRight();
                        } else {
                            timeText = "";
                            for (Map map : lvTwoTimeData) {
                                if (map.get("itemName").toString().equals(lvTwoTimeData.get(0).get("itemName").toString())) {
                                    map.put("itemStatus", true);
                                } else {
                                    map.put("itemStatus", false);
                                }
                            }
                            hideSpinnerRight();
                        }
                        break;
                    case 2:
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            lvTwoAdapter.setData(lvTwoExTypeData);
                            showSpinnerRight();
                        } else {
                            expressTypeText = "";
                            for (Map map : lvTwoExTypeData) {
                                if (map.get("itemName").toString().equals(lvTwoExTypeData.get(0).get("itemName").toString())) {
                                    map.put("itemStatus", true);
                                } else {
                                    map.put("itemStatus", false);
                                }
                            }
                            hideSpinnerRight();
                        }

                        break;
                    case 3:
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            lvTwoAdapter.setData(lvTwosExStatusData);
                            showSpinnerRight();
                        } else {
                            expresStatusText = "";
                            for (Map map : lvTwosExStatusData) {
                                if (map.get("itemName").toString().equals(lvTwosExStatusData.get(0).get("itemName").toString())) {
                                    map.put("itemStatus", true);
                                } else {
                                    map.put("itemStatus", false);
                                }
                            }
                            hideSpinnerRight();
                        }
                        break;
                    case 4:
                        if ((boolean) lvOneData.get(position).get("itemStatus")) {
                            lvTwoAdapter.setData(lvTwosExUgrentsData);
                            showSpinnerRight();
                        } else {
                            expressUgrentText = "";
                            for (Map map : lvTwosExUgrentsData) {
                                if (map.get("itemName").toString().equals(lvTwosExUgrentsData.get(0).get("itemName").toString())) {
                                    map.put("itemStatus", true);
                                } else {
                                    map.put("itemStatus", false);
                                }
                            }
                            hideSpinnerRight();
                        }
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
                                keyWordText = "title";
                                break;
                            case 1:
                                keyWordText = "nickname";
                                break;
                        }
                        // keyWordText = lvTwoAdapter.getData().get(position).get("itemName").toString();
                        break;
                    case 1:
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        int day;
                        int day1;
                        int day2;
                        Date date1;
                        Date date2;
                        String s;
                        String s1;
                        switch (position) {
                            case 0:
                                startDateText = "";
                                endDateText = "";
                                break;
                            case 1:
                                day = c.get(Calendar.DAY_OF_WEEK);
                                day1 = c.get(Calendar.DAY_OF_YEAR) - day + 1;
                                c.set(Calendar.DAY_OF_YEAR, day1);
                                date1 = c.getTime();
                                day2 = c.get(Calendar.DAY_OF_YEAR) + 6;
                                c.set(Calendar.DAY_OF_YEAR, day2);
                                date2 = c.getTime();
                                s = sdf.format(date1);
                                s1 = sdf.format(date2);
                                Log.e("log", "date=" + s + "::date1=" + s1);
                                startDateText = s;
                                endDateText = s1;
                                break;
                            case 2:
                                day = c.get(Calendar.DAY_OF_MONTH);
                                day1 = c.get(Calendar.DAY_OF_YEAR) - day + 1;
                                c.set(Calendar.DAY_OF_YEAR, day1);
                                date1 = c.getTime();
                                day2 = c.get(Calendar.DAY_OF_YEAR) + 29;
                                c.set(Calendar.DAY_OF_YEAR, day2);
                                date2 = c.getTime();
                                s = sdf.format(date1);
                                s1 = sdf.format(date2);
                                Log.e("log", "date=" + s + "::date1=" + s1);
                                startDateText = s;
                                endDateText = s1;
                                break;
                        }
                        // timeText = lvTwoAdapter.getData().get(position).get("itemName").toString();
                        break;
                    case 2:
                        switch (position) {
                            case 0:
                                expressTypeText = "";
                                break;
                            case 1:
                                expressTypeText = "1";
                                break;
                            case 2:
                                expressTypeText = "2";
                                break;
                            case 3:
                                expressTypeText = "3";
                                break;
                        }
                        //expressTypeText = lvTwoAdapter.getData().get(position).get("itemName").toString();
                        break;
                    case 3:
                        switch (position) {
                            case 0:
                                expresStatusText = "";
                                break;
                            case 1:
                                expresStatusText = "1";
                                break;
                            case 2:
                                expresStatusText = "2";
                                break;
                            case 3:
                                expresStatusText = "3";
                                break;
                            case 4:
                                expresStatusText = "4";
                                break;
                        }
                        // expresStatusText = lvTwoAdapter.getData().get(position).get("itemName").toString();
                        break;
                    case 4:
                        switch (position) {
                            case 0:
                                expressUgrentText = "";
                                break;
                            case 1:
                                expressUgrentText = "2";
                                break;
                            case 2:
                                expressUgrentText = "1";
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
         startDate false string 根据创建时间的起始时间检索
         endDate false string 根据创建时间的结束时间检索
         type false string 根据包裹类型检索( 1: 小包裹 2: 中包裹 3: 大包裹)
         status false string 1 根据快递状态检索( 1: 待认领 2: 被认领 3: 已送达 4: 已确认)
         keywordType false string 关键词检索( nickname: 发布人昵称 title: 标题)
         keyword false string 关键词（keywordType是nickname的话，keyword就是要检索的用户昵称）
         */
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getExpress(bean.getToken(), String.valueOf(start), String.valueOf(limit), startDateText, endDateText, expressTypeText, expresStatusText, keyWordText, keyWord, expressUgrentText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean>() {
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
                    public void onNext(ExpressListBean expressListBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (!isLoadMore) {
                            mExpresses = expressListBean.getResources();
                            if (mExpresses != null && mExpresses.size() > 0) {
                                mAdapter.setData(mExpresses);
                                noDataView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }
                        } else {
                            isLoadMore = false;
                            if (expressListBean.getResources() != null && expressListBean.getResources().size() > 0) {
                                mExpresses.addAll(expressListBean.getResources());
                                mAdapter.setData(mExpresses);
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
            // 0：生活一区 1：生活二区 2：报刊亭
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
