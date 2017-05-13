package com.burning.smile.schoolhelper.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.login.LoginActivity;
import com.burning.smile.schoolhelper.util.ActivityManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by smile on 2017/3/14.
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.browseSetting)
    LinearLayout browseSetting;
    @BindView(R.id.messageRecomend)
    LinearLayout messageRecomend;
    @BindView(R.id.privacySetting)
    LinearLayout privacySetting;
    @BindView(R.id.supportSetting)
    LinearLayout supportSetting;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.versionMessage)
    LinearLayout versionMessage;
    @BindView(R.id.suggestionSetting)
    LinearLayout suggestionSetting;
    @BindView(R.id.exit)
    Button exit;


    private SharedPreferences mSharedPreferences;

    @Override
    protected void init() {
        ActivityManager.getInstance().addActivity(this);
        ButterKnife.bind(this);
        version.setText("v" + getVersion());
        mSharedPreferences = getSharedPreferences(AppConfig.USERINFO, Activity.MODE_PRIVATE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.setting_act;
    }

    @OnClick({R.id.backLL, R.id.browseSetting, R.id.messageRecomend, R.id.privacySetting, R.id.supportSetting, R.id.versionMessage, R.id.suggestionSetting, R.id.exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.browseSetting:
                break;
            case R.id.messageRecomend:
                break;
            case R.id.privacySetting:
                break;
            case R.id.supportSetting:
                break;
            case R.id.versionMessage:
                break;
            case R.id.suggestionSetting:
                break;
            case R.id.exit:
                // JMessageClient.logout();
                mSharedPreferences.edit().clear().commit();
                ActivityManager.getInstance().finishAll();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "信息获取出错";
        }
    }
}
