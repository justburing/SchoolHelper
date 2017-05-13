package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/5/4.
 */
public class ForumDetailBean implements Serializable {

    /**
     * id : 6
     * title : 测试HK您
     * content :
     * imgs : ["http://112.74.36.71:8000/files/default/2017/05-03/155931398201354099.jpg","http://112.74.36.71:8000/files/default/2017/05-03/1559495398f9131206.mp3"]
     * is_elite : 0
     * is_stick : 0
     * last_post_time : 2017-05-04T09:36:40+00:00
     * group_id : 1
     * created_time : 2017-05-03T16:00:00+00:00
     * updated_time : 2017-05-04T09:36:40+00:00
     * post_num : 2
     * hit_num : 13
     * reward_coin : 0
     * type : default
     * comments : [{"id":"3","thread_id":"6","content":"测试啦","post_id":"0","created_time":"2017-05-04T09:35:29+00:00","adopt":"0","user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"},"from_user":"","post_num":"1","item_comments":[{"id":"4","thread_id":"6","content":"测试啦","post_id":"3","created_time":"2017-05-04T09:36:40+00:00","adopt":"0","user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"},"from_user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}}]}]
     * user : {"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}
     * last_post_member : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}
     */

    private String id;
    private String title;
    private String content;
    private String is_elite;
    private String is_stick;
    private String last_post_time;
    private String group_id;
    private String created_time;
    private String updated_time;
    private String post_num;
    private String hit_num;
    private String reward_coin;
    private String type;
    private UserBean user;
    private LastPostMemberBean last_post_member;
    private List<String> imgs;
    private List<CommentsBean> comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIs_elite() {
        return is_elite;
    }

    public void setIs_elite(String is_elite) {
        this.is_elite = is_elite;
    }

    public String getIs_stick() {
        return is_stick;
    }

    public void setIs_stick(String is_stick) {
        this.is_stick = is_stick;
    }

    public String getLast_post_time() {
        return last_post_time;
    }

    public void setLast_post_time(String last_post_time) {
        this.last_post_time = last_post_time;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
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

    public String getPost_num() {
        return post_num;
    }

    public void setPost_num(String post_num) {
        this.post_num = post_num;
    }

    public String getHit_num() {
        return hit_num;
    }

    public void setHit_num(String hit_num) {
        this.hit_num = hit_num;
    }

    public String getReward_coin() {
        return reward_coin;
    }

    public void setReward_coin(String reward_coin) {
        this.reward_coin = reward_coin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public LastPostMemberBean getLast_post_member() {
        return last_post_member;
    }

    public void setLast_post_member(LastPostMemberBean last_post_member) {
        this.last_post_member = last_post_member;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<CommentsBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentsBean> comments) {
        this.comments = comments;
    }

    public static class UserBean {
        /**
         * id : 5
         * nickname : BurNIng
         * avatar : http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg
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

    public static class LastPostMemberBean {
        /**
         * id : 6
         * nickname : admin
         * avatar : http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg
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

    public static class CommentsBean {
        /**
         * id : 3
         * thread_id : 6
         * content : 测试啦
         * post_id : 0
         * created_time : 2017-05-04T09:35:29+00:00
         * adopt : 0
         * user : {"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}
         * from_user :
         * post_num : 1
         * item_comments : [{"id":"4","thread_id":"6","content":"测试啦","post_id":"3","created_time":"2017-05-04T09:36:40+00:00","adopt":"0","user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"},"from_user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}}]
         */

        private String id;
        private String thread_id;
        private String content;
        private String post_id;
        private String created_time;
        private String adopt;
        private PublisherUserBean user;
        private FromUserBean from_user;
        private String post_num;
        private List<ItemCommentsBean> item_comments;

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

        public PublisherUserBean getUser() {
            return user;
        }

        public void setUser(PublisherUserBean user) {
            this.user = user;
        }

        public FromUserBean getFrom_user() {
            return from_user;
        }

        public void setFrom_user(FromUserBean from_user) {
            this.from_user = from_user;
        }

        public String getPost_num() {
            return post_num;
        }

        public void setPost_num(String post_num) {
            this.post_num = post_num;
        }

        public List<ItemCommentsBean> getItem_comments() {
            return item_comments;
        }

        public void setItem_comments(List<ItemCommentsBean> item_comments) {
            this.item_comments = item_comments;
        }

        public static class PublisherUserBean {
            /**
             * id : 5
             * nickname : BurNIng
             * avatar : http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg
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
             * id : 5
             * nickname : BurNIng
             * avatar : http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg
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

        public static class ItemCommentsBean {
            /**
             * id : 4
             * thread_id : 6
             * content : 测试啦
             * post_id : 3
             * created_time : 2017-05-04T09:36:40+00:00
             * adopt : 0
             * user : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}
             * from_user : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}
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
                 * id : 6
                 * nickname : admin
                 * avatar : http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg
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
                 * id : 6
                 * nickname : admin
                 * avatar : http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg
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
}
