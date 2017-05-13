package com.burning.smile.schoolhelper.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.EncryptUtils;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

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
public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private SharedPreferences mSharedPreferences;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        view.setPresenter(this);

    }

    @Override
    public void login(final String userName, final String userPassword) {
        if (userName.equals("")) {
            view.showEmptyAccout();
            return;
        }
        if (userPassword.equals("")) {
            view.showEmptyPassword();
            return;
        }
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().login(userName, userPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.toString());
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.showLoginFailure("账号不存在或者账号密码错误");
                        } else if (e instanceof HttpException) {
                            if (((HttpException) e).code() == 404) {
                                view.showLoginFailure("账号不存在");
                            } else if (((HttpException) e).code() == 500) {
                                view.showLoginFailure("密码错误");
                            }
                        } else if (e instanceof ConnectException) {
                            view.showLoginFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.showLoginFailure(e.toString());
                        }
                    }

                    @Override
                    public void onNext(final UserInfoBean userInfoBean) {
                        mSharedPreferences.edit().putString(AppConfig.USER_NAME, userName).apply();
                        mSharedPreferences.edit().putBoolean(AppConfig.IS_AUTO_LOGIN, view.isAutoLogin()).apply();
                        if (view.isRememberPassword()) {
                            mSharedPreferences.edit().putString(AppConfig.USER_PASS, userPassword).apply();
                        } else {
                            mSharedPreferences.edit().putString(AppConfig.USER_PASS, "").apply();
                        }
                        mSharedPreferences.edit().putString(AppConfig.USER_AVATAR, userInfoBean.getUser().getAvatar().toString()).apply();
                        AndroidFileUtil.saveObject((Context) view, userInfoBean, AppConfig.USER_FILE);
                        Log.e("token", userInfoBean.getToken());
                        JMessageClient.login(AppConfig.JMESSAGE_PREIX + userInfoBean.getUser().getId(), EncryptUtils.getMD5(userPassword), new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    view.dimissLoadingView();
                                    view.showLoginSuccess("登录成功");
                                    Log.e("success", s);
                                } else {
                                    Log.e("error", i + ">>>" + s);
                                    if (i == 801003 || s.equals("user not exist")) {
                                        JMessageClient.register(AppConfig.JMESSAGE_PREIX + userInfoBean.getUser().getId(), EncryptUtils.getMD5(userPassword), new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                view.dimissLoadingView();
                                                if (i == 0) {
                                                    JMessageClient.getUserInfo(AppConfig.JMESSAGE_PREIX + userInfoBean.getUser().getId(), new GetUserInfoCallback() {
                                                        @Override
                                                        public void gotResult(int i, String s, UserInfo userInfo) {
                                                            if (i == 0) {
                                                                view.dimissLoadingView();
                                                                view.showLoginSuccess("登录成功");
                                                                userInfo.setNickname(userInfoBean.getUser().getNickname());
                                                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, null);
                                                            } else {
                                                                view.dimissLoadingView();
                                                                view.showLoginSuccess("登录成功");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    view.dimissLoadingView();
                                                    view.showLoginFailure("登录失败");
                                                }
                                            }
                                        });
                                    } else if (i == 871201) {
                                        view.dimissLoadingView();
                                        view.showLoginFailure("响应超时,可以尝试切换网络后再登录");
                                    }
//
                                }
                            }
                        });

                    }
                });
    }

    @Override
    public void start() {
        view.showUserAvatar();
        mSharedPreferences = ((LoginActivity) view).getSharedPreferences(AppConfig.USERINFO, Context.MODE_PRIVATE);
        String userName = mSharedPreferences.getString(AppConfig.USER_NAME, "");
        view.fillUserName(userName);
        String userPassword = mSharedPreferences.getString(AppConfig.USER_PASS, "");
        if (!userPassword.equals("")) {
            view.fillUserPassword(userPassword);
            view.checkIsRememberPassword(true);
        }
        view.checkIsAutoLogin(mSharedPreferences.getBoolean(AppConfig.IS_AUTO_LOGIN, false));
    }
}
