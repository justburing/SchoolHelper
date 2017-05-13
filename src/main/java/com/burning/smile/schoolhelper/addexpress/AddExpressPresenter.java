package com.burning.smile.schoolhelper.addexpress;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/4/6.
 */
public class AddExpressPresenter implements AddExpressContract.Presenter {

    private AddExpressContract.View view;
    private UserInfoBean userInfo;

    public AddExpressPresenter(AddExpressContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        userInfo = AndroidFileUtil.getObject((AddExpressActivity) view, AppConfig.USER_FILE);
    }

    @Override
    public void postExpress(String title, String detail, String offer, String type, String is_urgent) {
        if (offer.equals("")) {
            view.showExpressOfferError("奖赏金不能为空");
            return;
        }
        if (title.equals("")) {
            view.showExpressTitleError("快递标题不能为空");
            return;
        }
        if (detail.equals("")) {
            view.showExpressDetailError("内容不能为空");
            return;
        }
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().postExpress(userInfo.getToken(), title, detail, offer, type, is_urgent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.showPostFailure("发布失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.showPostFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.showPostFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.showPostFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject.get("success").getAsString().equals("true")) {
                            view.showPostSuccess("发布成功");
                        } else {
                            view.showPostFailure("发布失败");

                        }
                    }
                });

    }

    @Override
    public void start() {

    }
}
