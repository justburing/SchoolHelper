package com.burning.smile.schoolhelper.register;

import com.burning.smile.schoolhelper.BasePresenter;
import com.burning.smile.schoolhelper.BaseView;


/**
 * Created by smile on 2017/3/8.
 */
public interface RegisterContract {

    interface View extends BaseView<Presenter> {
        void showAvatarDialog();

        void showAvatar();

        void showUserNameEmptyError();

        void showUserPasswordEmptyError();

        void showLoadingView();

        void dimissLoadingView();

        void showRegisterSuccess(String msg);

        void showRegisterFialure(String msg);

    }

    interface Presenter extends BasePresenter {
        String takePhoto();

        void choosePicture();

        void register(String userName, String userNickname, String userEmail, String userPassword);
    }
}
