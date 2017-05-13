package com.burning.smile.schoolhelper.mycollection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.burning.smile.androidtools.tools.AndroidFileUtil;
import com.burning.smile.androidtools.tools.AndroidFragUtil;
import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.R;
import com.burning.smile.schoolhelper.data.ForumListBean;
import com.burning.smile.schoolhelper.data.UserInfoBean;
import com.burning.smile.schoolhelper.forumdetail.ForumDetailActivity;
import com.burning.smile.schoolhelper.util.LoadingFragment;
import com.burning.smile.schoolhelper.util.chat.ChatUtils;
import com.burning.smile.schoolhelper.util.retrofit.RetrofitUtil;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by smile on 2017/5/13.
 */
public class ForumCollectionFragment extends Fragment {
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.noDataView)
    LinearLayout noDataView;
    @BindView(R.id.refresh)
    Button refresh;


    private ListViewAdapter mAdapter;
    private UserInfoBean userInfoBean;
    private List<ForumListBean.Forum> forums;
    private Map map;

    public ForumCollectionFragment() {
    }

    public static ForumCollectionFragment newInstance() {
        ForumCollectionFragment fragment = new ForumCollectionFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.collection_forum_frag, null);
        ButterKnife.bind(this, view);
        userInfoBean = AndroidFileUtil.getObject(getActivity(), AppConfig.USER_FILE);
        map = AndroidFileUtil.getObject(getActivity(), AppConfig.COLLECTION_THREAD);
        mAdapter = new ListViewAdapter(getActivity());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(forums.get(position).getId(), position);
            }
        });
        getData();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        return view;
    }

    public void getData() {
        //  AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().getCollections(userInfoBean.getToken(), "thread")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "获取论坛收藏信息出错", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                Toast.makeText(getActivity(), object.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            Toast.makeText(getActivity(), "当前无网络,请检查网络状况后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject object) {
                        if (object != null) {
                            ForumListBean bean = JSON.parseObject(object.toJSONString(), ForumListBean.class);
                            if (bean != null) {
                                if (bean.getResources() != null && bean.getResources().size() != 0) {
                                    forums = bean.getResources();
                                    mAdapter.setData(forums);
                                    noDataView.setVisibility(View.GONE);
                                } else {
                                    noDataView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(getActivity(), "获取论坛收藏信息出错", Toast.LENGTH_SHORT).show();
                                noDataView.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });

    }

    public void cancelCollect(final String id, final int position) {
        AndroidFragUtil.showDialog(getActivity().getSupportFragmentManager(), new LoadingFragment());
        RetrofitUtil.getRetrofitApiInstance().cancelCollect(userInfoBean.getToken(), "thread", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (e instanceof MalformedJsonException) {
                            Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof HttpException) {
                            try {
                                JSONObject object = JSON.parseObject(((HttpException) e).response().errorBody().string(), JSONObject.class);
                                Toast.makeText(getActivity(), object.getJSONObject("error").getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (e instanceof ConnectException) {
                            Toast.makeText(getActivity(), "当前无网络,请检查网络状况后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("error", e.toString());
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        AndroidFragUtil.dismissDialog(getActivity().getSupportFragmentManager());
                        if (jsonObject.getString("success").equals("true")) {
                            forums.remove(position);
                            mAdapter.setData(forums);
                            if (map != null)
                                map.remove(AppConfig.COLLECTION_THREAD + id);
                            Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showDialog(final String forumId, final int position) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_collection_dialog, null);
        TextView viewIt = (TextView) dialogView.findViewById(R.id.viewIt);
        TextView cancelIt = (TextView) dialogView.findViewById(R.id.cancelIt);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_transparent);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.BottomToTop);
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
        cancelIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCollect(forumId, position);
                dialog.dismiss();
            }
        });
        viewIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), ForumDetailActivity.class).putExtra("forumId", forumId));
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext;
        private List<ForumListBean.Forum> forums;

        public ListViewAdapter(Context context) {
            this.mContext = context;
        }


        public void setData(List<ForumListBean.Forum> forums) {
            this.forums = forums;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return forums == null ? 0 : forums.size();
        }

        @Override
        public ForumListBean.Forum getItem(int position) {
            return forums == null ? null : forums.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_collection_forum_lv, null);
                viewHolder.userAvatar = (CircleImageView) convertView.findViewById(R.id.item_userAvatar);
                viewHolder.userNickname = (TextView) convertView.findViewById(R.id.item_userNickname);
                viewHolder.forumView = (TextView) convertView.findViewById(R.id.item_forum_view);
                viewHolder.forumMessage = (TextView) convertView.findViewById(R.id.item_forum_message);
                viewHolder.forumTitle = (TextView) convertView.findViewById(R.id.item_forum_title);
                viewHolder.forumContent = (TextView) convertView.findViewById(R.id.item_forum_content);
                viewHolder.forumTime = (TextView) convertView.findViewById(R.id.item_forum_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ForumListBean.Forum forum = forums.get(position);
            Glide.with(mContext).load(forum.getUser().getAvatar()).into(viewHolder.userAvatar);
            viewHolder.userNickname.setText(forum.getUser().getNickname());
            viewHolder.forumView.setText(forum.getHit_num());
            viewHolder.forumMessage.setText(forum.getPost_num());
            viewHolder.forumTitle.setText(forum.getTitle());
            // viewHolder.forumContent.setText(forum.getContent());
            ChatUtils.spannableEmoticonFilter(viewHolder.forumContent, forum.getContent());
            viewHolder.forumTime.setText(forum.getUpdated_time());
            return convertView;
        }

        private class ViewHolder {
            private CircleImageView userAvatar;
            private TextView userNickname;
            private TextView forumView;
            private TextView forumMessage;
            private TextView forumTitle;
            private TextView forumContent;
            private TextView forumTime;
        }
    }
}
