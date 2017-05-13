package com.burning.smile.schoolhelper.express;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;
import com.burning.smile.schoolhelper.data.ExpressListBean;

import java.util.List;

/**
 * Created by smile on 2017/3/13.
 */
public interface ExpressContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void dimissLoadingView();

        void getDataSuccess(String msg, List<ExpressListBean.Express> expresses);

        void getDataFailure(String msg);

        void loadMoreDataSuccess(String msg, List<ExpressListBean.Express> expresses);

        void loadMoreDataFailure(String msg);

        void refreshDataSuccess(String msg, List<ExpressListBean.Express> expresses);

        void refreshDataFailure(String msg);

        void onLoad();

        void setStart(int start);

        void setLimit(int limit);

        int getStart();

        int getLimit();

        void setLoadStart(int start);

        int getLoadStart();

        String getIsUrgent();
    }

    interface Presenter extends BasePresenter {
        void getExpresses(int start, int limit, String is_urgent);

        void loadMoreData(int start, int limit, String is_urgent);

        void refreshData();

        void openDrawerContent();

        void switchData();

    }
}
