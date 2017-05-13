package com.burning.smile.schoolhelper.myconversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidCommon;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.chat.ChatActivity;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by smile on 2017/5/6.
 */
public class ConversationListActivity extends BaseActivity {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.lv_conersation)
    SwipeMenuListView lvConersation;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;

    private List<Conversation> conversations;
    private ListViewAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        conversations = JMessageClient.getConversationList();
        if (conversations != null && conversations.size() > 0) {
            mAdapter.notifyDataSetChanged();
            noDataView.setVisibility(View.GONE);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new ListViewAdapter(this);
        lvConersation.setAdapter(mAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 创建一个Item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // 设置背景
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.red)));
                // 设置宽度
                deleteItem.setWidth(AndroidCommon.dip2px(getApplicationContext(), 90));
                // 设置一个图标
                deleteItem.setIcon(R.mipmap.ic_item_delete);
                // 添加到菜单
                menu.addMenuItem(deleteItem);
                // 创建一个Item
                SwipeMenuItem topItem = new SwipeMenuItem(
                        getApplicationContext());
                // 设置背景
                topItem.setBackground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.orange400)));
                // 设置宽度
                topItem.setWidth(AndroidCommon.dip2px(getApplicationContext(), 90));
                // 设置一个图标
                topItem.setIcon(R.mipmap.ic_top);
                // 添加到菜单
                menu.addMenuItem(topItem);
            }
        };

        lvConersation.setMenuCreator(creator);
        // Close Interpolator
        lvConersation.setCloseInterpolator(new BounceInterpolator());
        // Open Interpolator
        lvConersation.setOpenInterpolator(new BounceInterpolator());
        lvConersation.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    if (JMessageClient.deleteSingleConversation(((UserInfo) conversations.get(position).getTargetInfo()).getUserName())) {
                        conversations.remove(position);
                        mAdapter.notifyDataSetChanged();
                        if (conversations.size() == 0) {
                            noDataView.setVisibility(View.VISIBLE);
                        }
                    }
                    ;
                } else if (index == 1) {
                    Conversation conversation = conversations.get(position);
                    conversations.remove(conversation);
                    conversations.add(0, conversation);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        lvConersation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo user = (UserInfo) conversations.get(position).getTargetInfo();
                startActivity(new Intent(ConversationListActivity.this, ChatActivity.class).putExtra("userName", user.getUserName()));
                conversations.get(position).resetUnreadCount();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.conversation_list_act;
    }


    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }


        @Override
        public int getCount() {
            return conversations == null ? 0 : conversations.size();
        }

        @Override
        public Conversation getItem(int position) {
            return conversations == null ? null : conversations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_lv, null);
                viewHolder.item_userAvatar = (CircleImageView) convertView.findViewById(R.id.item_avatar);
                viewHolder.item_userNickname = (TextView) convertView.findViewById(R.id.item_nickname);
                viewHolder.item_last_msg = (TextView) convertView.findViewById(R.id.item_lastmessage);
                viewHolder.item_unread_msg_num = (TextView) convertView.findViewById(R.id.item_unread_msg_num);
                viewHolder.item_last_time = (TextView) convertView.findViewById(R.id.item_last_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Conversation conversation = conversations.get(position);
            String userName = ((UserInfo) conversation.getTargetInfo()).getUserName();
            JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        Glide.with(mContext).load(userInfo.getAvatarFile()).into(viewHolder.item_userAvatar);
                        String userName = userInfo.getNickname();
                        viewHolder.item_userNickname.setText(userName);
                    } else {
                        viewHolder.item_userAvatar.setImageResource(R.mipmap.ic_test_avatart);
                        viewHolder.item_userNickname.setText("昵称为空");
                    }
                }
            });
            Message message = conversation.getLatestMessage();
            if (message != null) {
                if (message.getContentType() == ContentType.text) {
                    TextContent textContent = (TextContent) message.getContent();
                    ChatUtils.spannableEmoticonFilter(viewHolder.item_last_msg, textContent.getText());
                } else if (message.getContentType() == ContentType.voice) {
                    VoiceContent voiceContent = (VoiceContent) message.getContent();
                    viewHolder.item_last_msg.setText("[语音]" + voiceContent.getDuration() + "\"");
                } else if (message.getContentType() == ContentType.image) {
                    ImageContent imageContent = (ImageContent) message.getContent();
                    viewHolder.item_last_msg.setText("[图片]");
                } else {
                    viewHolder.item_last_msg.setText("");
                }
                viewHolder.item_last_time.setText(new SimpleDateFormat("MM-dd HH:mm").format(message.getCreateTime()));
            }
            if (conversation.getUnReadMsgCnt() == 0) {
                viewHolder.item_unread_msg_num.setVisibility(View.GONE);
            } else {

                viewHolder.item_unread_msg_num.setVisibility(View.VISIBLE);
                viewHolder.item_unread_msg_num.setText(conversation.getUnReadMsgCnt() + "");
            }
            return convertView;
        }

        private class ViewHolder {
            private CircleImageView item_userAvatar;
            private TextView item_userNickname;
            private TextView item_last_msg;
            private TextView item_unread_msg_num;
            private TextView item_last_time;
        }
    }
}
