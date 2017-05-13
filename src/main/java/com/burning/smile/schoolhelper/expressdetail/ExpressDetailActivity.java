package com.burning.smile.schoolhelper.expressdetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.chat.ChatActivity;
import com.burning.smile.schoolhelper.data.ExpressListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/15.
 */
public class ExpressDetailActivity extends BaseActivity implements ExpressDetailContract.View {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.publisherAvatar)
    CircleImageView publisherAvatar;
    @BindView(R.id.publisherNickname)
    TextView publisherNickname;
    @BindView(R.id.expressTime)
    TextView expressTime;
    @BindView(R.id.expressTitle)
    TextView expressTitle;
    @BindView(R.id.expressContent)
    TextView expressContent;
    @BindView(R.id.follow)
    Button follow;
    @BindView(R.id.chatIcon)
    ImageView chatIcon;
    @BindView(R.id.chatText)
    TextView chatText;
    @BindView(R.id.chatLL)
    LinearLayout chatLL;
    @BindView(R.id.collectIcon)
    ImageView collectIcon;
    @BindView(R.id.collectText)
    TextView collectText;
    @BindView(R.id.collectLL)
    LinearLayout collectLL;
    @BindView(R.id.dealIcon)
    ImageView dealIcon;
    @BindView(R.id.dealText)
    TextView dealText;
    @BindView(R.id.dealLL)
    LinearLayout dealLL;

    private String expressId;
    private ExpressDetailContract.Presenter mPresenter;
    private String mPublisherId;
    private UserInfoBean userInfoBean;
    private Map map;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        expressId = getIntent().getStringExtra("expressId");
        map = AndroidFileUtil.getObject(this, AppConfig.COLLECTION_EXPRESS);
        if (map != null) {
            if (map.get(AppConfig.COLLECTION_EXPRESS + expressId) != null && map.get(AppConfig.COLLECTION_EXPRESS + expressId).toString().equals(expressId)) {
                collectIcon.setImageResource(R.mipmap.ic_collected);
                collectText.setText("已收藏");
            }
        }
        new ExpressDetailPresenter(this).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.express_detail_act;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        expressId = savedInstanceState.getString("expressId");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("expressId", expressId);
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
    public void loadDataSuccess(String msg, ExpressListBean.Express express) {
        Glide.with(this).load(express.getPublisher().getAvatar()).error(R.mipmap.ic_test_avatart).into(publisherAvatar);
        publisherNickname.setText(express.getPublisher().getNickname());
        mPublisherId = express.getPublisher().getId();
        expressTime.setText(express.getCreated_time());
        expressTitle.setText(express.getTitle());
        expressContent.setText(express.getDetail());
        //status 1:待代领 2：被代领 3：已送达 4：已确认
        if (express.getStatus().equals("2")) {
            dealText.setText("您已接单");
            dealText.setTextColor(ContextCompat.getColor(this, R.color.red));
            dealIcon.setImageResource(R.mipmap.ic_express_2);
            dealLL.setEnabled(false);
        } else if (express.getStatus().equals("3")) {
            dealText.setText("该订单在进行");
            dealText.setTextColor(ContextCompat.getColor(this, R.color.blue500));
            dealIcon.setImageResource(R.mipmap.ic_run);
            dealLL.setEnabled(false);
        } else if (express.getStatus().equals("4")) {
            dealText.setText("该订单已完成");
            dealText.setTextColor(ContextCompat.getColor(this, R.color.green400));
            dealIcon.setImageResource(R.mipmap.ic_correct);
            dealLL.setEnabled(false);
        }

    }

    @Override
    public void loadDataFailure(String msg) {
        toast(msg);
    }

    @Override
    public void dealExpressSuccess(String msg) {
        toast(msg);
        dealText.setText("您已接单");
        dealText.setTextColor(ContextCompat.getColor(this, R.color.red));
        dealIcon.setImageResource(R.mipmap.ic_express_2);
        dealLL.setEnabled(false);
    }

    @Override
    public void dealExpressFailure(String msg) {
        toast(msg);
    }

    @Override
    public String getExpressId() {
        return expressId;
    }

    @Override
    public void collectSuccessed(String msg) {
        toast(msg);
        collectIcon.setImageResource(R.mipmap.ic_collected);
        collectText.setText("已收藏");
        if (map == null) {
            map = new HashMap();
            map.put(AppConfig.COLLECTION_EXPRESS + expressId, expressId);
        } else {
            map.put(AppConfig.COLLECTION_EXPRESS + expressId, expressId);
        }
        AndroidFileUtil.saveObject(this, map, AppConfig.COLLECTION_EXPRESS);
    }

    @Override
    public void cancelCollectFailure(String msg) {
        toast(msg);
    }

    @Override
    public void cancelCollectSuccessed(String msg) {
        toast(msg);
        collectIcon.setImageResource(R.mipmap.ic_collect_1);
        collectText.setText("收藏");
        if (map != null)
            map.remove(AppConfig.COLLECTION_EXPRESS + expressId);
        AndroidFileUtil.saveObject(this, map, AppConfig.COLLECTION_EXPRESS);
    }

    @Override
    public void collectFailure(String msg) {
        toast(msg);
    }

    @Override
    public void setPresenter(ExpressDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @OnClick({R.id.backLL, R.id.follow, R.id.chatLL, R.id.collectLL, R.id.dealLL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.follow:
                if (follow.getText().equals("+关注")) {
                    follow.setBackgroundResource(R.drawable.rect_followed_btn);
                    follow.setText("已关注");
                    follow.setTextColor(ContextCompat.getColor(this, R.color.white));
                } else {
                    follow.setBackgroundResource(R.drawable.rect_follow_btn);
                    follow.setText("+关注");
                    follow.setTextColor(Color.parseColor("#FF7F24"));
                }
                break;
            case R.id.chatLL:
                if (userInfoBean.getUser().getId().equals(mPublisherId)) {
                    toast("不能与自己进行聊天");
                } else {
                    startActivity(new Intent(ExpressDetailActivity.this, ChatActivity.class)
                            .putExtra("userName", AppConfig.JMESSAGE_PREIX + mPublisherId));
                }
                break;
            case R.id.collectLL:
                if (collectText.getText().toString().equals("收藏")) {
                    mPresenter.collect(expressId);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("是否取消收藏")
                            .setNegativeButton("我再考虑下", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("取消收藏", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mPresenter.cancelCollect(expressId);
                                }
                            }).create();
                    dialog.show();

                }
                break;
            case R.id.dealLL:
                if (userInfoBean.getUser().getId().equals(mPublisherId)) {
                    toast("亲，不能自己为自己接单哦");
                } else {
                    mPresenter.dealExpress(expressId);
                }
                break;
        }
    }
}
