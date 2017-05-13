package com.burning.smile.schoolhelper.expressdetail;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;
import com.burning.smile.schoolhelper.data.ExpressListBean;

/**
 * Created by smile on 2017/4/6.
 */
public interface ExpressDetailContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();

        void dimissLoadingView();

        void loadDataSuccess(String msg, ExpressListBean.Express express);

        void loadDataFailure(String msg);

        void dealExpressSuccess(String msg);

        void dealExpressFailure(String msg);

        String getExpressId();

        void collectSuccessed(String msg);

        void collectFailure(String msg);

        void cancelCollectSuccessed(String msg);

        void cancelCollectFailure(String msg);
    }

    interface Presenter extends BasePresenter {
        void loadExpressDetail(String expressId);

        void dealExpress(String expressId);

        void collect(String id);

        void cancelCollect(String id);
    }
}
