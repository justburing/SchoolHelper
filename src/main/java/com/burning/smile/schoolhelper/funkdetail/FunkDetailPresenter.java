package com.burning.smile.schoolhelper.funkdetail;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.data.FunkCommentListBean;
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
public class FunkDetailPresenter implements FunkDetailContract.Presenter {

    private FunkDetailContract.View view;
    private UserInfoBean userInfo;

    public FunkDetailPresenter(FunkDetailContract.View view) {
        userInfo = AndroidFileUtil.getObject((FunkDetailActiviity) view, AppConfig.USER_FILE);
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void loadFunkDetail(String funkId) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().getFunkDetail(userInfo.getToken(), funkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkListBean.Funk>() {
                    @Override
                    public void onCompleted() {
                        view.dimissLoadingView();
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
                        } else {
                            view.loadDataFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(FunkListBean.Funk funk) {
                        view.dimissLoadingView();
                        if (funk != null) {
                            view.loadDataSuccess("", funk);
                        } else {
                            view.loadDataFailure("获取信息出错");
                        }
                    }
                });

    }

    @Override
    public void loadFunkComment(String funkId) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().getFunkComments(userInfo.getToken(), funkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkCommentListBean>() {
                    @Override
                    public void onCompleted() {
                        view.dimissLoadingView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.loadCommentFailure("没有相关数据");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.loadCommentFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            view.loadCommentFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(FunkCommentListBean bean) {
                        view.dimissLoadingView();
                        if (bean != null) {
                            view.loadCommentSuccess("", bean.getResources());
                        } else {
                            view.loadCommentFailure("获取评论信息出错");
                        }
                    }
                });
    }

    @Override
    public void postFunkComment(String funkId, String content, String toUserId) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().postFunkComment(userInfo.getToken(), funkId, content, toUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunkCommentListBean.PostComment>() {
                    @Override
                    public void onCompleted() {
                        view.dimissLoadingView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dimissLoadingView();
                        if (e instanceof MalformedJsonException) {
                            view.postCommentFailure("发表评论失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.postCommentFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            view.postCommentFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(FunkCommentListBean.PostComment bean) {
                        view.dimissLoadingView();
                        if (bean != null) {
                            view.postCommentSuccess("", bean);
                        } else {
                            view.postCommentFailure("发表评论出错");
                        }
                    }
                });
    }


    @Override
    public void collect(String id) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().collect(userInfo.getToken(), "goods", id)
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
        RetrofitUtil.getRetrofitApiInstance().cancelCollect(userInfo.getToken(), "goods", id)
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
    public void likeFunk(String id) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().likeFunk(userInfo.getToken(), id)
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
                            view.likeFunkFailure("点赞失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                view.likeFunkFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.likeFunkFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.likeFunkFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject.getString("success").equals("true")) {
                            view.likeFunkSuccessed("点赞成功");
                        } else {
                            view.likeFunkFailure("点赞失败");
                        }
                    }
                });

    }

    @Override
    public void cancelLikeFunk(String id) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().cancelLikeFunk(userInfo.getToken(), id)
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
                            view.cancelLikeFunkFailure("取消点赞失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string().trim(), JSONObject.class);
                                view.cancelLikeFunkFailure(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            view.cancelLikeFunkFailure("当前无网络,请检查网络状况后重试");
                        } else {
                            view.cancelLikeFunkFailure(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        view.dimissLoadingView();
                        if (jsonObject.getString("success").equals("true")) {
                            view.cancelLikeFunkSuccessed("取消点赞成功");
                        } else {
                            view.cancelLikeFunkFailure("取消点赞失败");
                        }
                    }
                });

    }

    @Override
    public void start() {
        loadFunkDetail(view.getFunkId());
    }
}
