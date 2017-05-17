package com.burning.smile.schoolhelper.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.UpProgressListener;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.ProcessImageView;
import com.google.gson.stream.MalformedJsonException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static cn.smssdk.SMSSDK.getVerificationCode;
import static cn.smssdk.SMSSDK.submitVerificationCode;

/**
 * Created by smile on 2017/5/16.
 */
public class UserProfileVertifyActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.question)
    ImageView question;
    @BindView(R.id.userTruename)
    EditText userTruename;
    @BindView(R.id.userStudentId)
    EditText userStudentId;
    @BindView(R.id.userPhone)
    EditText userPhone;
    @BindView(R.id.userPhoneVertifyCode)
    EditText userPhoneVertifyCode;
    @BindView(R.id.sendPhoneVertifyCode)
    Button sendPhoneVertifyCode;
    @BindView(R.id.userEmail)
    EditText userEmail;
    @BindView(R.id.takePhoto)
    ImageView takePhoto;
    @BindView(R.id.userVertifyPicture)
    ProcessImageView userVertifyPicture;
    @BindView(R.id.comfirm)
    Button comfirm;

    private UserInfoBean userInfoBean;
    private AndroidCameraUtil cameraUtil;
    private File picFile;
    private String filePath;
    private UpProgressListener mListener;
    //控制按钮样式是否改变
    private boolean tag = true;
    //每次验证请求需要间隔60S
    private int i = 60;
    private String uri;
    private EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Message msg = new Message();
                    msg.arg1 = 0;
                    msg.obj = data;
                    handler.sendMessage(msg);
                    Log.d(TAG, "提交验证码成功");
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Log.e("data", data.toString());
                    Message msg = new Message();
                    //获取验证码成功
                    msg.arg1 = 1;
                    msg.obj = "获取验证码成功";
                    handler.sendMessage(msg);
                    Log.d(TAG, "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    Message msg = new Message();
                    //返回支持发送验证码的国家列表
                    msg.arg1 = 2;
                    msg.obj = "返回支持发送验证码的国家列表";
                    handler.sendMessage(msg);
                    Log.d(TAG, "返回支持发送验证码的国家列表");
                }
            } else {
                ((Throwable) data).printStackTrace();
                Message msg = new Message();
                //返回支持发送验证码的国家列表
                msg.arg1 = 3;
                msg.obj = "验证失败";
                handler.sendMessage(msg);
                Log.d(TAG, "验证失败");
                ((Throwable) data).printStackTrace();
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0:
                    //客户端验证成功,返回校验的手机和国家代码phone/country
                    userVertify();
                    break;
                case 1:
                    //获取验证码成功
                    toast("获取验证码成功,请及时查看");
                    break;
                case 2:
                    //返回支持发送验证码的国家列表
                    break;
                case 3:
                    //验证码有误
                    toast("验证码有误,请确认");
                    break;
            }
        }
    };

    @Override
    protected void init() {
        ButterKnife.bind(this);
        toolbarTitle.setText("用户实名验证");
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        cameraUtil = AndroidCameraUtil.getInstance(this);
        mListener = userVertifyPicture;
        showQuestionDialog();
        userVertifyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        SMSSDK.registerEventHandler(eventHandler); //注册短信回调
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_profile_vertify_act;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @OnClick({R.id.backLL, R.id.question, R.id.sendPhoneVertifyCode, R.id.comfirm, R.id.takePhoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.question:
                showQuestionDialog();
                break;
            case R.id.sendPhoneVertifyCode:
                if (isMobileNO(userPhone.getText().toString())) {
                    getVerificationCode("86", userPhone.getText().toString());
                    changeBtnGetCode();
                } else {
                    toast("手机号码格式有误");
                }
                break;
            case R.id.takePhoto:
                picFile = new File(AndroidCameraUtil.dir, "schoolhelper_user_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg");
                filePath = picFile.getPath();
                cameraUtil.takePhoto(picFile);
                break;
            case R.id.comfirm:
                submitVerificationCode("86", userPhone.getText().toString(), userPhoneVertifyCode.getText().toString());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_TAKEPHOTO:
                    takePhoto.setVisibility(View.GONE);
                    userVertifyPicture.setVisibility(View.VISIBLE);
                    Glide.with(this).load(picFile).into(userVertifyPicture);
                    upload(filePath);
                    break;
            }
        }
    }


    public void upload(String path) {
        OkGo.post(AppConfig.BASE_URL + "file/upload")//
                .tag(path)//
                .headers("X-Auth-Token", userInfoBean.getToken())
                .params("gruop", "user")
                .params("file", new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("callbackString", s);
                        JSONObject jsonObject = JSON.parseObject(s);
                        uri = jsonObject.getString("uri");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (e instanceof MalformedJsonException) {
                            toast("照片上传失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            toast("照片上传失败");
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        Log.e("progress", currentSize + ">>>" + totalSize + ">>>" + progress + ">>>" + networkSpeed);
                        mListener.update((int) (progress * 100));
                    }
                });
    }

    private boolean isMobileNO(String phone) {
       /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phone)) return false;
        else return phone.matches(telRegex);
    }


    private void changeBtnGetCode() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (tag) {
                    while (i > 0) {
                        i--;
                        //如果活动为空
                        if (UserProfileVertifyActivity.this == null) {
                            break;
                        }
                        UserProfileVertifyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendPhoneVertifyCode.setBackgroundColor(ContextCompat.getColor(UserProfileVertifyActivity.this, R.color.grey400));
                                sendPhoneVertifyCode.setTextColor(ContextCompat.getColor(UserProfileVertifyActivity.this, R.color.grey50));
                                sendPhoneVertifyCode.setText(i + "S");
                                sendPhoneVertifyCode.setEnabled(false);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    tag = false;
                }
                i = 60;
                tag = true;
                if (UserProfileVertifyActivity.this != null) {
                    UserProfileVertifyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendPhoneVertifyCode.setBackgroundColor(ContextCompat.getColor(UserProfileVertifyActivity.this, R.color.cyan800));
                            sendPhoneVertifyCode.setTextColor(ContextCompat.getColor(UserProfileVertifyActivity.this, R.color.white));
                            sendPhoneVertifyCode.setText("发送验证码");
                            sendPhoneVertifyCode.setEnabled(true);
                        }
                    });
                }
            }
        };
        thread.start();
    }


    public void showQuestionDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_question_dialog, null);
        final TextView content = (TextView) dialogView.findViewById(R.id.content);
        content.setText("本平台实名认证是用户必需进行的一项工作，它关乎到您是否可以发布新信息、评论等诸多功能，所以请您认真对待，填写相关真实信息，请勿作假，一经查实，用户将被冻结并追究相关责任。");
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


    public void showDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_file_dialog, null);
        TextView viewIt = (TextView) dialogView.findViewById(R.id.viewIt);
        viewIt.setText("替换图片");
        TextView deleteIt = (TextView) dialogView.findViewById(R.id.deleteIt);
        deleteIt.setVisibility(View.GONE);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent);
        dialog.setCancelable(false);
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
        viewIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                picFile = new File(AndroidCameraUtil.dir, "schoolhelper_user_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg");
                filePath = picFile.getPath();
                cameraUtil.takePhoto(picFile);
            }
        });
    }


    public void userVertify() {
        String truename = userTruename.getText().toString();
        String studentId = userStudentId.getText().toString();
        String email = userEmail.getText().toString();
        String mobile = userPhone.getText().toString();
        if (truename.equals("")) {
            toast("真实姓名为空,请输入真实姓名后再试");
            return;
        }
        if (studentId.equals("")) {
            toast("学号为空,请输入学号后再试");
            return;
        }
        if (email.equals("")) {
            toast("邮箱为空,请输入邮箱后再试");
            return;
        } else {
            if (!email.matches("^([a-z0-9A-Z]+[_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
                toast("邮箱格式有误,请确认后再试");
                return;
            }
        }
        if (uri == null||uri.equals("")) {
            toast("请上传图片");
        }
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().userVertify(userInfoBean.getToken(), truename, studentId, mobile, email, uri)
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
                            toast("提交信息出错");
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
                        toast("提交验证信息成功，即将审核");
                        finish();
                    }
                });
    }
}
