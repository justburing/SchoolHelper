package com.burning.smile.schoolhelper.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.mycollection.MyCollectionActivity;
import com.burning.smile.schoolhelper.myconversation.ConversationListActivity;
import com.burning.smile.schoolhelper.mydownload.MyDownloadActivity;
import com.burning.smile.schoolhelper.myexpress.MyExpressActivity;
import com.burning.smile.schoolhelper.myfunk.MyFunkActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/15.
 */
public class MineActivity extends BaseActivity {


    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.userNickname)
    TextView userNickname;
    @BindView(R.id.userCoin)
    TextView userCoin;
    @BindView(R.id.recharge)
    Button recharge;
    @BindView(R.id.withDraw)
    Button withDraw;
    @BindView(R.id.follwNum)
    TextView follwNum;
    @BindView(R.id.fansNum)
    TextView fansNum;
    @BindView(R.id.creditNum)
    TextView creditNum;
    @BindView(R.id.creditBar)
    ProgressBar creditBar;
    @BindView(R.id.tv_1)
    TextView tv_1;
    @BindView(R.id.tv_2)
    TextView tv_2;
    @BindView(R.id.tv_3)
    TextView tv_3;
    @BindView(R.id.tv_4)
    TextView tv_4;
    @BindView(R.id.tv_5)
    TextView tv_5;
    @BindView(R.id.expressTotal)
    TextView expressTotal;
    @BindView(R.id.expressLL)
    LinearLayout expressLL;
    @BindView(R.id.funkTotal)
    TextView funkTotal;
    @BindView(R.id.funkLL)
    LinearLayout funkLL;
    @BindView(R.id.forumTotal)
    TextView forumTotal;
    @BindView(R.id.forumLL)
    LinearLayout forumLL;
    @BindView(R.id.collectionLL)
    LinearLayout collectionLL;
    @BindView(R.id.downloadLL)
    LinearLayout downloadLL;
    @BindView(R.id.messageLL)
    LinearLayout messageLL;
    @BindView(R.id.creditLL)
    LinearLayout creditLL;

    private UserInfoBean mUserbean;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences(AppConfig.USERINFO, Activity.MODE_PRIVATE);
        mUserbean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        userCoin.setText(mUserbean.getUser().getCoin() + " 流币");
        userNickname.setText(mUserbean.getUser().getNickname());
        creditNum.setText(mUserbean.getUser().getCredit());
        int credit = Integer.valueOf(mUserbean.getUser().getCredit());
        if(credit<=200){
            creditNum.setTextColor(ContextCompat.getColor(this,R.color.red500));
            tv_1.setTextColor(ContextCompat.getColor(this,R.color.orange400));
        }else if(credit>200&&credit<=400){
            creditNum.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_1.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_2.setTextColor(ContextCompat.getColor(this,R.color.orange400));
        }else if(credit>400&&credit <=600){
            creditNum.setTextColor(ContextCompat.getColor(this,R.color.cyan500));
            tv_1.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_2.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_3.setTextColor(ContextCompat.getColor(this,R.color.orange400));
        }else if(credit>600&&credit <=800){
            creditNum.setTextColor(ContextCompat.getColor(this,R.color.green200));
            tv_1.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_2.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_3.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_4.setTextColor(ContextCompat.getColor(this,R.color.orange400));
        }else if(credit>800){
            creditNum.setTextColor(ContextCompat.getColor(this,R.color.green500));
            tv_1.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_2.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_3.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_4.setTextColor(ContextCompat.getColor(this,R.color.orange400));
            tv_5.setTextColor(ContextCompat.getColor(this,R.color.orange400));
        }
        creditBar.setProgress(credit);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(mSharedPreferences.getString(AppConfig.USER_AVATAR, "")).error(R.mipmap.ic_test_avatart).into(userAvatar);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_act;
    }

    @OnClick({R.id.profileLL, R.id.backLL, R.id.creditLL,R.id.expressLL, R.id.funkLL, R.id.forumLL, R.id.collectionLL, R.id.downloadLL, R.id.messageLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.creditLL:
                startActivity(new Intent(MineActivity.this,CreditShowActivity.class)
                .putExtra("credit",Integer.valueOf(mUserbean.getUser().getCredit())));
                break;
            case R.id.profileLL:
                startActivity(new Intent(MineActivity.this, MineProfileActivity.class));
                break;
            case R.id.expressLL:
                startActivity(new Intent(MineActivity.this, MyExpressActivity.class));

                break;
            case R.id.funkLL:
                startActivity(new Intent(MineActivity.this, MyFunkActivity.class));
                break;
            case R.id.forumLL:
                break;
            case R.id.collectionLL:
                startActivity(new Intent(MineActivity.this, MyCollectionActivity.class));
                break;
            case R.id.downloadLL:
                startActivity(new Intent(MineActivity.this, MyDownloadActivity.class));
                break;
            case R.id.messageLL:
                startActivity(new Intent(MineActivity.this, ConversationListActivity.class));
                break;
        }
    }
}
