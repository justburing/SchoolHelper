package com.burning.smile.schoolhelper.funkdetail;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.activity.BaseActivity;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.FunkCommentListBean;
import com.burning.smile.schoolhelper.data.FunkListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.photoshower.PhotoViewActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.chat.ChatCommentKeyBoard;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;
import com.burning.smile.schoolhelper.util.emoij.Constants;
import com.burning.smile.schoolhelper.util.emoij.SimpleCommonUtils;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.sj.emoji.EmojiBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by smile on 2017/4/21.
 */
public class FunkDetailActiviity extends BaseActivity implements FunkDetailContract.View, FuncLayout.OnFuncKeyBoardListener {
    @BindView(R.id.iconBack)
    ImageView iconBack;
    @BindView(R.id.backLL)
    LinearLayout backLL;
    @BindView(R.id.publisherAvatar)
    CircleImageView publisherAvatar;
    @BindView(R.id.publisherNickname)
    TextView publisherNickname;
    @BindView(R.id.funkTime)
    TextView funkTime;
    @BindView(R.id.contentLL)
    LinearLayout contentLL;
    @BindView(R.id.rollPagerView)
    RollPagerView rollPagerView;
    @BindView(R.id.funkContent)
    TextView funkContent;
    @BindView(R.id.funkPrice)
    TextView funkPrice;
    @BindView(R.id.funkCommendLv)
    ListView funkCommendLv;
    @BindView(R.id.noCommentsView)
    TextView noCommentsView;
    @BindView(R.id.chatIcon)
    ImageView chatIcon;
    @BindView(R.id.chatText)
    TextView chatText;
    @BindView(R.id.chatLL)
    LinearLayout chatLL;
    @BindView(R.id.commentIcon)
    ImageView commentIcon;
    @BindView(R.id.commentText)
    TextView commentText;
    @BindView(R.id.commentLL)
    LinearLayout commentLL;
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
    @BindView(R.id.functionView)
    LinearLayout functionView;
    @BindView(R.id.ek_bar)
    ChatCommentKeyBoard ekBar;
    @BindView(R.id.iconMore)
    ImageView iconMore;


    private FunkDetailContract.Presenter mPresenter;
    private String funkId;
    private PictrueAdapter pictrueAdapter;
    private SrollViewPagerAdapter srollViewPagerAdapter;
    private PostCommendAdapter postCommendAdapter;
    private String userId;
    private String funkPosterId = "";
    private String postCommenterId = "";
    private Map map;
    private Map likedMap;
    private boolean isLiked = false;

    @Override
    protected void init() {
        ButterKnife.bind(this);
        UserInfoBean userBean = AndroidFileUtil.getObject(this, AppConfig.USER_FILE);
        userId = userBean.getUser().getId();
        pictrueAdapter = new PictrueAdapter(this);
        //rollPagerView.setAdapter(pictrueAdapter);

        //设置每个图片的切换时间
        rollPagerView.setPlayDelay(3000);
        //设置图片切换动画时间
        rollPagerView.setAnimationDurtion(500);


        //设置适配器
        srollViewPagerAdapter = new SrollViewPagerAdapter();
        rollPagerView.setAdapter(srollViewPagerAdapter);
        rollPagerView.setHintView(new ColorPointHintView(this, Color.RED, Color.DKGRAY));

        postCommendAdapter = new PostCommendAdapter(this);
        funkCommendLv.setAdapter(postCommendAdapter);
        initEmoticonsKeyBoardBar();
        funkId = getIntent().getStringExtra("funkId");
        map = AndroidFileUtil.getObject(this, AppConfig.COLLECTION_GOODS);
        if (map != null) {
            if (map.get(AppConfig.COLLECTION_GOODS + funkId) != null && map.get(AppConfig.COLLECTION_GOODS + funkId).toString().equals(funkId)) {
                collectIcon.setImageResource(R.mipmap.ic_collected);
                collectText.setText("已收藏");
            }
        }
        likedMap = AndroidFileUtil.getObject(this, AppConfig.LIKED_GOODS);
        if (likedMap != null) {
            if (likedMap.get(AppConfig.LIKED_GOODS + funkId) != null && likedMap.get(AppConfig.LIKED_GOODS + funkId).toString().equals(funkId)) {
                isLiked = true;
            }
        }
        new FunkDetailPresenter(this).start();
    }


    private void initEmoticonsKeyBoardBar() {
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.getView().setVisibility(View.GONE);
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
                if (!TextUtils.isEmpty(ekBar.getEtChat().getText().toString())) {
                    if (!postCommenterId.equals("") && ekBar.getEtChat().getText().toString().startsWith("回复 @")) {
                        mPresenter.postFunkComment(funkId, ekBar.getEtChat().getText().toString(), postCommenterId);
                    } else {
                        mPresenter.postFunkComment(funkId, ekBar.getEtChat().getText().toString(), funkPosterId);
                    }

                } else {
                    toast("请先输入内容");
                }
                ekBar.getEtChat().setText("");
            }
        });
        ekBar.getBtn_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ekBar.getView().getVisibility() == View.VISIBLE) {
                    ekBar.getView().setVisibility(View.GONE);
                    functionView.setVisibility(View.VISIBLE);
                    functionView.bringToFront();
                }
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
        return R.layout.funk_detail_act;
    }


    @OnClick({R.id.backLL, R.id.chatLL, R.id.collectLL, R.id.dealLL, R.id.commentLL, R.id.iconMore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLL:
                finish();
                break;
            case R.id.chatLL:
                break;
            case R.id.commentLL:
                if (ekBar.getView().getVisibility() == View.GONE) {
                    ekBar.getView().setVisibility(View.VISIBLE);
                    ekBar.getView().bringToFront();
                    functionView.setVisibility(View.GONE);
                }
                break;
            case R.id.collectLL:
                if (collectText.getText().toString().equals("收藏")) {
                    mPresenter.collect(funkId);
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
                                    mPresenter.cancelCollect(funkId);
                                }
                            }).create();
                    dialog.show();

                }
                break;
            case R.id.dealLL:
                break;

            case R.id.iconMore:
                showMoreDialog(isLiked);
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        funkId = savedInstanceState.getString("funkId");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("funkId", funkId);
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
    public void loadDataSuccess(String msg, FunkListBean.Funk funk) {
        Glide.with(this).load(funk.getPublisher().getAvatar()).error(R.mipmap.ic_test_avatart).into(publisherAvatar);
        publisherNickname.setText(funk.getPublisher().getNickname());
        funkPosterId = funk.getPublisher().getId();
        funkTime.setText(funk.getCreated_time());
        funkPrice.setText("￥" + funk.getPrice() + "元");
        //  funkTitle.setText(funk.getTitle());
        funkContent.setText(funk.getBody());
        List<String> imgs = funk.getImgs();
        if (imgs != null && imgs.size() != 0) {
            // pictrueAdapter.setData(imgs);
            srollViewPagerAdapter.setUrls(imgs);
        }
        mPresenter.loadFunkComment(funkId);
        iconMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadDataFailure(String msg) {
        toast(msg);
    }

    @Override
    public void loadCommentSuccess(String msg, List<FunkCommentListBean.PostComment> commentList) {

        if (commentList != null && commentList.size() > 0) {
            postCommendAdapter.setData(commentList);
            noCommentsView.setVisibility(View.GONE);
        } else {
            noCommentsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadCommentFailure(String msg) {
        toast(msg);
    }

    @Override
    public void postCommentSuccess(String msg, FunkCommentListBean.PostComment comment) {
        postCommendAdapter.addItem(comment);
        if (postCommendAdapter.getCount() != 0) {
            noCommentsView.setVisibility(View.GONE);
        }
        if (ekBar.getView().getVisibility() == View.VISIBLE) {
            ekBar.getView().setVisibility(View.GONE);
            functionView.setVisibility(View.VISIBLE);
        }
        postCommenterId = "";
        ekBar.getEtChat().setText("");
    }

    @Override
    public void postCommentFailure(String msg) {
        toast(msg);
    }

    @Override
    public String getFunkId() {
        return funkId;
    }

    @Override
    public void setPresenter(FunkDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void OnFuncPop(int i) {

    }

    @Override
    public void OnFuncClose() {

    }

    @Override
    public void collectSuccessed(String msg) {
        toast(msg);
        collectIcon.setImageResource(R.mipmap.ic_collected);
        collectText.setText("已收藏");
        if (map == null) {
            map = new HashMap();
            map.put(AppConfig.COLLECTION_GOODS + funkId, funkId);
        } else {
            map.put(AppConfig.COLLECTION_GOODS + funkId, funkId);
        }
        AndroidFileUtil.saveObject(this, map, AppConfig.COLLECTION_GOODS);
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
            map.remove(AppConfig.COLLECTION_GOODS + funkId);
        AndroidFileUtil.saveObject(this, map, AppConfig.COLLECTION_GOODS);
    }

    @Override
    public void likeFunkFailure(String msg) {
        if (msg.equals("您已经点赞过了!")) {
            toast("点赞成功");
            if (likedMap == null) {
                likedMap = new HashMap();
                likedMap.put(AppConfig.LIKED_GOODS + funkId, funkId);
            } else {
                likedMap.put(AppConfig.LIKED_GOODS + funkId, funkId);
            }
            isLiked = true;
        } else {
            toast(msg);
        }
    }

    @Override
    public void likeFunkSuccessed(String msg) {
        toast(msg);
        if (likedMap == null) {
            likedMap = new HashMap();
            likedMap.put(AppConfig.LIKED_GOODS + funkId, funkId);
        } else {
            likedMap.put(AppConfig.LIKED_GOODS + funkId, funkId);
        }
        AndroidFileUtil.saveObject(this, likedMap, AppConfig.LIKED_GOODS);
        isLiked = true;
    }

    @Override
    public void cancelLikeFunkFailure(String msg) {
        toast(msg);
    }

    @Override
    public void cancelLikeFunkSuccessed(String msg) {
        toast(msg);
        if (likedMap != null)
            likedMap.remove(AppConfig.LIKED_GOODS + funkId);
        AndroidFileUtil.saveObject(this, likedMap, AppConfig.LIKED_GOODS);
        isLiked = false;
    }

    @Override
    public void collectFailure(String msg) {
        toast(msg);
    }


    public void showMoreDialog(boolean isLiked) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_funk_more_dialog, null);
        LinearLayout likeLL = (LinearLayout) dialogView.findViewById(R.id.likeLL);
        ImageView likeIcon = (ImageView) dialogView.findViewById(R.id.likeIcon);
        final TextView likeText = (TextView) dialogView.findViewById(R.id.likeText);
        if (isLiked) {
            likeIcon.setImageResource(R.mipmap.ic_liked);
            likeText.setText("取消点赞");
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
        likeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (likeText.getText().toString().equals("取消点赞")) {
                   mPresenter.cancelLikeFunk(funkId);
                } else {
                    mPresenter.likeFunk(funkId);
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
                mPresenter.loadFunkDetail(funkId);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_forum_lv_gv, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_forum_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String url = data.get(position);
//            if (map.get("url").toString().contains("http")) {
//                Glide.with(mContext).load(map.get("url").toString()).into(viewHolder.imageView);
//            } else if (map.get("url").toString().contains("Drawable")) {
//                Glide.with(mContext).load(R.mipmap.ic_take_photo).into(viewHolder.imageView);
//            } else {
//                Glide.with(mContext).load(new File(map.get("url").toString())).error(R.mipmap.ic_launcher).crossFade().into(viewHolder.imageView);
//            }
            Glide.with(mContext).load(url).error(R.mipmap.ic_test).crossFade().into(viewHolder.imageView);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FunkDetailActiviity.this, PhotoViewActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("pics", (ArrayList<String>) data);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private ImageView imageView;
        }
    }

    class PostCommendAdapter extends BaseAdapter {
        private Context mContext;
        private List<FunkCommentListBean.PostComment> data;

        public PostCommendAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<FunkCommentListBean.PostComment> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public void addItem(FunkCommentListBean.PostComment comment) {
            if (data == null) {
                data = new ArrayList<>();
            }
            data.add(0, comment);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public FunkCommentListBean.PostComment getItem(int position) {
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_funk_commend_lv, null);
                viewHolder.posterAvatar = (ImageView) convertView.findViewById(R.id.itemPosterAvatar);
                viewHolder.postNickname = (TextView) convertView.findViewById(R.id.itemPosterNickname);
                viewHolder.postContent = (EmoticonsEditText) convertView.findViewById(R.id.itemPostContent);
                viewHolder.postTime = (TextView) convertView.findViewById(R.id.itemPostTime);
                SimpleCommonUtils.initEmoticonsEditText(viewHolder.postContent);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final FunkCommentListBean.PostComment comment = data.get(position);
            if (comment != null) {
                Glide.with(mContext).load(comment.getFrom_user().getAvatar()).error(R.mipmap.ic_test_avatart).into(viewHolder.posterAvatar);
                viewHolder.postNickname.setText(comment.getFrom_user().getNickname());
                if (comment.getFrom_user().getId().equals(comment.getTo_user().getId()) || !comment.getContent().startsWith("回复@ ")) {
                    viewHolder.postContent.setText(comment.getContent());
                } else {
                    SpannableString spannableString = new SpannableString(comment.getContent());
                    spannableString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 4, 4 + comment.getTo_user().getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 4, 4 + comment.getTo_user().getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.postContent.setText(spannableString);
                }
                viewHolder.postTime.setText(comment.getCreated_time());
                viewHolder.postContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId.equals(comment.getFrom_user().getId())) {
                            toast("不能回复自己");
                        } else {
                            if (ekBar.getView().getVisibility() == View.GONE) {
                                ekBar.getView().setVisibility(View.VISIBLE);
                                ekBar.getView().bringToFront();
                                functionView.setVisibility(View.GONE);
                            }
                            ekBar.getEtChat().setText("回复@ " + comment.getFrom_user().getNickname() + "：");
                            postCommenterId = comment.getFrom_user().getId();
                        }
                    }
                });
            }
            return convertView;
        }


        class ViewHolder {
            private ImageView posterAvatar;
            private TextView postNickname;
            private EmoticonsEditText postContent;
            private TextView postTime;
        }
    }


    class SrollViewPagerAdapter extends StaticPagerAdapter {
        private List<String> urls;

        public void setUrls(List<String> url) {
            urls = url;
            notifyDataSetChanged();
        }

        //        private String[] urls = {
//                "http://d.hiphotos.baidu.com/zhidao/pic/item/3b87e950352ac65c1b6a0042f9f2b21193138a97.jpg",
//                "http://h.hiphotos.baidu.com/zhidao/pic/item/e824b899a9014c0869f80ddd0d7b02087af4f482.jpg",
//                "http://hiphotos.baidu.com/lvpics/pic/item/eab9044c62e7dca0d62afce6.jpg",
//                "http://hiphotos.baidu.com/lvpics/pic/item/246cca2a810fa1fa033bf6ba.jpg",
//                "http://hiphotos.baidu.com/lvpics/pic/item/42e89c268b2ab94f8b82a114.jpg",
//                "http://e.hiphotos.baidu.com/zhidao/pic/item/3bf33a87e950352aaa95e1115143fbf2b2118b37.jpg"
//        };
        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(container.getContext()).load(urls.get(position)).error(R.mipmap.ic_test).crossFade().into(view);  // 加载网络图片
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FunkDetailActiviity.this, PhotoViewActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("pics", (ArrayList<String>) urls);
                    startActivity(intent);
                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return (urls == null || urls.size() == 0) ? 0 : urls.size();
        }
    }
}