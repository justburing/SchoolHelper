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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
public class MineReceiverFragment extends Fragment {

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
    private boolean isCan = false;
    private Thread thread;

    public static MineReceiverFragment newInstance() {
        MineReceiverFragment fragment = new MineReceiverFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.express_mine_receiver_frag, null);
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
                if (expresses.get(position).getStatus().equals("4")) {
                    showOpinionDialog();
                } else {
                    startActivity(new Intent(getActivity(), ReceiverExpressDetailActivity.class).putExtra("id", expresses.get(position).getId()));
                }
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
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment(), false);
        RetrofitUtil.getRetrofitApiInstance().getMyExpress(userInfoBean.getToken(), String.valueOf(start), String.valueOf(limit), "receiver_id")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
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
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
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


    public void comfirmDelivery(String id) {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().comfirmDelivery(userInfoBean.getToken(), id)
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
                            Toast.makeText(getActivity(), "确认送达快递失败", Toast.LENGTH_SHORT).show();
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

    public void showDialog(final String id) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage("确定此快递已经送达到对方手上？")
                .setPositiveButton("我确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        comfirmDelivery(id);
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

    public void showOpinionDialog() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    isCan = true;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_credit_dialog, null);
        final RelativeLayout root = (RelativeLayout) dialogView.findViewById(R.id.rl1);
        final TextView tv2 = (TextView) dialogView.findViewById(R.id.tv2);
        tv2.setText("+2");
        tv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow500));
        final StarRatingView starRatingView = (StarRatingView) dialogView.findViewById(R.id.ratingbar);
        starRatingView.setRate(5);
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
        score = 2.5f;
        starRatingView.setOnRateChangeListener(new StarRatingView.OnRateChangeListener() {
            @Override
            public void onRateChange(int rate) {
             //   Log.e("rate", rate / 2 - 0.5 + "");
                score = rate / 2f;
                int i = 0;
                int credit = 0;
                int color = R.color.red500;
                if (score <= 1) {
                    i = 0;
                    if (score == 0) {
                        credit = -10;
                    } else if (score == 0.5) {
                        credit = -8;
                    } else {
                        credit = -5;
                    }
                    color = R.color.red800;
                } else if (score > 1 && score <= 2) {
                    i = 1;
                    if (score == 1.5) {
                        credit = -3;
                    } else {
                        credit = 0;
                    }
                    color = R.color.red500;
                } else if (score > 2 && score <= 3) {
                    i = 2;
                    if (score == 2.5) {
                        credit = +2;
                    } else {
                        credit = +5;
                    }
                    color = R.color.yellow500;
                } else if (score > 3 && score <= 4) {
                    i = 3;
                    if (score == 3.5) {
                        credit = +7;
                    } else {
                        credit = +10;
                    }
                    color = R.color.blue600;
                } else if (score > 4 && score <= 5) {
                    i = 4;
                    if (score == 4.5) {
                        credit = +12;
                    } else {
                        credit = +15;
                    }
                    color = R.color.purplea700;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins((int) starRatingView.getX() + (int) starRatingView.getChildAt(i).getX(), 0, 0, 0);
                if (!isCan) {
                    return;
                }
                isCan = false;
                final TextView textView = new TextView(getActivity());
                textView.setLayoutParams(layoutParams);
                textView.setText(credit > 0 ? "+" + credit : credit + "");
                textView.setTextSize(16);
                textView.setTextColor(ContextCompat.getColor(getActivity(), color));
                final Animation out = AnimationUtils.loadAnimation(getActivity(), R.anim.tv_gone);
                out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                root.addView(textView);
                textView.startAnimation(out);
                tv2.setText(credit > 0 ? "+" + credit : credit + "");
                tv2.setTextColor(ContextCompat.getColor(getActivity(), color));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                thread = null;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                thread = null;
                //Log.e("score", score + "");
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_express_mine_receiver, null);
                viewHolder.item_express_type = (TextView) convertView.findViewById(R.id.item_express_type);
                viewHolder.item_express_address = (TextView) convertView.findViewById(R.id.item_express_address);
                viewHolder.item_express_time = (TextView) convertView.findViewById(R.id.item_express_time);
                viewHolder.item_express_title = (TextView) convertView.findViewById(R.id.item_express_title);
                viewHolder.item_express_content = (TextView) convertView.findViewById(R.id.item_express_content);
                viewHolder.item_express_delivery = (TextView) convertView.findViewById(R.id.item_express_delivery);
                viewHolder.item_status_image = (ImageView) convertView.findViewById(R.id.statusImage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ExpressListBean.Express express = expresses.get(position);
            String expressType = express.getType().equals("1") ? "小包裹" : (express.getType().equals("2") ? "中包裹" : "大包裹");
            viewHolder.item_express_type.setText(expressType);
            viewHolder.item_express_time.setText(express.getCreated_time());
            viewHolder.item_express_title.setText(express.getTitle());
            viewHolder.item_express_content.setText(express.getDetail());
            //String status = express.getStatus().equals("1")?"暂无人接单":(express.getStatus().equals("2")?"有人接单":(express.getStatus().equals("3")?"已送达":"已确认"));
//            viewHolder.item_express_status.setText(status);
 //           Log.e("status", express.getStatus());
            String status = express.getStatus();
            if (status.equals("1")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.GONE);
                viewHolder.item_express_delivery.setBackgroundResource(R.drawable.rect_express_btn_disable);
                viewHolder.item_express_delivery.setEnabled(true);
                viewHolder.item_express_delivery.setVisibility(View.GONE);
            } else if (status.equals("2")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_auth_bg);
                viewHolder.item_express_delivery.setBackgroundResource(R.drawable.rect_express_btn);
                viewHolder.item_express_delivery.setEnabled(true);
                viewHolder.item_express_delivery.setText("确认送达");
                viewHolder.item_express_delivery.setVisibility(View.VISIBLE);
            } else if (status.equals("3")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_dark_1));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_status_3);
                viewHolder.item_express_delivery.setBackgroundResource(R.drawable.rect_express_btn_disable);
                viewHolder.item_express_delivery.setEnabled(false);
                viewHolder.item_express_delivery.setText("确认送达");
                viewHolder.item_express_delivery.setVisibility(View.GONE);
            } else if (status.equals("4")) {
                viewHolder.item_express_type.setBackgroundResource(R.drawable.rect_express_type_bg_1_disable);
                viewHolder.item_express_type.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_express_address.setBackgroundResource(R.drawable.rect_express_adreess_bg_1_disable);
                viewHolder.item_express_address.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_express_title.setTextColor(ContextCompat.getColor(mContext, R.color.text_grey));
                viewHolder.item_status_image.setVisibility(View.VISIBLE);
                viewHolder.item_status_image.setImageResource(R.mipmap.ic_status_4);
                viewHolder.item_express_delivery.setBackgroundResource(R.drawable.rect_express_opinion_btn);
                viewHolder.item_express_delivery.setEnabled(true);
                viewHolder.item_express_delivery.setText("评价该单");
                viewHolder.item_express_delivery.setVisibility(View.GONE);
            }
            viewHolder.item_express_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.item_express_delivery.getText().toString().equals("评价该单")) {
                        showOpinionDialog();
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
            private TextView item_express_time;
            private TextView item_express_title;
            private TextView item_express_content;
            private TextView item_express_delivery;
            private ImageView item_status_image;
        }
    }
}
