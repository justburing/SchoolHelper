package com.burning.smile.schoolhelper.forum;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;
import com.burning.smile.schoolhelper.data.ForumListBean;

import java.util.List;

/**
 * Created by smile on 2017/4/6.
 */
public interface ForumContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void dimissLoadingView();

        void getDataSuccess(String msg, List<ForumListBean.Forum> forums);

        void getDataFailure(String msg);

        void loadMoreDataSuccess(String msg, List<ForumListBean.Forum> forums);

        void loadMoreDataFailure(String msg);

        void refreshDataSuccess(String msg, List<ForumListBean.Forum> forums);

        void refreshDataFailure(String msg);

        void onLoad();

        void setStart(int start);

        void setLimit(int limit);

        int getStart();

        int getLimit();

        void setLoadStart(int start);

        int getLoadStart();

        String getOrderBy();


    }

    interface Presenter extends BasePresenter {
        void getForums(int start, int limit, String orderBY);

        void loadMoreData(int start, int limit, String orderBy);

        void refreshData();

        void openDrawerContent();
    }
}
