package com.burning.smile.schoolhelper.login;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;

/**
 * Created by smile on 2017/3/8.
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {
        void showEmptyAccout();

        void showEmptyPassword();

        void showLoginFailure(String errorString);

        void showLoginSuccess(String msg);

        void showLoadingView();

        void dimissLoadingView();

        boolean isRememberPassword();

        boolean isAutoLogin();

        void fillUserName(String userName);

        void fillUserPassword(String userPassword);

        void checkIsAutoLogin(boolean isAutoLogin);

        void checkIsRememberPassword(boolean isRememberPassword);

        void showUserAvatar();

    }

    interface Presenter extends BasePresenter {

        void login(String userName, String userPassword);
    }
}
