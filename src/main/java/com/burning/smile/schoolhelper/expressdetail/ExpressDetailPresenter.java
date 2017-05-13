package com.burning.smile.schoolhelper.expressdetail;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
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
public class ExpressDetailPresenter implements ExpressDetailContract.Presenter {

    private ExpressDetailContract.View view;
    private UserInfoBean userInfo;

    public ExpressDetailPresenter(ExpressDetailContract.View view) {
        userInfo = AndroidFileUtil.getObject((ExpressDetailActivity) view, AppConfig.USER_FILE);
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void loadExpressDetail(String expressId) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().getExpressDetail(userInfo.getToken(), expressId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean.Express>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.loadDataFailure("没有相关数据");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.loadDataFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.loadDataFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.loadDataFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ExpressListBean.Express express) {
                        view.dimissLoadingView();
                        if (express != null) {
                            view.loadDataSuccess("", express);
                        } else {
                            view.loadDataFailure("获取快递信息出错");
                        }
                    }
                });
    }

    @Override
    public void dealExpress(String expressId) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().dealExpress(userInfo.getToken(), expressId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.dealExpressFailure("没有相关数据");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.dealExpressFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.dealExpressFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.dealExpressFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject != null) {
                            view.dealExpressSuccess("接单成功");
                        } else {
                            view.dealExpressFailure("接单失败");
                        }
                    }
                });
    }

    @Override
    public void collect(String id) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().collect(userInfo.getToken(), "express", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.collectFailure("收藏信息失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.collectFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.collectFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.collectFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject.getString("success").equals("true")) {
                            view.collectSuccessed("收藏成功");
                        } else {
                            view.collectFailure("收藏失败");
                        }
                    }
                });

    }

    @Override
    public void cancelCollect(String id) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().cancelCollect(userInfo.getToken(), "express", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.cancelCollectFailure("取消收藏失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.cancelCollectFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.cancelCollectFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.cancelCollectFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject.getString("success").equals("true")) {
                            view.cancelCollectSuccessed("取消收藏成功");
                        } else {
                            view.cancelCollectFailure("取消收藏失败");
                        }
                    }
                });

    }

    @Override
    public void start() {
        loadExpressDetail(view.getExpressId());
    }
}
