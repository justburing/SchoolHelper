package com.burning.smile.schoolhelper.mine;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.GradeProgressView;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/16.
 */
public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.isUerVertifyed)
    ImageView isUerVertifyed;
    @BindView(R.id.userNickname)
    TextView userNickname;
    @BindView(R.id.userGender)
    TextView userGender;
    @BindView(R.id.creditView)
    GradeProgressView creditView;

    private String userId;

    private int progress = 0;
    private int credit;
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (progress<=credit){
                try {
                    Thread.sleep(30);
                    creditView.setProgress(progress);
                    progress += 3;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void init() {
        ButterKnife.bind(this);
        userId = getIntent().getStringExtra("userId");
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        creditView.setTextSize(40,20);
        getUserInfo();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_profile_view_act;
    }


    public void getUserInfo(){
        AndroidFragUtil.showDialog(getSupportFragmentManager(),new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getUserInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean.UserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("获取用户个人信息出错");
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
                        finish();
                    }

                    @Override
                    public void onNext(UserInfoBean.UserBean userBean){
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        Glide.with(UserInfoActivity.this).load(userBean.getAvatar()).crossFade().into(userAvatar);
                        userNickname.setText(userBean.getNickname());
                        credit = Integer.valueOf(userBean.getCredit());
                        thread.start();
                    }
                });
    }
}
