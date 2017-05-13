package com.burning.smile.schoolhelper.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.tools.EncryptUtils;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by smile on 2017/3/8.
 */
public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View view;
    private SharedPreferences mSharedPreferences;
    private AndroidCameraUtil mCameraUtil;
    private String filePath;
    private File takePhotoFile;

    public RegisterPresenter(RegisterContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public String takePhoto() {
        takePhotoFile = new File(AndroidCameraUtil.dir, "schoolhelper_" + System.currentTimeMillis() + ".jpg");
        filePath = takePhotoFile.getPath();
        mCameraUtil.takePhoto(takePhotoFile);
        return filePath;
    }

    @Override
    public void choosePicture() {
        mCameraUtil.choosePhoto();
    }

    @Override
    public void register(final String userName, String userNickname, String userEmail, final String userPassword) {
        if (TextUtils.isEmpty(userName)) {
            view.showUserNameEmptyError();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            view.showUserPasswordEmptyError();
            return;
        }
        if (TextUtils.isEmpty(userNickname)) {
            userNickname = userName;
        }
        if (TextUtils.isEmpty(userEmail)) {
            userEmail = "";
        }
        view.showLoadingView();
        final String finalUserNickname = userNickname;
        RetrofitUtil.getRetrofitApiInstance().register(userName, userPassword, userEmail, userNickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean.UserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.toString());
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.showRegisterFialure("用户已存在或昵称校验失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.showRegisterFialure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.showRegisterFialure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.showRegisterFialure(e.toString());
                        }
                    }

                    @Override
                    public void onNext(final UserInfoBean.UserBean userBean) {
                        if (userBean != null) {
                            JMessageClient.register(AppConfig.JMESSAGE_PREIX + userBean.getId(), EncryptUtils.getMD5(userPassword), new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        JMessageClient.getUserInfo(AppConfig.JMESSAGE_PREIX + userBean.getId(), new GetUserInfoCallback() {
                                            @Override
                                            public void gotResult(int i, String s, UserInfo userInfo) {
                                                if (i == 0) {
                                                    userInfo.setNickname(finalUserNickname);
                                                    JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                                                        @Override
                                                        public void gotResult(int i, String s) {
                                                            view.dimissLoadingView();
                                                            view.showRegisterSuccess("注册成功");
                                                            if (i == 0) {
                                                                Log.e("yes", "yes");
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } else {
                                        view.dimissLoadingView();
                                        view.showRegisterSuccess("注册成功");
                                    }
                                }
                            });
                        } else {
                            view.dimissLoadingView();
                            view.showRegisterFialure("注册失败");
                        }
                    }
                });
    }

    @Override
    public void start() {
        mSharedPreferences = ((RegisteActivity) view).getSharedPreferences(AppConfig.USERINFO, Context.MODE_PRIVATE);
        mCameraUtil = AndroidCameraUtil.getInstance((RegisteActivity) view);
    }

}
