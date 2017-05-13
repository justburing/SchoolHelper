package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/5/3.
 */
public class ForumCommentListBean implements Serializable {

    /**
     * resources : [{"id":"5","thread_id":"1","content":"哇塞．真的没有了埃","post_id":"2","created_time":"2017-04-25T14:55:42+00:00","adopt":"0","user":{"id":"9","nickname":"天天凯","avatar":""},"from_user":{"id":"9","nickname":"天天凯","avatar":""}},{"id":"3","thread_id":"1","content":"哇塞．真的没有了埃","post_id":"2","created_time":"2017-04-25T14:50:45+00:00","adopt":"0","user":{"id":"9","nickname":"天天凯","avatar":""},"from_user":""}]
     * total : 2
     */

    private String total;
    private List<ResourcesBean> resources;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ResourcesBean> getResources() {
        return resources;
    }

    public void setResources(List<ResourcesBean> resources) {
        this.resources = resources;
    }

    public static class ResourcesBean {
        /**
         * id : 5
         * thread_id : 1
         * content : 哇塞．真的没有了埃
         * post_id : 2
         * created_time : 2017-04-25T14:55:42+00:00
         * adopt : 0
         * user : {"id":"9","nickname":"天天凯","avatar":""}
         * from_user : {"id":"9","nickname":"天天凯","avatar":""}
         */

        private String id;
        private String thread_id;
        private String content;
        private String post_id;
        private String created_time;
        private String adopt;
        private UserBean user;
        private FromUserBean from_user;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getThread_id() {
            return thread_id;
        }

        public void setThread_id(String thread_id) {
            this.thread_id = thread_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public String getAdopt() {
            return adopt;
        }

        public void setAdopt(String adopt) {
            this.adopt = adopt;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public FromUserBean getFrom_user() {
            return from_user;
        }

        public void setFrom_user(FromUserBean from_user) {
            this.from_user = from_user;
        }

        public static class UserBean {
            /**
             * id : 9
             * nickname : 天天凯
             * avatar :
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

        public static class FromUserBean {
            /**
             * id : 9
             * nickname : 天天凯
             * avatar :
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
