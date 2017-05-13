package com.burning.smile.schoolhelper.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.DrawerOpreator;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.express.ExpressFragment;
import com.burning.smile.schoolhelper.forum.ForumFragment;
import com.burning.smile.schoolhelper.funk.FunkFragment;
import com.burning.smile.schoolhelper.mine.MineActivity;
import com.burning.smile.schoolhelper.myconversation.ConversationListActivity;
import com.burning.smile.schoolhelper.setting.SettingActivity;
import com.burning.smile.schoolhelper.util.ActivityManager;
import com.burning.smile.schoolhelper.util.ActivityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/3/9.
 */
public class HomeActivity extends BaseActivity implements DrawerOpreator {
    @BindView(R.id.contentFrame)
    FrameLayout contentFrame;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.userAvatar)
    CircleImageView userAvatar;
    @BindView(R.id.userNickname)
    TextView userNickname;
    @BindView(R.id.userCoin)
    TextView userCoin;
    @BindView(R.id.recharge)
    Button recharge;
    @BindView(R.id.userProfileLL)
    LinearLayout userProfileLL;
    @BindView(R.id.contentLv)
    ListView leftMenuLv;
    @BindView(R.id.setting)
    ImageView setting;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.leftMenu)
    RelativeLayout leftMenu;

    private List<Map<String, Object>> contents;
    private ListViewAdapter mAdapter;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void init() {
        ButterKnife.bind(this);
        ActivityManager.getInstance().addActivity(this);
        JMessageClient.registerEventReceiver(this);
        Log.e("registerId", JPushInterface.getRegistrationID(this));
        Log.e("isLogined", JPushInterface.getConnectionState(this) + "");
        mSharedPreferences = getSharedPreferences(AppConfig.USERINFO, Activity.MODE_PRIVATE);
        setupDrawerContent();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), ExpressFragment.newInstance(), R.id.contentFrame);
        leftMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mAdapter.setSelectedPosition(position);
                        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), ExpressFragment.newInstance(), R.id.contentFrame);
                        break;
                    case 1:
                        mAdapter.setSelectedPosition(position);
                        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), FunkFragment.newInstance(), R.id.contentFrame);
                        break;
                    case 2:
                        mAdapter.setSelectedPosition(position);
                        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), ForumFragment.newInstance(), R.id.contentFrame);
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this, MineActivity.class));
                        break;
                }
                closeDrawer();
            }
        });
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        //  JMessageClient.logout();
        super.onDestroy();
    }

    public void onEvent(NotificationClickEvent event) {
        Message message = event.getMessage();
        startActivity(new Intent(this, ConversationListActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_act;
    }

    public void setupDrawerContent() {
        contents = new ArrayList<>();
        List<String> content = new ArrayList<>();
        content.add("快递代领");
        content.add("旧货交易");
        content.add("论坛");
        content.add("我的");
        List<Integer> icon = new ArrayList<>();
        icon.add(R.mipmap.ic_express);
        icon.add(R.mipmap.ic_funk);
        icon.add(R.mipmap.ic_forum);
        icon.add(R.mipmap.ic_profile);
        contents = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", icon.get(i));
            map.put("name", content.get(i));
            contents.add(map);
        }
        mAdapter = new ListViewAdapter(this);
        leftMenuLv.setAdapter(mAdapter);
        //初始化用户信息
        UserInfoBean userInfo = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        userNickname.setText(userInfo.getUser().getNickname());
        userCoin.setText(userInfo.getUser().getCoin().toString());
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(mSharedPreferences.getString(AppConfig.USER_AVATAR, "")).error(R.mipmap.ic_test_avatart).into(userAvatar);
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(leftMenu);
    }

    @Override
    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean isOpening() {
        return drawerLayout.isDrawerOpen(leftMenu);
    }


    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private int selectedPosition;

        public ListViewAdapter(Context context) {
            mContext = context;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return contents == null ? 0 : contents.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return contents == null ? null : contents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_left_menu_lv, null);
                holder.item = (LinearLayout) convertView.findViewById(R.id.item);
                holder.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
                holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> map = contents.get(position);
            holder.item_icon.setImageResource((Integer) map.get("icon"));
            holder.item_name.setText(map.get("name").toString());
            if (position == selectedPosition) {
                holder.item.setBackgroundColor(R.color.purplea700);
            } else {
                holder.item.setBackgroundColor(android.R.color.transparent);
            }
            return convertView;
        }


        private class ViewHolder {
            private LinearLayout item;
            private ImageView item_icon;
            private TextView item_name;
        }
    }


}
