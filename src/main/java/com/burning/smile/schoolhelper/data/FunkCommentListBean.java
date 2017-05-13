package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/4/30.
 */
public class FunkCommentListBean implements Serializable {


    /**
     * resources : [{"id":"20","content":"测试","created_time":"2017-05-01T16:08:15+00:00","from_user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg"},"to_user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg"}},{"id":"19","content":"[dga][dga][dga][dga]竟然不断网","created_time":"2017-05-01T16:07:29+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"17","content":"[ecj][ecj][eft][eft][eft][ecg]","created_time":"2017-05-01T15:58:21+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"16","content":"dhshsh","created_time":"2017-05-01T15:42:49+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"15","content":"[ecc][ecc][ecc]fffff","created_time":"2017-05-01T15:42:20+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"14","content":"[eeq][eeq][eeq]阿西吧","created_time":"2017-05-01T14:35:54+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"13","content":"[eeq][eeq][eeq][eeq]","created_time":"2017-05-01T14:35:23+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"11","content":"[ebu][ebu][ebu][eci][eci][eci]","created_time":"2017-05-01T14:14:10+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"9","content":"[ecq][ecq][ecq]","created_time":"2017-05-01T14:13:44+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}},{"id":"8","content":" [eeo][eeo][eeo]","created_time":"2017-05-01T14:12:46+00:00","from_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"},"to_user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/1244182845ae086577.gif"}}]
     * total : 14
     */

    private String total;
    private List<PostComment> resources;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<PostComment> getResources() {
        return resources;
    }

    public void setResources(List<PostComment> resources) {
        this.resources = resources;
    }

    public static class PostComment {
        /**
         * id : 20
         * content : 测试
         * created_time : 2017-05-01T16:08:15+00:00
         * from_user : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg"}
         * to_user : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg"}
         */

        private String id;
        private String content;
        private String created_time;
        private FromUserBean from_user;
        private ToUserBean to_user;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public FromUserBean getFrom_user() {
            return from_user;
        }

        public void setFrom_user(FromUserBean from_user) {
            this.from_user = from_user;
        }

        public ToUserBean getTo_user() {
            return to_user;
        }

        public void setTo_user(ToUserBean to_user) {
            this.to_user = to_user;
        }

        public static class FromUserBean {
            /**
             * id : 6
             * nickname : admin
             * avatar : http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg
             */

            private String id;
            private String nickname;
            private String avatar;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }

        public static class ToUserBean {
            /**
             * id : 6
             * nickname : admin
             * avatar : http://112.74.36.71:8000/files/user/2017/04-26/064459bc8393392374.jpg
             */

            private String id;
            private String nickname;
            private String avatar;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
