package com.burning.smile.schoolhelper.register;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/8.
 */
public class RegisteActivity extends BaseActivity implements RegisterContract.View {

    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.userName)
    MaterialEditText userName;
    @BindView(R.id.userNickname)
    MaterialEditText userNickname;
    @BindView(R.id.userEmail)
    MaterialEditText userEmail;
    @BindView(R.id.userPass)
    MaterialEditText userPass;
    @BindView(R.id.confirmUserPass)
    MaterialEditText confirmUserPass;
    @BindView(R.id.register)
    Button register;

    private RegisterContract.Presenter mPresenter;
    private String filePath;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        new RegisterPresenter(this).start();
        //  userEmail.addValidator(new RegexpValidator("邮箱格式有误","^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"));

        userName.addValidator(new METValidator("账号应为4-13位的以数字或字母开头的数字字母下划线组合") {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return text.toString().matches("^[a-zA-Z0-9][a-zA-Z0-9_]{3,12}$") && !isEmpty;
            }
        });
        userEmail.addValidator(new METValidator("邮箱格式有误") {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return text.toString().matches("^([a-z0-9A-Z]+[_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$") || isEmpty;
            }
        });
        userPass.addValidator(new METValidator("密码格式为8-16位的两种或以上数字字母以及符号组合") {
            @Override
            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
                return text.toString().matches("^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,16}$") && !isEmpty;
            }
        });
        userPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmUserPass.clearValidators();
                confirmUserPass.addValidator(new RegexpValidator("密码不一致", s.toString()));
                if (!confirmUserPass.getText().toString().equals(""))
                    confirmUserPass.validate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.register_act;
    }

    @Override
    public void showAvatarDialog() {
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
                filePath = mPresenter.takePhoto();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mPresenter.choosePicture();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showAvatar() {
        Glide.with(this).load(new File(filePath)).error(R.mipmap.ic_test_avatart).crossFade().into(userAvatar);
    }

    @Override
    public void showUserNameEmptyError() {
        toast("用户名为空");
    }

    @Override
    public void showUserPasswordEmptyError() {
        toast("密码为空");
    }

    @Override
    public void showLoadingView() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
    }

    @Override
    public void dimissLoadingView() {
        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
    }

    @Override
    public void showRegisterSuccess(String msg) {
        toast(msg);
        Intent intent = new Intent();
        intent.putExtra(AppConfig.USER_NAME, userName.getText().toString());
        intent.putExtra(AppConfig.USER_PASS, confirmUserPass.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showRegisterFialure(String msg) {
        toast(msg);
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @OnClick({R.id.userAvatar, R.id.register, R.id.backLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userAvatar:
                showAvatarDialog();
                break;
            case R.id.register:
                if (!userName.validate()) {
                    return;
                }
                if (!userEmail.validate()) {
                    showRegisterFialure("邮箱格式有误，请确认");
                    return;
                }
                if (!userPass.validate()) {
                    return;
                }
                if (!confirmUserPass.validate()) {
                    showRegisterFialure("密码不一致，请确认");
                    return;
                }

                mPresenter.register(userName.getText().toString(), userNickname.getText().toString(), userEmail.getText().toString(), confirmUserPass.getText().toString());
                break;
            case R.id.backLL:
                super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_TAKEPHOTO:
                    showAvatar();
                    break;
                case AndroidCameraUtil.CAMERA_CHOOSEPHOTO:
                    filePath = getFilePathFromAlbum(data);
                    showAvatar();
                    break;

            }
        }
    }

    public String getFilePathFromAlbum(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor query = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        assert query != null;
        query.moveToFirst();
        int columnIndex = query.getColumnIndex(filePathColumns[0]);
        String picturePath = query.getString(columnIndex);
        query.close();
        return picturePath;
    }
}
