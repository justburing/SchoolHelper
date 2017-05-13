package com.burning.smile.schoolhelper.funk;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.FunkListBean;
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
public class FunkPresenter implements FunkContract.Presenter {
    private FunkContract.View view;
    private UserInfoBean userInfo;


    public FunkPresenter(FunkContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        userInfo = AndroidFileUtil.getObject(((FunkFragment) view).getActivity(), AppConfig.USER_FILE);
    }


    @Override
    public void start() {
        getFunks(view.getStart(), view.getLimit(), view.getOrderBy());
    }

    @Override
    public void getFunks(int start, final int limit, String orderBy) {
        /**
         start false string 0 起始页
         limit false string 10 每页数量
         orderby false string updated_time desc 排序（目前支持created_time, updated_time, post_num, hits, price）排序
         title false string 根据标题检索
         category_id false string 根据分类检索
         status false string 1 (0:未发布， 1:已发布，2: 已关闭)
         */
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().getFunk(userInfo.getToken(), String.valueOf(start), String.valueOf(limit), orderBy, "", "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkListBean>() {
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
                    public void onNext(FunkListBean funkListBean) {
                        view.onLoad();
                        view.setLoadStart(limit);
                        if (funkListBean != null) {
                            view.dimissLoadingView();
                            view.getDataSuccess("", funkListBean.getResources());
                        }
                    }
                });
    }

    @Override
    public void loadMoreData(final int start, final int limit, String orderBy) {
        RetrofitUtil.getRetrofitApiInstance().getFunk(userInfo.getToken(), String.valueOf(start), String.valueOf(limit), orderBy, "", "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkListBean>() {
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
                    public void onNext(FunkListBean funkListBean) {
                        view.onLoad();
                        if (funkListBean != null && funkListBean.getResources() != null && funkListBean.getResources().size() != 0) {
                            view.setLoadStart(start + limit);
                            view.loadMoreDataSuccess("", funkListBean.getResources());
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
}
