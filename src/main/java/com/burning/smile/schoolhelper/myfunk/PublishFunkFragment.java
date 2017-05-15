package com.burning.smile.schoolhelper.myfunk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.pulltorefresh.PullToRefreshListFooter;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.FunkListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/3/14.
 */
public class PublishFunkFragment extends Fragment {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.refresh)
    Button refresh;

    private int start = 0;
    private int limit = 10;
    private ListViewAdapter mAdapter;
    private UserInfoBean userInfoBean;
    private List<FunkListBean.Funk> funks;
    private boolean isLoadMore = false;


    public static PublishFunkFragment newInstance() {
        PublishFunkFragment fragment = new PublishFunkFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.funk_mine_publish_frag, null);
        ButterKnife.bind(this, view);
        mAdapter = new ListViewAdapter(getActivity());
        userInfoBean = AndroidFileUtil.getObject(getActivity(), AppConfig.USER_FILE);
        listView.setAdapter(mAdapter);
        PullToRefreshListFooter footer = new PullToRefreshListFooter(getActivity());
        listView.addFooterView(footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = limit;
                start = limit;
                isLoadMore = true;
                getData();
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
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(getActivity(), MyExpressDetailActivity.class).putExtra("id", funks.get(position).getId()));
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
      //  AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(),new LoadingFragment(),false);
        RetrofitUtil.getRetrofitApiInstance().getFunk(userInfoBean.getToken(), String.valueOf(start), String.valueOf(limit), "","","","",userInfoBean.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                      //  AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "获取我出售的旧货细信息数据出错", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                Toast.makeText(getActivity(), object.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            Toast.makeText(getActivity(), "当前无网络,请检查网络状况后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(FunkListBean funkListBean) {
                   //    AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (!isLoadMore) {
                            funks = funkListBean.getResources();
                            if (funks != null && funks.size() > 0) {
                                mAdapter.notifyDataSetChanged();
                                noDataView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }
                        } else {
                            isLoadMore = false;
                            if (funkListBean.getResources() != null && funkListBean.getResources().size() > 0) {
                                funks.addAll(funkListBean.getResources());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                start = 0;
                                Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        //private List<FunkListBean.Funk> funks;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

//        public void setData(List<FunkListBean.Funk> funks) {
//            this.funks = funks;
//            notifyDataSetChanged();
//        }

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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_publish_funk_lv, null);
                viewHolder.funkType = (TextView) convertView.findViewById(R.id.item_funk_type);
                viewHolder.funkUpsNum = (TextView) convertView.findViewById(R.id.item_funk_likedNum);
                viewHolder.funkTitle = (TextView) convertView.findViewById(R.id.item_funk_title);
                viewHolder.funkContent = (TextView) convertView.findViewById(R.id.item_funk_content);
                viewHolder.funkTime = (TextView) convertView.findViewById(R.id.item_funk_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FunkListBean.Funk funk = funks.get(position);
            viewHolder.funkType.setText(funk.getCategory());
            viewHolder.funkUpsNum.setText(funk.getUps_num());
            viewHolder.funkTitle.setText(funk.getTitle());
            viewHolder.funkContent.setText(funk.getBody());
            viewHolder.funkTime.setText(funk.getUpdated_time());
            return convertView;
        }

        private class ViewHolder {
            private TextView funkType;
            private TextView funkUpsNum;
            private TextView funkTitle;
            private TextView funkContent;
            private TextView funkTime;
        }
    }
}
