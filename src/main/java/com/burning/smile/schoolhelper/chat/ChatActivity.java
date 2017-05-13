package com.burning.smile.schoolhelper.chat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidCameraUtil;
import com.burning.smile.androidtools.widget.record.RecordButton;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.chat.adapter.MessageChatAdapter;
import com.burning.smile.schoolhelper.util.chat.ChatEmoticonsKeyBoard;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;
import com.burning.smile.schoolhelper.util.chat.MoreFuctionView;
import com.burning.smile.schoolhelper.util.chat.VoiceView;
import com.burning.smile.schoolhelper.util.emoij.Constants;
import com.sj.emoji.EmojiBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by smile on 2017/5/4.
 */
public class ChatActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.lv_chat)
    ListView lvChat;
    @BindView(R.id.ek_bar)
    ChatEmoticonsKeyBoard ekBar;

    private VoiceView voiceView;
    private MoreFuctionView moreFuctionView;
    private MessageChatAdapter messageChatAdapter;
    private Conversation conversation;
    private String userName;
    private AndroidCameraUtil cameraUtil;
    private String filePath;
    private File takePhotoFile;


    @Override
    protected void init() {
        ButterKnife.bind(this);
        cameraUtil = AndroidCameraUtil.getInstance(this);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initEmoticonsKeyBoardBar();
        userName = getIntent().getStringExtra("userName");
        // JMessageClient.enterSingleConversation(userName);
        conversation = JMessageClient.getSingleConversation(userName);
        if (conversation != null) {
            messageChatAdapter = new MessageChatAdapter(this, conversation.getAllMessage());
            lvChat.setAdapter(messageChatAdapter);
        } else {
            conversation = Conversation.createSingleConversation(userName);
            List<Message> messages = conversation.getAllMessage();
            if (messages != null) {
                messageChatAdapter = new MessageChatAdapter(this, messages);
            } else {
                messageChatAdapter = new MessageChatAdapter(this, new ArrayList<Message>());
            }
            lvChat.setAdapter(messageChatAdapter);
        }
        JMessageClient.registerEventReceiver(this);
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

//    public void onEvent(NotificationClickEvent event){
//        Message message = event.getMessage();
//        startActivity
//                (new Intent(this,ChatActivity.class).putExtra("userName",message.getFromUser().getNickname())
//        );
//    }

    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        messageChatAdapter.notifyDataSetChanged();
        switch (msg.getContentType()) {
            case text:
                //处理文字消息
                TextContent textContent = (TextContent) msg.getContent();
                textContent.getText();
                messageChatAdapter.add(msg);
                scrollToBottom();
                break;
            case image:
                //处理图片消息
                ImageContent imageContent = (ImageContent) msg.getContent();
                imageContent.getLocalPath();//图片本地地址
                imageContent.getLocalThumbnailPath();//图片对应缩略图的本地地址
                messageChatAdapter.add(msg);
                scrollToBottom();
                break;
            case voice:
                //处理语音消息
                VoiceContent voiceContent = (VoiceContent) msg.getContent();
                voiceContent.getLocalPath();//语音文件本地地址
                voiceContent.getDuration();//语音文件时长
                messageChatAdapter.add(msg);
                scrollToBottom();
                break;
            case custom:
                //处理自定义消息
                CustomContent customContent = (CustomContent) msg.getContent();
                customContent.getNumberValue("custom_num"); //获取自定义的值
                customContent.getBooleanValue("custom_boolean");
                customContent.getStringValue("custom_string");
                messageChatAdapter.add(msg);
                break;
            case eventNotification:
                //处理事件提醒消息
                EventNotificationContent eventNotificationContent = (EventNotificationContent) msg.getContent();
                switch (eventNotificationContent.getEventNotificationType()) {
                    case group_member_added:
                        //群成员加群事件
                        break;
                    case group_member_removed:
                        //群成员被踢事件
                        break;
                    case group_member_exit:
                        //群成员退群事件
                        break;
                }
                break;
        }
    }

    private void initEmoticonsKeyBoardBar() {
        voiceView = new VoiceView(this);
        moreFuctionView = new MoreFuctionView(this);
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);

        ekBar.addFuncView(ChatEmoticonsKeyBoard.FUNC_TYPE_PTT, voiceView);
        ekBar.addFuncView(ChatEmoticonsKeyBoard.FUNC_TYPE_PLUG, moreFuctionView);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Message message = JMessageClient.createSingleTextMessage(userName, ekBar.getEtChat().getText().toString());
                messageChatAdapter.add(message);
                scrollToBottom();
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        messageChatAdapter.notifyDataSetChanged();
                        Log.e("error", s);
                        if (i == 0) {

                        } else {

                        }
                    }
                });
                JMessageClient.sendMessage(message);
                ekBar.getEtChat().setText("");
            }
        });
        voiceView.getRecordBtn().setAudioFinishRecorderListener(new RecordButton.AudioFinishRecorderListener() {
            @Override
            public void play() {

            }

            @Override
            public void onFinish(float seconds, String filePath) {
                ekBar.reset();
                try {
                    final Message message = JMessageClient.createSingleVoiceMessage(userName, new File(filePath), (int) seconds);
                    messageChatAdapter.add(message);
                    scrollToBottom();
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            messageChatAdapter.notifyDataSetChanged();
                            Log.e("error", s);
                            if (i == 0) {

                            } else {

                            }
                        }
                    });
                    JMessageClient.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ekBar.getBtnImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtil.choosePhoto();
            }
        });
        ekBar.getBtnCamera().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFile = new File(AndroidCameraUtil.dir, "schoolhelper_" + System.currentTimeMillis() + ".jpg");
                filePath = takePhotoFile.getPath();
                cameraUtil.takePhoto(takePhotoFile);
            }
        });
        moreFuctionView.getFileView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("文件");
            }
        });
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                ChatUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        //OnSendImage(((EmoticonEntity)o).getIconUri());
                    }
                } else {
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.chat_act;
    }

    private void scrollToBottom() {
        lvChat.requestLayout();
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                lvChat.setSelection(lvChat.getBottom());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AndroidCameraUtil.CAMERA_TAKEPHOTO:
                    try {
                        final Message imageMessage = JMessageClient.createSingleImageMessage(userName, takePhotoFile);
                        messageChatAdapter.add(imageMessage);
                        scrollToBottom();
                        imageMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                messageChatAdapter.notifyDataSetChanged();
                                Log.e("info", s);
                                if (i == 0) {

                                } else {

                                }
                            }
                        });
                        JMessageClient.sendMessage(imageMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case AndroidCameraUtil.CAMERA_CHOOSEPHOTO:
                    filePath = getFilePathFromAlbum(data);
                    try {
                        final Message imageMessage = JMessageClient.createSingleImageMessage(userName, new File(filePath));
                        messageChatAdapter.add(imageMessage);
                        scrollToBottom();
                        imageMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                messageChatAdapter.notifyDataSetChanged();
                                Log.e("info", s);
                                if (i == 0) {

                                } else {

                                }
                            }
                        });
                        JMessageClient.sendMessage(imageMessage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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


    @Override
    public void OnFuncPop(int i) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {

    }
}
