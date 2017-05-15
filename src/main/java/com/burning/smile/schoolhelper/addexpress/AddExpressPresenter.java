package com.burning.smile.schoolhelper.addexpress;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.PayKeyBoardPopwindow;
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
    //private SharedPreferences mSharedPreferences;

    public AddExpressPresenter(AddExpressContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        userInfo = AndroidFileUtil.getObject((AddExpressActivity) view, AppConfig.USER_FILE);
        view.setIsPaySet(userInfo.getUser().getIs_pay_set());
       // mSharedPreferences = ((AddExpressActivity) view).getSharedPreferences(AppConfig.USERINFO, Context.MODE_PRIVATE);

    }

    @Override
    public void postExpress(String title, String detail, final String offer, String type, String is_urgent, String pay_password) {
        view.showLoadingView();
        RetrofitUtil.getRetrofitApiInstance().postExpress(userInfo.getToken(), title, detail, offer, type, is_urgent, pay_password)
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
                            userInfo.getUser().setCoin(Integer.valueOf(userInfo.getUser().getCoin())-Integer.valueOf(offer)+"");
                            AndroidFileUtil.saveObject((AddExpressActivity)view,userInfo,AppConfig.USER_FILE);
                        } else {
                            view.showPostFailure("发布失败");

                        }
                    }
                });

    }

    @Override
    public void showPayDialog(final String title, final String detail, final String offer, final String type, final String is_urgent) {
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
        final View dialogView = LayoutInflater.from((AddExpressActivity) view).inflate(R.layout.view_comfirm_pay_dialog, null);
        ImageView backIv = (ImageView) dialogView.findViewById(R.id.backIv);
        TextView orderInfoTv = (TextView) dialogView.findViewById(R.id.orderInfoTv);
        TextView orderUsername = (TextView) dialogView.findViewById(R.id.orderUsername);
        orderUsername.setText(userInfo.getUser().getUsername());
        TextView orderMoney = (TextView) dialogView.findViewById(R.id.orderMoney);
        orderMoney.setText(offer + " 流币");
        Button pay = (Button) dialogView.findViewById(R.id.pay);
        final Dialog dialog = new Dialog((AddExpressActivity) view,R.style.dialog_transparent);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.setFocusable(true);
        dialogView.findViewById(R.id.view).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                int height = dialogView.findViewById(R.id.view).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dialog.dismiss();
                    }
                }
                return true;
            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view.getIsPaySet()==0){
                    view.showInputPayPassError("您尚未设置支付密码");
                }else{
                    showPassView(dialog,title, detail, offer, type, is_urgent);
                }
            }
        });
    }

    @Override
    public void showPassView(final Dialog dialog, final String title, final String detail, final String offer, final String type, final String is_urgent) {
     new PayKeyBoardPopwindow((AddExpressActivity) view,R.style.dialog_transparent, new PayKeyBoardPopwindow.OnInputNumberCodeCallback() {
                @Override
                public void onInputFinish(String input) {
                    dialog.dismiss();
                   // Log.e("input", input);
                    postExpress(title,detail,offer,type,is_urgent, input);
                }
            });
    }

    @Override
    public void start() {

    }
}
