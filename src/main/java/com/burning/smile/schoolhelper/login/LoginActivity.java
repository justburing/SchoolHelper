package com.burning.smile.schoolhelper.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.home.HomeActivity;
import com.burning.smile.schoolhelper.register.RegisteActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by smile on 2017/3/7.
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.userAvatar)
    CircleImageView mUserAvatar;
    @BindView(R.id.userName)
    MaterialEditText mUserName;
    @BindView(R.id.userPass)
    MaterialEditText mUserPass;
    @BindView(R.id.isRememberPass)
    CheckBox mIsRememberPass;
    @BindView(R.id.isAutoLogin)
    CheckBox mIsAutoLogin;
    @BindView(R.id.login)
    Button mLogin;
    @BindView(R.id.register)
    TextView mRegister;
    @BindView(R.id.forgetPassword)
    TextView mForgetPassword;


    private LoginContract.Presenter mPresenter;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void init() {
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences(AppConfig.USERINFO, Context.MODE_PRIVATE);
        new LoginPresenter(this).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_act;
    }

    @Override
    public void showEmptyAccout() {
        toast("账号为空");
    }

    @Override
    public void showEmptyPassword() {
        toast("密码为空");
    }

    @Override
    public void showLoginFailure(String errorString) {
        toast(errorString);
    }

    @Override
    public void showLoginSuccess(String msg) {
        toast(msg);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
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
    public boolean isRememberPassword() {
        return mIsRememberPass.isChecked();
    }

    @Override
    public boolean isAutoLogin() {
        return mIsAutoLogin.isChecked();
    }

    @Override
    public void fillUserName(String userName) {
        mUserName.setText(userName);
    }

    @Override
    public void fillUserPassword(String userPassword) {
        mUserPass.setText(userPassword);
    }

    @Override
    public void checkIsAutoLogin(boolean isAutoLogin) {
        mIsAutoLogin.setChecked(isAutoLogin);
        if (isAutoLogin) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                mLogin.callOnClick();
            } else {
                mPresenter.login(mUserName.getText().toString(), mUserPass.getText().toString());
            }
        }
    }

    @Override
    public void checkIsRememberPassword(boolean isRememberPassword) {
        mIsRememberPass.setChecked(isRememberPassword);
    }

    @Override
    public void showUserAvatar() {
        String userAvatar = mSharedPreferences.getString(AppConfig.USER_AVATAR, "");
        if (userAvatar != null && !userAvatar.equals("")) {
            Glide.with(this).load(userAvatar).error(R.mipmap.ic_test_avatart).into(mUserAvatar);
        }
    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @OnClick({R.id.login, R.id.register, R.id.forgetPassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                mPresenter.login(mUserName.getText().toString(), mUserPass.getText().toString());
                break;
            case R.id.register:
                startActivityForResult(new Intent(LoginActivity.this, RegisteActivity.class), 100);
                break;
            case R.id.forgetPassword:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    fillUserName(data.getStringExtra(AppConfig.USER_NAME));
                    fillUserPassword(data.getStringExtra(AppConfig.USER_PASS));
                    break;
            }
        }
    }
}
