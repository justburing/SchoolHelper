package com.burning.smile.schoolhelper.express;

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
 * Created by smile on 2017/3/13.
 */
public class ExpressPresenter implements ExpressContract.Presenter {
    private ExpressContract.View view;
    private UserInfoBean userInfo;

    public ExpressPresenter(ExpressContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        userInfo = AndroidFileUtil.getObject(((ExpressFragment) view).getActivity(), AppConfig.USER_FILE);
    }

    @Override
    public void getExpresses(int start, final int limit, String is_urgent) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().getExpress(userInfo.getToken(), String.valueOf(start), String.valueOf(limit), "", "", "", "", "", "", is_urgent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        view.onLoad();
                        if (e instanceof MalformedJsonException) {
                            view.getDataFailure("没有相关数据");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.getDataFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.getDataFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.getDataFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ExpressListBean expressListBean) {
                        view.onLoad();
                        view.setLoadStart(limit);
                        if (expressListBean != null) {
                            view.dimissLoadingView();
                            view.getDataSuccess("", expressListBean.getResources());
                        }
                    }
                });


    }

    @Override
    public void loadMoreData(final int start, final int limit, String is_urgent) {
        RetrofitUtil.getRetrofitApiInstance().getExpress(userInfo.getToken(), String.valueOf(start), String.valueOf(limit), "", "", "", "", "", "", is_urgent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onLoad();
                        if (e instanceof MalformedJsonException) {
                            view.loadMoreDataFailure("没有相关数据");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.loadMoreDataFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.loadMoreDataFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.loadMoreDataFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ExpressListBean expressListBean) {
                        view.onLoad();
                        if (expressListBean != null && expressListBean.getResources() != null && expressListBean.getResources().size() != 0) {
                            view.setLoadStart(start + limit);
                            view.loadMoreDataSuccess("", expressListBean.getResources());
                        } else {
                            view.setLoadStart(start);
                            view.loadMoreDataFailure("没有更多数据了");
                        }
                    }
                });
    }


    @Override
    public void refreshData() {

    }

    @Override
    public void openDrawerContent() {

    }

    @Override
    public void switchData() {

    }

    @Override
    public void start() {
        getExpresses(view.getStart(), view.getLimit(), view.getIsUrgent());
    }
}
