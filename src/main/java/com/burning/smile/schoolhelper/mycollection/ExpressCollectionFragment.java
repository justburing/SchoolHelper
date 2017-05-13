package com.burning.smile.schoolhelper.mycollection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/13.
 */
public class ExpressCollectionFragment extends Fragment {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.refresh)
    Button refresh;


    private ListViewAdapter mAdapter;
    private UserInfoBean userInfoBean;
    private List<ExpressListBean.Express> express;
    private Map map;

    public static ExpressCollectionFragment newInstance() {
        ExpressCollectionFragment fragment = new ExpressCollectionFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collection_express_frag, null);
        ButterKnife.bind(this, view);
        userInfoBean = AndroidFileUtil.getObject(getActivity(), AppConfig.USER_FILE);
        map = AndroidFileUtil.getObject(getActivity(), AppConfig.COLLECTION_EXPRESS);
        mAdapter = new ListViewAdapter(getActivity());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(express.get(position).getId(), position);
            }
        });
        getData();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        return view;
    }

    public void getData() {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getCollections(userInfoBean.getToken(), "express")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "获取快递收藏信息出错", Toast.LENGTH_SHORT).show();
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
                    public void onNext(JSONObject object) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (object != null) {
                            ExpressListBean bean = JSON.parseObject(object.toJSONString(), ExpressListBean.class);
                            if (bean != null) {
                                if (bean.getResources() != null && bean.getResources().size() != 0) {
                                    express = bean.getResources();
                                    mAdapter.setData(express);
                                    noDataView.setVisibility(View.GONE);
                                } else {
                                    noDataView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "获取快递收藏信息出错", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    public void cancelCollect(final String id, final int position) {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().cancelCollect(userInfoBean.getToken(), "express", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
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
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (jsonObject.getString("success").equals("true")) {
                            express.remove(position);
                            mAdapter.setData(express);
                            if (map != null)
                                map.remove(AppConfig.COLLECTION_EXPRESS + id);
                            Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void showDialog(final String expressId, final int position) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_collection_dialog, null);
        TextView viewIt = (TextView) dialogView.findViewById(R.id.viewIt);
        TextView cancelIt = (TextView) dialogView.findViewById(R.id.cancelIt);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_transparent);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        cancelIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCollect(expressId, position);
                dialog.dismiss();
            }
        });
        viewIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), ExpressDetailActivity.class).putExtra("expressId", expressId));
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private List<ExpressListBean.Express> expresses;

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_collection_express_lv, null);
                viewHolder.expressType = (TextView) convertView.findViewById(R.id.express_type);
                viewHolder.expressAdress = (TextView) convertView.findViewById(R.id.express_address);
                viewHolder.expressTime = (TextView) convertView.findViewById(R.id.express_time);
                viewHolder.expressTitle = (TextView) convertView.findViewById(R.id.express_title);
                viewHolder.expressContent = (TextView) convertView.findViewById(R.id.express_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ExpressListBean.Express express = expresses.get(position);
            String expressType = express.getType().equals("1") ? "小包裹" : (express.getType().equals("2") ? "中包裹" : "大包裹");
            viewHolder.expressType.setText(expressType);
            viewHolder.expressTime.setText(express.getCreated_time());
            viewHolder.expressTitle.setText(express.getTitle());
            viewHolder.expressContent.setText(express.getDetail());
            return convertView;
        }

        private class ViewHolder {
            private TextView expressType;
            private TextView expressAdress;
            private TextView expressTime;
            private TextView expressTitle;
            private TextView expressContent;
        }
    }
}
