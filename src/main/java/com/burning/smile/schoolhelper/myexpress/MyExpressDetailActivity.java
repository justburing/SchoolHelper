package com.burning.smile.schoolhelper.myexpress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.chat.ChatActivity;
import com.burning.smile.schoolhelper.data.MyExpressDetailBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.StarRatingView;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/7.
 */
public class MyExpressDetailActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.expressTitle)
    TextView expressTitle;
    @BindView(R.id.expressContent)
    TextView expressContent;
    @BindView(R.id.express_time)
    TextView expressTime;
    @BindView(R.id.expressCoin)
    TextView expressCoin;
    @BindView(R.id.applyerListView)
    ListView applyerListView;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.comfirFinishDeal)
    Button comfirFinishDeal;
    @BindView(R.id.noApplys)
    LinearLayout noApplys;
    @BindView(R.id.cancelDeal)
    Button cancelDeal;

    private ListViewAdapter mAdapter;
    private UserInfoBean userInfoBean;
    private String id;
    private String receiverId;
    private String status;
    private float score;
    private boolean isCan = false;
    private Thread thread;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        id = getIntent().getStringExtra("id");
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        mAdapter = new ListViewAdapter(this);
        applyerListView.setAdapter(mAdapter);
        comfirFinishDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comfirFinishDeal.getText().toString().equals("确认收到")) {
                    showComfirmDialog(id);
                } else {
                    showOpinionDialog(id);
                }
            }
        });
        cancelDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelDeal.getText().toString().equals("我要投诉")) {
                    toast("投诉");
                } else {
                    toast("退单");
                }
            }
        });
        getData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.express_publish_detail_act;
    }


    public void getData() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getMyExpressDetail(userInfoBean.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyExpressDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("获取我发起的快递数据出错");
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
                    public void onNext(MyExpressDetailBean myExpressDetailBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (myExpressDetailBean != null) {
                            expressTitle.setText(myExpressDetailBean.getTitle());
                            expressContent.setText(myExpressDetailBean.getDetail());
                            expressTime.setText(myExpressDetailBean.getCreated_time());
                            expressCoin.setText("悬赏金:" + myExpressDetailBean.getOffer() + "流币");
                            status = myExpressDetailBean.getStatus();
                            if (myExpressDetailBean.getStatus().equals("1")) {
                                tv.setText("接受本单的用户");
                                if (myExpressDetailBean.getApplys() == null || myExpressDetailBean.getApplys().size() == 0) {
                                    noApplys.setVisibility(View.VISIBLE);
                                } else {
                                    noApplys.setVisibility(View.GONE);
                                }
                                mAdapter.setData(myExpressDetailBean.getApplys());
                                comfirFinishDeal.setVisibility(View.GONE);
                                cancelDeal.setVisibility(View.GONE);
                            } else if (myExpressDetailBean.getStatus().equals("2")) {
                                tv.setText("已授权的用户");
                                MyExpressDetailBean.Receiver receiver = myExpressDetailBean.getReceiver();
                                if (receiver != null) {
                                    List<MyExpressDetailBean.ApplysBean> applysBeanList = new ArrayList<MyExpressDetailBean.ApplysBean>();
                                    MyExpressDetailBean.ApplysBean applysBean = new MyExpressDetailBean.ApplysBean();
                                    applysBean.setAvatar(receiver.getAvatar());
                                    applysBean.setId(receiver.getId());
                                    applysBean.setNickname(receiver.getNickname());
                                    applysBeanList.add(applysBean);
                                    mAdapter.setData(applysBeanList);
                                }
                                comfirFinishDeal.setVisibility(View.GONE);
                                cancelDeal.setVisibility(View.VISIBLE);
                            } else if (myExpressDetailBean.getStatus().equals("3")) {
                                tv.setText("已授权的用户");
                                MyExpressDetailBean.Receiver receiver = myExpressDetailBean.getReceiver();
                                if (receiver != null) {
                                    List<MyExpressDetailBean.ApplysBean> applysBeanList = new ArrayList<MyExpressDetailBean.ApplysBean>();
                                    MyExpressDetailBean.ApplysBean applysBean = new MyExpressDetailBean.ApplysBean();
                                    applysBean.setAvatar(receiver.getAvatar());
                                    applysBean.setId(receiver.getId());
                                    applysBean.setNickname(receiver.getNickname());
                                    applysBeanList.add(applysBean);
                                    mAdapter.setData(applysBeanList);
                                }
                                comfirFinishDeal.setVisibility(View.VISIBLE);
                                cancelDeal.setVisibility(View.VISIBLE);
                            } else if (myExpressDetailBean.getStatus().equals("4")) {
                                tv.setText("已授权的用户");
                                MyExpressDetailBean.Receiver receiver = myExpressDetailBean.getReceiver();
                                if (receiver != null) {
                                    List<MyExpressDetailBean.ApplysBean> applysBeanList = new ArrayList<MyExpressDetailBean.ApplysBean>();
                                    MyExpressDetailBean.ApplysBean applysBean = new MyExpressDetailBean.ApplysBean();
                                    applysBean.setAvatar(receiver.getAvatar());
                                    applysBean.setId(receiver.getId());
                                    applysBean.setNickname(receiver.getNickname());
                                    applysBeanList.add(applysBean);
                                    mAdapter.setData(applysBeanList);
                                }
                                comfirFinishDeal.setVisibility(View.VISIBLE);
                                cancelDeal.setVisibility(View.VISIBLE);
                                cancelDeal.setText("我要投诉");
                                comfirFinishDeal.setText("评价订单");
                            }
                            Log.e("status", myExpressDetailBean.getStatus() + ">>" + myExpressDetailBean.getReceiver_id());
                        }
                    }
                });
    }

    public void authExpressDeal(String userId) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().authExpressDeal(userInfoBean.getToken(), id, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("授权失败");
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
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        toast("授权成功");
                    }
                });

    }


    public void showDialog(final String userId) {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("确定授权给此用户吗？")
                .setPositiveButton("我确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        authExpressDeal(userId);
                    }
                })
                .setNegativeButton("我再考虑下", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public void comfirmFinishDeal(String id) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().comfirmFinishDeal(userInfoBean.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(MyExpressDetailActivity.this, "确认接受快递失败", Toast.LENGTH_SHORT).show();
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
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        toast("确认成功");
                        comfirFinishDeal.setBackgroundColor(R.color.grey500);
                        comfirFinishDeal.setEnabled(false);
                    }
                });
    }


    public void reviewOrder(String expressId, String rating, String content) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().reviewOrder(userInfoBean.getToken(), expressId, rating, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("评价订单失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string().trim(), JSONObject.class);
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
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        toast("评价订单成功，感谢您的评价");
                    }
                });
    }
    public void showComfirmDialog(final String id) {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("确定此快递已经送达到您手上？")
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
        View dialogView = LayoutInflater.from(MyExpressDetailActivity.this).inflate(R.layout.view_credit_dialog, null);
        final RelativeLayout root = (RelativeLayout) dialogView.findViewById(R.id.rl1);
        final TextView tv2 = (TextView) dialogView.findViewById(R.id.tv2);
        tv2.setText("+6");
        tv2.setTextColor(ContextCompat.getColor(MyExpressDetailActivity.this, R.color.yellow500));
        final StarRatingView starRatingView = (StarRatingView) dialogView.findViewById(R.id.ratingbar);
        starRatingView.setRate(6);
        final EditText opinion = (EditText) dialogView.findViewById(R.id.opinion);
        final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        final Button submit = (Button) dialogView.findViewById(R.id.submit);
        final Dialog dialog = new Dialog(MyExpressDetailActivity.this, R.style.dialog_transparent_1);
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
        score = 3f;
        starRatingView.setOnRateChangeListener(new StarRatingView.OnRateChangeListener() {
            @Override
            public void onRateChange(int rate) {
                Log.e("rate", rate / 2 - 0.5 + "");
                score = rate / 2f;
                int i = 0;
                int credit = 0;
                int color = R.color.red500;
                if (score <= 1) {
                    i = 0;
                    if (score == 0) {
                        credit = -12;
                    } else if (score == 0.5) {
                        credit = -9;
                    } else {
                        credit = -6;
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
                        credit = +3;
                    } else {
                        credit = +6;
                    }
                    color = R.color.yellow500;
                } else if (score > 3 && score <= 4) {
                    i = 3;
                    if (score == 3.5) {
                        credit = +9;
                    } else {
                        credit = +12;
                    }
                    color = R.color.blue600;
                } else if (score > 4 && score <= 5) {
                    i = 4;
                    if (score == 4.5) {
                        credit = +15;
                    } else {
                        credit = +18;
                    }
                    color = R.color.purplea700;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins((int) starRatingView.getX() + (int) starRatingView.getChildAt(i).getX(), 0, 0, 0);
                if (!isCan) {
                    return;
                }
                isCan = false;
                final TextView textView = new TextView(MyExpressDetailActivity.this);
                textView.setLayoutParams(layoutParams);
                textView.setText(credit > 0 ? "+" + credit : credit + "");
                textView.setTextSize(16);
                textView.setTextColor(ContextCompat.getColor(MyExpressDetailActivity.this, color));
                final Animation out = AnimationUtils.loadAnimation(MyExpressDetailActivity.this, R.anim.tv_gone);
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
                tv2.setTextColor(ContextCompat.getColor(MyExpressDetailActivity.this, color));
                root.requestLayout();
                root.postInvalidate();
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
                reviewOrder(expressId,String.valueOf(score),opinion.getText().toString().trim().equals("")?"暂无评价":opinion.getText().toString().trim());
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private List<MyExpressDetailBean.ApplysBean> applys;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<MyExpressDetailBean.ApplysBean> applys) {
            this.applys = applys;
            notifyDataSetChanged();
        }

        public List<MyExpressDetailBean.ApplysBean> getData() {
            return applys;
        }

        @Override
        public int getCount() {
            return applys == null ? 0 : applys.size();
        }

        @Override
        public MyExpressDetailBean.ApplysBean getItem(int position) {
            return applys == null ? null : applys.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_express_detail_applyer_lv, null);
                viewHolder.item_avatar = (CircleImageView) convertView.findViewById(R.id.item_avatar);
                viewHolder.item_nickName = (TextView) convertView.findViewById(R.id.item_nickName);
                viewHolder.item_credit = (TextView) convertView.findViewById(R.id.item_user_credit);
                viewHolder.item_auth = (Button) convertView.findViewById(R.id.item_auth);
                viewHolder.item_recommend = (TextView) convertView.findViewById(R.id.item_recommend);
                convertView.setTag(viewHolder);
                ;
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final MyExpressDetailBean.ApplysBean applysBean = applys.get(position);
            Glide.with(mContext).load(applysBean.getAvatar()).into(viewHolder.item_avatar);
            viewHolder.item_nickName.setText(applysBean.getNickname());
            if (status.equals("1")) {
                viewHolder.item_auth.setBackgroundResource(R.drawable.rect_auth_btn_bg);
                viewHolder.item_auth.setText("授权");
                viewHolder.item_auth.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.item_auth.setBackgroundResource(R.drawable.rect_auth_btn_disable_bg);
                viewHolder.item_auth.setText("已授权");
                viewHolder.item_auth.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }
            viewHolder.item_credit.setText("(信用度" + ((int) (100 * Math.random()) + 200) + ")");
            if (position == 0) {
                viewHolder.item_recommend.setVisibility(View.VISIBLE);
                final Animation out = AnimationUtils.loadAnimation(mContext, R.anim.message_out);
                final Animation in = AnimationUtils.loadAnimation(mContext, R.anim.message_in);
                viewHolder.item_recommend.startAnimation(in);
                in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewHolder.item_recommend.startAnimation(out);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewHolder.item_recommend.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                viewHolder.item_recommend.setVisibility(View.GONE);
            }
            if (status.equals("1")) {
                viewHolder.item_auth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(applysBean.getId());
                    }
                });
            }
            viewHolder.item_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MyExpressDetailActivity.this, ChatActivity.class)
                            .putExtra("userName", AppConfig.JMESSAGE_PREIX + applysBean.getId()));
                }
            });

            return convertView;
        }


        private class ViewHolder {
            private CircleImageView item_avatar;
            private TextView item_nickName;
            private TextView item_credit;
            private Button item_auth;
            private TextView item_recommend;
        }
    }
}
