package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/3/8.
 */
public class UserInfoBean implements Serializable {


    /**
     * user : {"id":"5","username":"2013329620051","mobile":"","roles":["ROLE_USER","ROLE_ADMIN"],"nickname":"BurNIng","email":"test@qw.com","email_verified":"0","avatar":"http://112.74.36.71:8000/files/user/2017/05-06/101524c11664858383.gif","point":"0","coin":"62","qq":"","birthday":null,"credit":"348","login_time":"2017-05-15T09:13:49+00:00","login_ip":"112.17.240.131","created_ip":"112.17.245.152","created_time":"2017-04-23T15:17:02+00:00","updated_time":"2017-05-15T09:13:49+00:00","locked":"0","tag_id":"","is_pay_set":1}
     * token : 9blhr8aa7akoo8ggsckg4koko08wgs0
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

    public static class UserBean implements Serializable{
        /**
         * id : 5
         * username : 2013329620051
         * mobile :
         * roles : ["ROLE_USER","ROLE_ADMIN"]
         * nickname : BurNIng
         * email : test@qw.com
         * email_verified : 0
         * avatar : http://112.74.36.71:8000/files/user/2017/05-06/101524c11664858383.gif
         * point : 0
         * coin : 62
         * qq :
         * birthday : null
         * credit : 348
         * login_time : 2017-05-15T09:13:49+00:00
         * login_ip : 112.17.240.131
         * created_ip : 112.17.245.152
         * created_time : 2017-04-23T15:17:02+00:00
         * updated_time : 2017-05-15T09:13:49+00:00
         * locked : 0
         * tag_id :
         * is_pay_set : 1
         */

        private String id;
        private String username;
        private String mobile;
        private String nickname;
        private String email;
        private String email_verified;
        private String avatar;
        private String point;
        private String coin;
        private String qq;
        private Object birthday;
        private String credit;
        private String login_time;
        private String login_ip;
        private String created_ip;
        private String created_time;
        private String updated_time;
        private String locked;
        private String tag_id;
        private int is_pay_set;
        private List<String> roles;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail_verified() {
            return email_verified;
        }

        public void setEmail_verified(String email_verified) {
            this.email_verified = email_verified;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
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

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public Object getBirthday() {
            return birthday;
        }

        public void setBirthday(Object birthday) {
            this.birthday = birthday;
        }

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getLogin_time() {
            return login_time;
        }

        public void setLogin_time(String login_time) {
            this.login_time = login_time;
        }

        public String getLogin_ip() {
            return login_ip;
        }

        public void setLogin_ip(String login_ip) {
            this.login_ip = login_ip;
        }

        public String getCreated_ip() {
            return created_ip;
        }

        public void setCreated_ip(String created_ip) {
            this.created_ip = created_ip;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getUpdated_time() {
            return updated_time;
        }

        public void setUpdated_time(String updated_time) {
            this.updated_time = updated_time;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public String getTag_id() {
            return tag_id;
        }

        public void setTag_id(String tag_id) {
            this.tag_id = tag_id;
        }

        public int getIs_pay_set() {
            return is_pay_set;
        }

        public void setIs_pay_set(int is_pay_set) {
            this.is_pay_set = is_pay_set;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
