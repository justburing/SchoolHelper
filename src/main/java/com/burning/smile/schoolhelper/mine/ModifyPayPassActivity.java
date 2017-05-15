package com.burning.smile.schoolhelper.mine;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.PayKeyBoardDialog;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/15.
 */
public class ModifyPayPassActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.loginPassword)
    EditText loginPassword;
    @BindView(R.id.question)
    ImageView question;
    @BindView(R.id.payPassword)
    TextView payPassword;
    @BindView(R.id.comfirmPayPassword)
    TextView comfirmPayPassword;
    @BindView(R.id.modify)
    Button modify;

    private Stack<Integer> payPassNumber;
    private Stack<Integer> confirmPayPassNumber;
    private String payPass = "";
    private String confirmPayPass = "";
    private UserInfoBean userInfoBean;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        toolbarTitle.setText("修改用户支付密码");
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        payPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayKeyBoardDialog(ModifyPayPassActivity.this, R.style.dialog_transparent, payPassword, payPassNumber, new PayKeyBoardDialog.OnInputNumberCodeCallback() {
                    @Override
                    public void onInputFinish(String input, Stack<Integer> numberStack) {
                        // Log.e("pass",input);
                        payPass = input;
                        payPassNumber = numberStack;
                    }
                });
            }
        });
        comfirmPayPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayKeyBoardDialog(ModifyPayPassActivity.this, R.style.dialog_transparent, comfirmPayPassword, confirmPayPassNumber, new PayKeyBoardDialog.OnInputNumberCodeCallback() {
                    @Override
                    public void onInputFinish(String input, Stack<Integer> numberStack) {
                        //Log.e("pass1",input);
                        confirmPayPass = input;
                        confirmPayPassNumber = numberStack;
                    }
                });
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_modify_pay_pass_act;
    }

    @OnClick({R.id.backLL, R.id.question, R.id.modify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.question:
                showQuestionDialog();
                break;
            case R.id.modify:
                modify();
                break;
        }
    }


    public void showQuestionDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_question_dialog, null);
        final Button cancel = (Button) dialogView.findViewById(R.id.ok);
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void modify() {
        if (loginPassword.getText().toString().equals("")) {
            toast("请输入登录密码");
            return;
        }
        if (payPass.equals("") || payPassword.getText().toString().equals("")) {
            toast("请输入支付密码");
            return;
        } else {
            if (payPass.length() != 6 || payPassword.getText().toString().length() != 6) {
                toast("支付密码必须为6位");
                return;
            }
        }
        if (confirmPayPass.equals("") || comfirmPayPassword.getText().toString().equals("")) {
            toast("请输入确认的支付密码");
            return;
        } else {
            if (confirmPayPass.length() != 6 || comfirmPayPassword.getText().toString().length() != 6) {
                toast("确认的支付密码必须为6位");
                return;
            } else {
                if (!confirmPayPass.equals(payPass) || !comfirmPayPassword.getText().toString().equals(payPassword.getText().toString())) {
                    toast("两次支付密码不一致，请确认");
                    return;
                }
            }
        }
//        Log.e("pass",payPass);
//        Log.e("pass1",confirmPayPass);
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().modifyPayPass(userInfoBean.getToken(), loginPassword.getText().toString(), confirmPayPass)
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
                            toast("修改支付密码出错");
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
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (jsonObject.get("success") != null && jsonObject.get("success").equals("true")) {
                            toast("修改支付密码成功");
                            Intent intent = new Intent();
                            intent.putExtra("isPaySet", 1);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            if (jsonObject.getJSONObject("error") != null) {
                                toast(jsonObject.getJSONObject("error").getString("message"));
                            } else {
                                toast("修改支付密码失败");
                            }
                        }
                    }
                });
    }
}
