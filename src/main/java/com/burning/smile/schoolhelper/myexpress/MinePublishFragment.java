package com.burning.smile.schoolhelper.myexpress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.pulltorefresh.PullToRefreshListFooter;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.StarRatingView;
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
public class MinePublishFragment extends Fragment {

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
    private List<ExpressListBean.Express> expresses;
    private boolean isLoadMore = false;
    private float score;

    public static MinePublishFragment newInstance() {
        MinePublishFragment fragment = new MinePublishFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.express_mine_publish_frag, null);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), MyExpressDetailActivity.class).putExtra("id", expresses.get(position).getId()));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        //  AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(),new LoadingFragment(),false);
        RetrofitUtil.getRetrofitApiInstance().getMyExpress(userInfoBean.getToken(), String.valueOf(start), String.valueOf(limit), "publisher_id")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "获取我发起的快递数据出错", Toast.LENGTH_SHORT).show();
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
                    public void onNext(ExpressListBean expressListBean) {
                        //    AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (!isLoadMore) {
                            expresses = expressListBean.getResources();
                            if (expresses != null && expresses.size() > 0) {
                                mAdapter.notifyDataSetChanged();
                                noDataView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                            }
                        } else {
                            isLoadMore = false;
                            if (expressListBean.getResources() != null && expressListBean.getResources().size() > 0) {
                                expresses.addAll(expressListBean.getResources());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                start = 0;
                                Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    public void comfirmFinishDeal(String id) {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().comfirmFinishDeal(userInfoBean.getToken(), id)
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
                            Toast.makeText(getActivity(), "确认接受快递失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "确认成功", Toast.LENGTH_SHORT).show();
                        getData();
                    }
                });
    }


    public void reviewOrder(String expressId, String rating, String content) {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().reviewOrder(userInfoBean.getToken(), expressId, rating, content)
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
                            Toast.makeText(getActivity(), "评价订单失败", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string().trim(), JSONObject.class);
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
                        Toast.makeText(getActivity(), "评价订单成功，感谢您的评价", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showDialog(final String id) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage("确定此快递已经送达到您手上？")
                .setPositiveButton("我确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        comfirmFinishDeal(id);
                    }
                })
                .setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    public void showOpinionDialog(final String expressId) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_credit_dialog, null);
        final TextView tv2 = (TextView) dialogView.findViewById(R.id.tv2);
        tv2.setVisibility(View.GONE);
        final StarRatingView starRatingView = (StarRatingView) dialogView.findViewById(R.id.ratingbar);
        starRatingView.setRate(6);
        final EditText opinion = (EditText) dialogView.findViewById(R.id.opinion);
        final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        final Button submit = (Button) dialogView.findViewById(R.id.submit);
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_transparent_1);
        dialog.setCancelable(false);
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
        score = 3.0f;
        starRatingView.setOnRateChangeListener(new StarRatingView.OnRateChangeListener() {
            @Override
            public void onRateChange(int rate) {
                score = rate / 2f;

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                reviewOrder(expressId,String.valueOf(score),opinion.getText().toString().trim().equals("")?"暂无评价":opinion.getText().toString().trim());
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;

        public ListViewAdapter(Context context) {
            this.mContext = context;
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_express_mine_publish, null);
                viewHolder.item_express_type = (TextView) convertView.findViewById(R.id.item_express_type);
                viewHolder.item_express_address = (TextView) convertView.findViewById(R.id.item_express_address);
                viewHolder.item_express_status = (TextView) convertView.findViewById(R.id.item_express_status);
                viewHolder.item_express_time = (TextView) convertView.findViewById(R.id.item_express_time);
                viewHolder.item_express_title = (TextView) convertView.findViewById(R.id.item_express_title);
                viewHolder.item_express_content = (TextView) convertView.findViewById(R.id.item_express_content);
                viewHolder.item_express_pay = (TextView) convertView.findViewById(R.id.item_express_pay);
                viewHolder.item_status_image = (ImageView) convertView.findViewById(R.id.statusImage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ExpressListBean.Express express = expresses.get(position);
            String expressType = express.getType().equals("1") ? "小包裹" : (express.getType().equals("2") ? "中包裹" : "大包裹");
            viewHolder.item_express_type.setText(expressType);
            String status = express.getStatus();
            if (status.equals("1")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.GONE);
                viewHolder.item_express_pay.setVisibility(View.GONE);
            } else if (status.equals("2")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_auth_bg);
                viewHolder.item_express_pay.setVisibility(View.GONE);
            } else if (status.equals("3")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_status_3);
                viewHolder.item_express_pay.setBackgroundResource(R.drawable.rect_express_btn);
                viewHolder.item_express_pay.setEnabled(true);
                viewHolder.item_express_pay.setText("确认送达并付款");
                viewHolder.item_express_pay.setVisibility(View.VISIBLE);
            } else if (status.equals("4")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1_disable);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1_disable);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_status_4);
                viewHolder.item_express_pay.setBackgroundResource(R.drawable.rect_express_opinion_btn);
                viewHolder.item_express_pay.setEnabled(true);
                viewHolder.item_express_pay.setText("评价该单");
                viewHolder.item_express_pay.setVisibility(View.VISIBLE);
            }
            viewHolder.item_express_time.setText(express.getCreated_time());
            viewHolder.item_express_title.setText(express.getTitle());
            viewHolder.item_express_content.setText(express.getDetail());
            viewHolder.item_express_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.item_express_pay.getText().toString().equals("评价该单")) {
                        showOpinionDialog(express.getId());
                    } else {
                        showDialog(express.getId());
                    }
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView item_express_type;
            private TextView item_express_address;
            private TextView item_express_status;
            private TextView item_express_time;
            private TextView item_express_title;
            private TextView item_express_content;
            private TextView item_express_pay;
            private ImageView item_status_image;
        }
    }
}
