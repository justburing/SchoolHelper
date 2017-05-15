package com.burning.smile.schoolhelper.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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
import com.burning.smile.androidtools.bean.UploadFile;
import com.burning.smile.androidtools.tools.AndroidBitmapUtil;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/4/26.
 */
public class MineProfileActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.userAvatarLL)
    LinearLayout userAvatarLL;
    @BindView(R.id.userAccount)
    TextView userAccount;
    @BindView(R.id.userNickname)
    TextView userNickname;
    @BindView(R.id.userNicknameLL)
    LinearLayout userNicknameLL;
    @BindView(R.id.userBirthday)
    EditText userBirthday;
    @BindView(R.id.userBirthdayLL)
    LinearLayout userBirthdayLL;
    @BindView(R.id.userEmial)
    EditText userEmial;
    @BindView(R.id.userEmialLL)
    LinearLayout userEmialLL;
    @BindView(R.id.userQQ)
    EditText userQQ;
    @BindView(R.id.userQQLL)
    LinearLayout userQQLL;
    @BindView(R.id.userPhone)
    TextView userPhone;


    private AndroidCameraUtil mCameraUtil;
    private String filePath;
    private File takePhotoFile;
    private UploadFile uploadFile;
    private SharedPreferences mSharedPreferences;
    private UserInfoBean userInfoBean;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        toolbarTitle.setText("个人信息");
        mCameraUtil = AndroidCameraUtil.getInstance(this);
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        mSharedPreferences = getSharedPreferences(AppConfig.USERINFO, Activity.MODE_PRIVATE);
        Glide.with(this).load(mSharedPreferences.getString(AppConfig.USER_AVATAR, "")).error(R.mipmap.ic_test_avatart).crossFade().into(userAvatar);
        userAccount.setText(userInfoBean.getUser().getUsername());
        userNickname.setText(userInfoBean.getUser().getNickname());
        String userPhoneString = userInfoBean.getUser().getMobile();
        if (userPhoneString.equals("")) {
            userPhone.setHint("请输入手机号码");
        } else {
            userPhoneString = userPhoneString.replace(userPhoneString.substring(3, 8), "*****");
        }
        userPhone.setText(userPhoneString);
        userBirthday.setOnKeyListener(null);
        userEmial.setOnKeyListener(null);
        userQQ.setOnKeyListener(null);
        userPhone.setOnKeyListener(null);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.mine_profile_act;
    }


    @OnClick({R.id.backLL, R.id.userAvatarLL, R.id.userNicknameLL, R.id.userBirthdayLL, R.id.userEmialLL, R.id.userQQLL, R.id.userPhoneLL,R.id.userPayPasswordLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.userAvatarLL:
                showMofifyAvatarDialog();
                break;
            case R.id.userNicknameLL:
                showMofifyNicknameDialog();
                break;
            case R.id.userBirthdayLL:
                showMofifyBirthdayDialog();
                break;
            case R.id.userEmialLL:
                showMofifyEmialDialog();
                break;
            case R.id.userQQLL:
                showMofifyQQDialog();
                break;
            case R.id.userPhoneLL:
                showMofifyMobileDialog();
                break;
            case R.id.userPayPasswordLL:
                startActivity(new Intent(MineProfileActivity.this,ModifyPayPassActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_TAKEPHOTO:
                    byte[] bytes = AndroidBitmapUtil.compressImage(AndroidBitmapUtil.decodeSampledBitmapFromFile(filePath, 600, 800), 500);
                    uploadFile = new UploadFile();
                    uploadFile.setmFile(bytes);
                    uploadFile.setmName(filePath);
                    uploadFile.setmType("avatar");
                    modifyAvatar();
                    // showAvatar();
                    break;
                case AndroidCameraUtil.CAMERA_CHOOSEPHOTO:
                    filePath = mCameraUtil.getFilePathFromAlbum(data);
                    byte[] bytes1 = AndroidBitmapUtil.compressImage(AndroidBitmapUtil.decodeSampledBitmapFromFile(filePath, 600, 800), 500);
                    uploadFile = new UploadFile();
                    uploadFile.setmFile(bytes1);
                    uploadFile.setmName(filePath);
                    uploadFile.setmType("avatar");
                    modifyAvatar();
                    //showAvatar();
                    break;

            }
        }
    }

    public void showAvatar() {
        Glide.with(this).load(new File(filePath)).error(R.mipmap.ic_test_avatart).crossFade().into(userAvatar);
        JMessageClient.updateUserAvatar(new File(filePath), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    toast("用户头像修改成功");
                } else {
                    toast("用户头像已修改成功");
                }
            }
        });
    }

    public void showMofifyAvatarDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_dialog_avatar, null);
        final TextView takePhoto = (TextView) dialogView.findViewById(R.id.takePhoto);
        final TextView picture = (TextView) dialogView.findViewById(R.id.picture);
        final TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent);
        dialog.setCancelable(true);
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
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                filePath = takePhoto();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                choosePicture();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showMofifyNicknameDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_profile, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        title.setText("修改昵称");
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        content.setHint(userNickname.getText().toString());
        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(getScreenWidth(),
                getScreenHeight() / 3);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!content.getText().toString().equals("")) {
                    userNickname.setText(content.getText().toString());
                    JMessageClient.getUserInfo(AppConfig.JMESSAGE_PREIX + userInfoBean.getUser().getId(), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                userInfo.setNickname(content.getText().toString());
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            Log.e("yes", "yes");
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    toast("输入的昵称为空，无法进行修改");
                }
            }
        });
    }


    public void showMofifyBirthdayDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_profile, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        title.setText("个人生日");
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        if (userBirthday.getText().toString().equals("")) {
            content.setHint("请输入生日");
        } else {
            content.setHint(userBirthday.getText().toString());
        }
        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(getScreenWidth(),
                getScreenHeight() / 3);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!content.getText().toString().equals("")) {
                    userBirthday.setText(content.getText());
                } else {
                    toast("输入的生日为空，无法进行修改");
                }
            }
        });
    }

    public void showMofifyEmialDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_profile, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        title.setText("个人邮箱");
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        if (userEmial.getText().toString().equals("")) {
            content.setHint("请输入邮箱");
        } else {
            content.setHint(userEmial.getText().toString());
        }
        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(getScreenWidth(),
                getScreenHeight() / 3);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!content.getText().toString().equals("")) {
                    userEmial.setText(content.getText());
                } else {
                    toast("输入的邮箱为空，无法进行修改");
                }
            }
        });
    }

    public void showMofifyQQDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_profile, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        title.setText("个人QQ");
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        if (userQQ.getText().toString().equals("")) {
            content.setHint("请输入QQ");
        } else {
            content.setHint(userQQ.getText().toString());
        }
        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(getScreenWidth(),
                getScreenHeight() / 3);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!content.getText().toString().equals("")) {
                    userQQ.setText(content.getText());
                } else {
                    toast("输入的QQ为空，无法进行修改");
                }
            }
        });
    }

    public void showMofifyMobileDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modify_profile, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        title.setText("个人QQ");
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        if (userPhone.getText().toString().equals("")) {
            content.setHint("请输入手机号码");
        } else {
            content.setHint(userPhone.getText().toString());
        }

        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(getScreenWidth(),
                getScreenHeight() / 3);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setWindowAnimations(R.style.BottomToTop);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!content.getText().toString().equals("")) {
                    userPhone.setText(content.getText());
                } else {
                    toast("输入的手机号码为空，无法进行修改");
                }
            }
        });
    }

    public String takePhoto() {
        takePhotoFile = new File(AndroidCameraUtil.dir, "schoolhelper_" + System.currentTimeMillis() + ".jpg");
        filePath = takePhotoFile.getPath();
        mCameraUtil.takePhoto(takePhotoFile);
        return filePath;
    }

    public void choosePicture() {
        mCameraUtil.choosePhoto();
    }


    public void modifyAvatar() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile.getmFile());
        //addFormDataPart(uploadFiles.get(i).getmType(), uploadFiles.get(i).getmName(), imageBody);//第一个参数是后台接收图片流的参数名
//        .addFormDataPart("name", name)
//                .addFormDataPart("name", psd)
//                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
        //构建body
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(uploadFile.getmType(), uploadFile.getmName(), imageBody)//第一个参数是后台接收图片流的参数名
                .build();
        RetrofitUtil.getRetrofitApiInstance().modifyUserAvatar(userInfoBean.getToken(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean.UserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("修改头像失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            toast(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(UserInfoBean.UserBean userBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        showAvatar();
                        mSharedPreferences.edit().putString(AppConfig.USER_AVATAR, userBean.getAvatar().toString()).apply();

                    }
                });
    }
}
