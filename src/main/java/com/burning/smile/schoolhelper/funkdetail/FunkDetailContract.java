package com.burning.smile.schoolhelper.funkdetail;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;
import com.burning.smile.schoolhelper.data.FunkCommentListBean;
import com.burning.smile.schoolhelper.data.FunkListBean;

import java.util.List;

/**
 * Created by smile on 2017/4/6.
 */
public interface FunkDetailContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void dimissLoadingView();

        void loadDataSuccess(String msg, FunkListBean.Funk funk);

        void loadDataFailure(String msg);

        void loadCommentSuccess(String msg, List<FunkCommentListBean.PostComment> commentList);

        void loadCommentFailure(String msg);

        void postCommentSuccess(String msg, FunkCommentListBean.PostComment comment);

        void postCommentFailure(String msg);

        String getFunkId();

        void collectSuccessed(String msg);

        void collectFailure(String msg);

        void cancelCollectSuccessed(String msg);

        void cancelCollectFailure(String msg);

        void likeFunkSuccessed(String msg);

        void likeFunkFailure(String msg);

        void cancelLikeFunkSuccessed(String msg);

        void cancelLikeFunkFailure(String msg);
    }

    interface Presenter extends BasePresenter {
        void loadFunkDetail(String funkId);

        void loadFunkComment(String funkId);

        void postFunkComment(String funkId, String content, String toUserId);

        void collect(String id);

        void cancelCollect(String id);

        void likeFunk(String id);

        void cancelLikeFunk(String id);
    }
}
