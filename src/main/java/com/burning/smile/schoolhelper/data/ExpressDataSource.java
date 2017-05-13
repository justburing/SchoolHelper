package com.burning.smile.schoolhelper.data;

import java.util.List;

/**
 * Created by smile on 2017/3/13.
 */
public interface ExpressDataSource {

    interface GetExpressListBeanCallback {

        void onLoadExpressListBean(ExpressListBean bean);

        void onDataNotAvailable();
    }

    interface GetExpressListCallback {
        void onLoadExpressList(List<Express> expresses);

        void onDataNotAvailable();
    }

    void getExpressListBean(GetExpressListBeanCallback callback);

    void getExpressList(GetExpressListCallback callback);


}
