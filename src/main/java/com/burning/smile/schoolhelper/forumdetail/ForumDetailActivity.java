package com.burning.smile.schoolhelper.forumdetail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.ForumDetailBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.chat.ChatCommentKeyBoard;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;
import com.burning.smile.schoolhelper.util.emoij.Constants;
import com.burning.smile.schoolhelper.util.emoij.SimpleCommonUtils;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.burning.smile.schoolhelper.widget.ResizeListView;
import com.google.gson.stream.MalformedJsonException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.sj.emoji.EmojiBean;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sj.keyboard.EmoticonsKeyBoardPopWindow;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by smile on 2017/4/21.
 */
public class ForumDetailActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.publisherAvatar)
    CircleImageView publisherAvatar;
    @BindView(R.id.publisherNickname)
    TextView publisherNickname;
    @BindView(R.id.forumTitle)
    TextView forumTitle;
    @BindView(R.id.follow)
    TextView follow;
    @BindView(R.id.forumContent)
    EmoticonsEditText forumContent;
    @BindView(R.id.forumImageLv)
    ResizeListView forumImageLv;
    @BindView(R.id.forumFileLv)
    ResizeListView forumFileLv;
    @BindView(R.id.forumTime)
    TextView forumTime;
    @BindView(R.id.commendLv)
    ResizeListView commendLv;
    @BindView(R.id.ek_bar)
    ChatCommentKeyBoard ekBar;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.iconMore)
    ImageView iconMore;

    private PictrueAdapter pictrueAdapter;
    private FileAdapter fileAdapter;
    private CommendAdapter commendAdapter;
    private UserInfoBean userInfoBean;
    private String forumId;
    private EmoticonsKeyBoardPopWindow mKeyBoardPopWindow;
    private List<ForumDetailBean.CommentsBean> comments;
    private List<ForumDetailBean.CommentsBean.ItemCommentsBean> itemComments;
    private String threadId;
    private String content;
    private String postId;
    private String toUserId;
    private String posterId;
    private int positionOne;
    private List<String> imageUrls;
    private List<String> fileUrls;
    private Map collectionMap;
    private boolean isCollected = false;
    //通知栏进度条
    private NotificationManager mNotificationManager = null;
    private Notification mNotification;
    private long fileSize;
    private long downLoadFileSize;
    private long mNetworkSpeed;
    private float downLoadProgress;
    private String fileurl;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 定义一个Handler，用于处理下载线程与UI间通讯
            switch (msg.what) {
                case 1:
                    String totalSize = "";
                    if (fileSize / 1024 / 1024 < 1) {
                        totalSize = new DecimalFormat(".00").format(fileSize / 1024) + "Kb";
                    } else {
                        totalSize = new DecimalFormat(".00").format(fileSize / 1024 / 1024) + "M";
                    }

                    String currentSize;
                    if (downLoadFileSize / 1024 / 1024 < 1) {
                        currentSize = new DecimalFormat(".00").format(downLoadFileSize / 1024);
                    } else {
                        currentSize = new DecimalFormat(".00").format(downLoadFileSize / 1024 / 1024);
                    }

                    String result = new DecimalFormat(".00").format(downLoadProgress * 100);
                    String netSpeed;
                    if (mNetworkSpeed / 1024 / 1024 < 1) {
                        netSpeed = new DecimalFormat(".00").format(mNetworkSpeed / 1024) + "Kb/s";
                    } else {
                        netSpeed = new DecimalFormat(".00").format(mNetworkSpeed / 1024 / 1024) + "M/s";
                    }
                    mNotification.contentView.setTextViewText(R.id.content_view_size, currentSize + "/" + totalSize);
                    mNotification.contentView.setTextViewText(R.id.content_view_progress, result + "%");
                    mNotification.contentView.setTextViewText(R.id.content_view_netSpeed, netSpeed);
                    mNotification.contentView.setProgressBar(R.id.content_view_progressBar, (int) fileSize, (int) downLoadFileSize, false);
                    mNotificationManager.notify(1, mNotification);
                    // Log.e("size", "文件"+downLoadFileSize+":"+fileSize+":"+result);
                    break;
                case 2:
                    //   toast("文件下载完成,文件保存在" + ((File) msg.obj).getAbsolutePath());
                    toast("文件 " + ((File) msg.obj).getName() + " 下载成功");
                    break;
                case -1:
                    if (msg.obj instanceof ConnectException) {
                        toast("当前无网络,请检查网络状况后重试");
                    } else {
                        toast(msg.obj.toString());
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void init() {
        ButterKnife.bind(this);
        userInfoBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        forumId = getIntent().getStringExtra("forumId");
        Log.e("forumId", forumId);
        collectionMap = AndroidFileUtil.getObject(this, AppConfig.COLLECTION_THREAD);
        if (collectionMap != null && collectionMap.get(AppConfig.COLLECTION_THREAD + forumId) != null && collectionMap.get(AppConfig.COLLECTION_THREAD + forumId).toString().equals(forumId)) {
            isCollected = true;
        }
        SimpleCommonUtils.initEmoticonsEditText(forumContent);
        pictrueAdapter = new PictrueAdapter(this);
        forumImageLv.setAdapter(pictrueAdapter);
        fileAdapter = new FileAdapter(this);
        forumFileLv.setAdapter(fileAdapter);
        forumFileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileurl = fileUrls.get(position);
                showDialog();
            }
        });
        commendAdapter = new CommendAdapter(this);
        commendLv.setAdapter(commendAdapter);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN.............");
                        scrollView.requestLayout();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE.............");
                        ekBar.reset();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("TAG", "ACTION_CANCEL.............");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP.............");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        initEmoticonsKeyBoardBar();
        getForum();

    }

    private void initEmoticonsKeyBoardBar() {
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.getView().bringToFront();
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ekBar.reset();
                content = ekBar.getEtChat().getText().toString();
                if (content.equals("")) {
                    toast("请先输入内容");
                    return;
                }
                if (ekBar.getEtChat().getHint().toString().contains("回复")) {
                    postForumItemComment(threadId, content, postId, toUserId);
                } else {
                    postForumContent(threadId, content);
                }
                ekBar.getEtChat().setText("");
            }
        });
        ekBar.getBtn_back().setVisibility(View.GONE);
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        forumId = savedInstanceState.getString("forumId");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("forumId", forumId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.forum_detail_act;
    }

    @OnClick({R.id.backLL, R.id.follow, R.id.iconMore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.follow:
                break;
            case R.id.iconMore:
                showMoreDialog(isCollected);
                break;
        }
    }


    public void getForum() {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getForum(userInfoBean.getToken(), forumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ForumDetailBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("获取帖子详情失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast("获取帖子详情失败");
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ForumDetailBean forum) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (forum != null) {
                            threadId = forum.getId();
                            posterId = forum.getUser().getId();
                            forumTitle.setText(forum.getTitle());
                            Glide.with(getApplicationContext()).load(forum.getUser().getAvatar()).into(publisherAvatar);
                            publisherNickname.setText(forum.getUser().getNickname());
                            List<String> imgs = forum.getImgs();
                            if (imgs != null && imgs.size() > 0) {
                                fileUrls = new ArrayList<String>();
                                imageUrls = new ArrayList<String>();
                                for (String s : imgs) {
                                    if (s.endsWith(".jpg") || s.endsWith(".png") || s.endsWith(".gif")) {
                                        imageUrls.add(s);
                                    } else {
                                        fileUrls.add(s);
                                    }
                                }
                                pictrueAdapter.setData(imageUrls);
                                fileAdapter.setData(fileUrls);
                            }
                            forumContent.setText(forum.getContent());
                            forumTime.setText(forum.getCreated_time());
                            comments = forum.getComments();
                            commendAdapter.notifyDataSetChanged();
                            if (comments == null) {
                                comments = new ArrayList<>();
                                noDataView.setVisibility(View.VISIBLE);
                            } else {
                                if (comments.size() == 0) {
                                    noDataView.setVisibility(View.VISIBLE);
                                } else {
                                    noDataView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            toast("获取帖子详情失败");
                            finish();
                        }
                    }
                });
    }

    public void postForumContent(String threadId, String content) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().postForumComment(userInfoBean.getToken(), threadId, content, posterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ForumDetailBean.CommentsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("发布评论失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast("发布评论失败");
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ForumDetailBean.CommentsBean commentsBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (commentsBean != null) {
                            comments.add(commentsBean);
                            commendAdapter.notifyDataSetChanged();
                            ekBar.getEtChat().setText("");
                            ekBar.getEtChat().setHint("说点什么吧");
                            noDataView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void postForumItemComment(String threadId, String content, String postId, String fromUserId) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().postForumItemComment(userInfoBean.getToken(), threadId, content, postId, fromUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ForumDetailBean.CommentsBean.ItemCommentsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("回复评论失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast("回复评论失败");
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(ForumDetailBean.CommentsBean.ItemCommentsBean itemCommentsBean) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        ekBar.getEtChat().setText("");
                        ekBar.getEtChat().setHint("说点什么吧");
                        if (itemCommentsBean != null) {
                            comments.get(positionOne).getItem_comments().add(itemCommentsBean);
                            commendAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    public void collect(String id) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().collect(userInfoBean.getToken(), "thread", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());

                        if (e instanceof MalformedJsonException) {
                            toast("收藏信息失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (jsonObject.getString("success").equals("true")) {
                            if (collectionMap == null) {
                                collectionMap = new HashMap();
                                collectionMap.put(AppConfig.COLLECTION_THREAD + forumId, forumId);
                            } else {
                                collectionMap.put(AppConfig.COLLECTION_THREAD + forumId, forumId);
                            }
                            AndroidFileUtil.saveObject(ForumDetailActivity.this, collectionMap, AppConfig.COLLECTION_THREAD);
                            isCollected = true;
                            toast("收藏成功");
                        } else {
                            toast("收藏失败");
                        }
                    }
                });

    }

    public void cancelCollect(String id) {
        AndroidFragUtil.showDialog(getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().cancelCollect(userInfoBean.getToken(), "thread", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            toast("取消收藏失败");
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                toast(object.getJSONObject("error").getString("message"));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            toast("当前无网络,请检查网络状况后重试");
                        } else {
                            toast(e.toString());
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getSupportFragmentManager());
                        if (jsonObject.getString("success").equals("true")) {
                            collectionMap.remove(AppConfig.COLLECTION_THREAD + forumId);
                            isCollected = false;
                            AndroidFileUtil.saveObject(ForumDetailActivity.this, collectionMap, AppConfig.COLLECTION_THREAD);
                            toast("取消收藏成功");
                        } else {
                            toast("取消收藏失败");
                        }
                    }
                });

    }


    public void downLoadFile(String fileName) {
        String name;
        if (fileName.equals("")) {
            name = null;
        } else {
            name = fileName.replaceAll("\\.", "") + fileurl.substring(fileurl.lastIndexOf("."), fileurl.length());
        }
        notificationInit();
        OkGo.get(fileurl)
                .tag(this)
                .cacheKey(fileurl)
                .execute(new FileCallback(Environment.getExternalStorageDirectory() + "/download/schoolhelper/", name) {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = file;
                        handler.sendMessage(msg);

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Message msg = new Message();
                        msg.what = -1;
                        msg.obj = e;
                        handler.sendMessage(msg);
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        Log.e("downLoad", currentSize + ">>>" + totalSize + ">>>" + progress + ">>>" + networkSpeed);
                        downLoadFileSize = currentSize;
                        fileSize = totalSize;
                        downLoadProgress = progress;
                        mNetworkSpeed = networkSpeed;
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                });
    }


    public void showMoreDialog(boolean isCollected) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_forum_more_dialog, null);
        LinearLayout collectLL = (LinearLayout) dialogView.findViewById(R.id.collectLL);
        ImageView collectIcon = (ImageView) dialogView.findViewById(R.id.collectIcon);
        final TextView collectText = (TextView) dialogView.findViewById(R.id.collectText);
        if (isCollected) {
            collectIcon.setImageResource(R.mipmap.ic_collected_2);
            collectText.setText("取消收藏");
        }
        LinearLayout reportLL = (LinearLayout) dialogView.findViewById(R.id.reporttLL);
        LinearLayout refreshLL = (LinearLayout) dialogView.findViewById(R.id.refreshLL);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent);
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
        dialog.setCancelable(true);
        dialog.show();
        collectLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (collectText.getText().equals("取消收藏")) {
                    cancelCollect(forumId);
                } else {
                    collect(forumId);
                }
            }
        });
        reportLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toast("设计中");
            }
        });
        refreshLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getForum();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showMoreOpDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_more_op_dialog, null);
        TextView returnFloor = (TextView) dialogView.findViewById(R.id.returnFloor);
        TextView reportFloor = (TextView) dialogView.findViewById(R.id.reportFloor);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(this, R.style.dialog_more_op);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        //window.setWindowAnimations(R.style.BottomToTop);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        returnFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        reportFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void showFilenameDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_filename_dialog, null);
        final TextView title = (TextView) dialogView.findViewById(R.id.dialogTitle);
        final EditText content = (EditText) dialogView.findViewById(R.id.editText);
        final Button cancel = (Button) dialogView.findViewById(R.id.no);
        final Button comfirm = (Button) dialogView.findViewById(R.id.yes);
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this).setView(dialogView).create();
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
                downLoadFile("");
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downLoadFile(content.getText().toString());
            }
        });
    }

    public void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("下载该资源?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        toast("即将开始下载");
                        showFilenameDialog();
                    }
                })
                .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void notificationInit() {
        //通知栏内显示下载进度条
//        Intent intent=new Intent(this,ForumDetailActivity.class);//点击进度条，进入程序
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "file/*");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.icon = R.mipmap.ic_icon_3;
        mNotification.tickerText = "开始下载";
        mNotification.contentView = new RemoteViews(getPackageName(), R.layout.view_notification);//通知栏中进度布局
        mNotification.contentIntent = pIntent;
        mNotificationManager.notify(1, mNotification);
    }

    @Override
    public void OnFuncPop(int i) {
    }

    @Override
    public void OnFuncClose() {

    }


    class PictrueAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> data;

        public PictrueAdapter(Context context) {
            this.mContext = context;
            data = new ArrayList<>();
        }

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public String getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_detail_lv, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String url = data.get(position);
            if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif")) {
                if (url.contains("http")) {
                    Glide.with(mContext).load(url).into(viewHolder.imageView);
                } else {
                    Glide.with(mContext).load(new File(url)).error(R.mipmap.ic_launcher).crossFade().into(viewHolder.imageView);
                }
            }
            return convertView;
        }

        class ViewHolder {
            private ImageView imageView;
        }
    }

    class FileAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> data;

        public FileAdapter(Context context) {
            this.mContext = context;
            data = new ArrayList<>();
        }

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public String getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_file_lv, null);
                viewHolder.fileName = (TextView) convertView.findViewById(R.id.fileName);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String url = data.get(position);
            viewHolder.fileName.setText(url.substring(url.lastIndexOf('/') + 1));
            return convertView;
        }

        class ViewHolder {
            private TextView fileName;
        }
    }

    class CommendAdapter extends BaseAdapter {

        private Context mContext;
        private List<ForumDetailBean.CommentsBean> data;

        public CommendAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<ForumDetailBean.CommentsBean> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return comments == null ? 0 : comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_commend_lv, null);
                viewHolder.commenderAvatar = (CircleImageView) convertView.findViewById(R.id.commenderAvatar);
                viewHolder.commenderNickname = (TextView) convertView.findViewById(R.id.commenderNickname);
                viewHolder.commenderLevel = (TextView) convertView.findViewById(R.id.commenderLevel);
                viewHolder.commendContent = (EmoticonsEditText) convertView.findViewById(R.id.commendContent);
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.commendContent);
                viewHolder.commendFloor = (TextView) convertView.findViewById(R.id.whichFloor);
                viewHolder.commendTime = (TextView) convertView.findViewById(R.id.floorCommendTime);
                viewHolder.commendCommendLv = (ResizeListView) convertView.findViewById(R.id.commendsCommendLv);
                viewHolder.moreOperation = (ImageView) convertView.findViewById(R.id.item_more_op);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ForumDetailBean.CommentsBean commentsBean = comments.get(position);
            Glide.with(mContext).load(commentsBean.getUser().getAvatar()).into(viewHolder.commenderAvatar);
            viewHolder.commenderNickname.setText(commentsBean.getUser().getNickname());
            viewHolder.commenderLevel.setVisibility(View.GONE);
            viewHolder.commendContent.setText(commentsBean.getContent());
            viewHolder.commendFloor.setText("第" + (position + 1) + "楼");
            viewHolder.commendTime.setText(commentsBean.getCreated_time());
            CommendCommendAdapter adapter = new CommendCommendAdapter(mContext, position);
            viewHolder.commendCommendLv.setAdapter(adapter);
            adapter.setData(commentsBean.getItem_comments());
            viewHolder.commendContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ekBar.getEtChat().setHint("回复 " + commentsBean.getUser().getNickname());
                    postId = commentsBean.getId();
                    toUserId = commentsBean.getUser().getId();
                    positionOne = position;
                }
            });
            viewHolder.moreOperation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreOpDialog();
                }
            });
            return convertView;
        }

        class ViewHolder {
            private CircleImageView commenderAvatar;
            private TextView commenderNickname;
            private TextView commenderLevel;
            private EmoticonsEditText commendContent;
            private TextView commendFloor;
            private TextView commendTime;
            private ResizeListView commendCommendLv;
            private ImageView moreOperation;
        }
    }

    class CommendCommendAdapter extends BaseAdapter {

        private Context mContext;
        private List<ForumDetailBean.CommentsBean.ItemCommentsBean> data;
        private int index;

        public CommendCommendAdapter(Context context, int index) {
            this.mContext = context;
            this.index = index;

        }

        public void setData(List<ForumDetailBean.CommentsBean.ItemCommentsBean> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public ForumDetailBean.CommentsBean.ItemCommentsBean getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_commend_commend_lv, null);
                viewHolder.commendCommendContent = (EmoticonsEditText) convertView.findViewById(R.id.commenCommendContent);
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.commendCommendContent);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ForumDetailBean.CommentsBean.ItemCommentsBean itemCommentsBean = data.get(position);
            if (itemCommentsBean.getUser() != null && itemCommentsBean.getFrom_user() != null) {
                if (itemCommentsBean.getFrom_user().getId().equals(itemCommentsBean.getUser().getId())) {
                    viewHolder.commendCommendContent.setText(itemCommentsBean.getUser().getNickname() + "：" + itemCommentsBean.getContent());
                } else {
                    viewHolder.commendCommendContent.setText(itemCommentsBean.getUser().getNickname() + " 回复 " + itemCommentsBean.getFrom_user().getNickname() + "：" + itemCommentsBean.getContent());
                }
            } else {
                viewHolder.commendCommendContent.setText(itemCommentsBean.getUser().getNickname() + "：" + itemCommentsBean.getContent());
            }
            viewHolder.commendCommendContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positionOne = index;
                    ekBar.getEtChat().setHint("回复 " + itemCommentsBean.getUser().getNickname());
                    postId = itemCommentsBean.getId();
                    toUserId = itemCommentsBean.getUser().getId();
                }
            });
            return convertView;
        }

        class ViewHolder {
            private EmoticonsEditText commendCommendContent;
        }
    }
}
