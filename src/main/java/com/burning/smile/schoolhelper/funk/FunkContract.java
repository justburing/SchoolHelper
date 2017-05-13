package com.burning.smile.schoolhelper.funk;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;
import com.burning.smile.schoolhelper.data.FunkListBean;

import java.util.List;

/**
 * Created by smile on 2017/4/6.
 */
public interface FunkContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void dimissLoadingView();

        void getDataSuccess(String msg, List<FunkListBean.Funk> funks);

        void getDataFailure(String msg);

        void loadMoreDataSuccess(String msg, List<FunkListBean.Funk> funks);

        void loadMoreDataFailure(String msg);

        void refreshDataSuccess(String msg, List<FunkListBean.Funk> funks);

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
        void getFunks(int start, int limit, String orderby);

        void loadMoreData(int start, int limit, String orderby);

        void refreshData();

        void openDrawerContent();
    }
}
