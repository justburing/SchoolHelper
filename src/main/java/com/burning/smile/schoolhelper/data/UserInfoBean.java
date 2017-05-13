package com.burning.smile.schoolhelper.data;

import java.io.Serializable;

/**
 * Created by smile on 2017/3/8.
 */
public class UserInfoBean implements Serializable {


    /**
     * user : {"id":"4","username":"2013329620045","mobile":"","nickname":"残念","email_verified":"0","avatar":null,"point":"0","coin":"0","created_ip":"115.204.229.55"}
     * token : stn1znv3s9w4sgkkcccg44w8k4sk00g
     */

    private UserBean user;
    private String token;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class UserBean implements Serializable {
        /**
         * id : 4
         * username : 2013329620045
         * mobile :
         * nickname : 残念
         * email_verified : 0
         * avatar : null
         * point : 0
         * coin : 0
         * created_ip : 115.204.229.55
         */

        private String id;
        private String username;
        private String mobile;
        private String nickname;
        private String email_verified;
        private Object avatar;
        private String point;
        private String coin;
        private String created_ip;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail_verified() {
            return email_verified;
        }

        public void setEmail_verified(String email_verified) {
            this.email_verified = email_verified;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getCreated_ip() {
            return created_ip;
        }

        public void setCreated_ip(String created_ip) {
            this.created_ip = created_ip;
        }
    }
}
