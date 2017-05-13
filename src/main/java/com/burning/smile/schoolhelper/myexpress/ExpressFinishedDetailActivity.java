package com.burning.smile.schoolhelper.myexpress;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/8.
 */
public class ExpressFinishedDetailActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.publisherAvatar)
    CircleImageView publisherAvatar;
    @BindView(R.id.publisherNickname)
    TextView publisherNickname;
    @BindView(R.id.expressTime)
    TextView expressTime;
    @BindView(R.id.expressTitle)
    TextView expressTitle;
    @BindView(R.id.expressContent)
    TextView expressContent;
    @BindView(R.id.expressCoin)
    TextView expressCoin;
    @BindView(R.id.chat)
    Button chat;
    @BindView(R.id.comfirmFinish)
    Button comfirmFinish;

    private UserInfoBean userInfoBean;
    private String id;
    private String publisherId;
    private String status;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        getData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.express_receiver_detail_act;
    }

    @OnClick({R.id.backLL, R.id.chat, R.id.comfirmFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.chat:
                startActivity(new Intent(ExpressFinishedDetailActivity.this, ChatActivity.class)
                        .putExtra("userName", AppConfig.JMESSAGE_PREIX + publisherId));
                break;
            case R.id.comfirmFinish:
                showDialog(id);
                break;
        }
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
                            Glide.with(ExpressFinishedDetailActivity.this).load(myExpressDetailBean.getPublisher().getAvatar()).into(publisherAvatar);
                            publisherNickname.setText(myExpressDetailBean.getPublisher().getNickname());
                            publisherId = myExpressDetailBean.getPublisher().getId();
                            expressTitle.setText(myExpressDetailBean.getTitle());
                            expressContent.setText(myExpressDetailBean.getDetail());
                            expressTime.setText(myExpressDetailBean.getCreated_time());
                            expressCoin.setText("悬赏金额:" + myExpressDetailBean.getOffer() + "流币");
                            if (myExpressDetailBean.getStatus().equals("3")) {
                                comfirmFinish.setBackgroundColor(R.color.red300);
                                comfirmFinish.setEnabled(false);
                            }
                        }
                    }
                });
    }


    public void comfirmDelivery(String id) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().comfirmDelivery(userInfoBean.getToken(), id)
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
                            Toast.makeText(ExpressFinishedDetailActivity.this, "确认送达快递失败", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                Toast.makeText(ExpressFinishedDetailActivity.this, object.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            Toast.makeText(ExpressFinishedDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        Toast.makeText(ExpressFinishedDetailActivity.this, "确认成功", Toast.LENGTH_SHORT).show();
                        comfirmFinish.setBackgroundColor(R.color.red300);
                        comfirmFinish.setEnabled(false);
                    }
                });
    }

    public void showDialog(final String id) {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("确定此快递已经送达到对方手上？")
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
}
