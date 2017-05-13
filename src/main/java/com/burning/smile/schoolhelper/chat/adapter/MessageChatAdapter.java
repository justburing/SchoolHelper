package com.burning.smile.schoolhelper.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

@SuppressLint("InflateParams")
public class MessageChatAdapter extends BaseListAdapter<Message> {


    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;

    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;

    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;

    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;

    private MediaPlayer player;


    public MessageChatAdapter(Context context, List<Message> msgList) {
        // TODO Auto-generated constructor stub
        super(context, msgList);
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = list.get(position);
        if (msg.getContentType() == ContentType.image) {
            return msg.getDirect().equals(MessageDirect.send) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getContentType() == ContentType.location) {
            return msg.getDirect().equals(MessageDirect.send) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else if (msg.getContentType() == ContentType.voice) {
            return msg.getDirect().equals(MessageDirect.send) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else {
            return msg.getDirect().equals(MessageDirect.send) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    private View createViewByType(Message message, int position) {
        ContentType type = message.getContentType();
        if (type == ContentType.image) {
            return getItemViewType(position) == TYPE_RECEIVER_IMAGE ?
                    mInflater.inflate(R.layout.item_chat_received_image, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_image, null);
        } else if (type == ContentType.location) {
            return getItemViewType(position) == TYPE_RECEIVER_LOCATION ?
                    mInflater.inflate(R.layout.item_chat_received_location, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_location, null);
        } else if (type == ContentType.voice) {
            return getItemViewType(position) == TYPE_RECEIVER_VOICE ?
                    mInflater.inflate(R.layout.item_chat_received_voice, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_voice, null);
        } else {
            return getItemViewType(position) == TYPE_RECEIVER_TXT ?
                    mInflater.inflate(R.layout.item_chat_received_message, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_message, null);
        }
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Message item = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(item, position);
        }

        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);
        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
        ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);

        TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);

        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);

        final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);

        if (item.getDirect() == MessageDirect.send) {
            File avatar = item.getFromUser().getAvatarFile();
            if (avatar != null) {
                Glide.with(mContext).load(avatar).into(iv_avatar);
            } else {
                iv_avatar.setImageResource(R.mipmap.ic_test_avatart);
            }
        } else {
            File avatar = ((UserInfo) item.getTargetInfo()).getAvatarFile();
            if (avatar != null) {
                Glide.with(mContext).load(avatar).into(iv_avatar);
            } else {
                iv_avatar.setImageResource(R.mipmap.ic_test_avatart);
            }
        }

        tv_time.setText(new SimpleDateFormat("MM-dd HH:mm").format(item.getCreateTime()));

        if (getItemViewType(position) == TYPE_SEND_TXT
                || getItemViewType(position) == TYPE_SEND_LOCATION
                || getItemViewType(position) == TYPE_SEND_VOICE) {

            if (item.getStatus() == MessageStatus.send_success) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getContentType() == ContentType.voice) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已发送");
                }
            } else if (item.getStatus() == MessageStatus.send_fail) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getContentType() == ContentType.voice) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            } else if (item.getStatus() == MessageStatus.receive_success) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getContentType() == ContentType.voice) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已阅读");
                }
            } else if (item.getStatus() == MessageStatus.send_going) {
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getContentType() == ContentType.voice) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            }
        }

        final MessageContent content = item.getContent();
        if (content.getContentType() == ContentType.text) {
            TextContent stringExtra = (TextContent) content;
            String text = stringExtra.getText();
            // tv_message.setText(text);
            ChatUtils.spannableEmoticonFilter(tv_message, text);
        } else if (content.getContentType() == ContentType.image) {
            dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, item);

        } else if (content.getContentType() == ContentType.location) {
            final LocationContent locationContent = (LocationContent) item.getContent();
            tv_location.setText(locationContent.getAddress());
//			tv_location.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(mContext, LocationActivity.class);
//					intent.putExtra("type", "scan");
//					intent.putExtra("latitude", Double.parseDouble(String.valueOf(locationContent.getLatitude())));
//					intent.putExtra("longtitude", Double.parseDouble(String.valueOf(locationContent.getLongitude())));
//					mContext.startActivity(intent);
//				}
//			});
        } else if (content.getContentType() == ContentType.voice) {
            final VoiceContent voiceContent = (VoiceContent) item.getContent();
            int length = voiceContent.getDuration();
//            if (length != 0) {
//                tv_voice_length.setVisibility(View.VISIBLE);
//            } else {
//                tv_voice_length.setVisibility(View.INVISIBLE);
//            }
            if (item.getDirect() == MessageDirect.send) {
                tv_voice_length.setText(length + "\''");
                iv_voice.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            player = new MediaPlayer();
                            player.setDataSource(voiceContent.getLocalPath());
                            player.prepare();
                            player.start();
                            iv_voice.setImageResource(R.drawable.voice_change_left);
                            final AnimationDrawable animationDrawable = (AnimationDrawable) iv_voice.getDrawable();
                            animationDrawable.start();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    animationDrawable.stop();
                                    iv_voice.setImageResource(R.drawable.voice_left3);
                                    player.release();//释放资源
                                    player = null;
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                tv_voice_length.setText(length + "\''");
                iv_voice.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            player = new MediaPlayer();
                            player.setDataSource(voiceContent.getLocalPath());
                            player.prepare();
                            player.start();
                            iv_voice.setImageResource(R.drawable.voice_change_right);
                            final AnimationDrawable animationDrawable = (AnimationDrawable) iv_voice.getDrawable();
                            animationDrawable.start();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    animationDrawable.stop();
                                    iv_voice.setImageResource(R.drawable.voice_right3);
                                    player.release();//释放资源
                                    player = null;
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return convertView;
    }


    private void dealWithImage(int position, final ProgressBar progress_load, ImageView iv_fail_resend, TextView tv_send_status, final ImageView iv_picture, Message item) {
        if (getItemViewType(position) == TYPE_SEND_IMAGE) {
            if (item.getStatus() == MessageStatus.send_draft) {
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            } else if (item.getStatus() == MessageStatus.send_success) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已发送");
            } else if (item.getStatus() == MessageStatus.send_fail) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            }
            Glide.with(mContext).load(((ImageContent) item.getContent()).getLocalThumbnailPath()).into(iv_picture);
        } else {
            if (item.getStatus() == MessageStatus.receive_going) {
                progress_load.setVisibility(View.VISIBLE);
            } else if (item.getStatus() == MessageStatus.receive_success) {
                progress_load.setVisibility(View.INVISIBLE);
            } else if (item.getStatus() == MessageStatus.receive_fail) {
                progress_load.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(((ImageContent) item.getContent()).getLocalThumbnailPath()).into(iv_picture);
        }
    }


}
