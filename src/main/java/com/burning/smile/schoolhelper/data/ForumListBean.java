package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/4/6.
 */
public class ForumListBean implements Serializable {


    /**
     * resources : [{"id":"6","title":"测试HK您","content":"","imgs":["http://112.74.36.71:8000/files/default/2017/05-03/155931398201354099.jpg","http://112.74.36.71:8000/files/default/2017/05-03/1559495398f9131206.mp3"],"is_elite":"0","is_stick":"0","last_post_time":"2017-05-04T09:36:40+00:00","group_id":"1","created_time":"2017-05-03T16:00:00+00:00","updated_time":"2017-05-04T09:36:40+00:00","post_num":"2","hit_num":"12","reward_coin":"0","type":"default","last_post_member":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"},"user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}},{"id":"5","title":"测试一下","content":"测试","imgs":["http://112.74.36.71:8000/files/default/2017/05-03/150902e0a572056663.jpg","http://112.74.36.71:8000/files/default/2017/05-03/150914ad01f9247385.jpg"],"is_elite":"0","is_stick":"0","last_post_time":"0","group_id":"1","created_time":"2017-05-03T15:09:16+00:00","updated_time":"2017-05-03T15:09:16+00:00","post_num":"0","hit_num":"15","reward_coin":"0","type":"default","last_post_member":[],"user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}},{"id":"4","title":"测试图片数据","content":"测试测试踩踩踩踩踩踩踩踩菜","imgs":[],"is_elite":"0","is_stick":"0","last_post_time":"0","group_id":"1","created_time":"2017-05-03T15:02:50+00:00","updated_time":"2017-05-03T15:02:50+00:00","post_num":"0","hit_num":"0","reward_coin":"0","type":"default","last_post_member":[],"user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}},{"id":"3","title":"测试测试测试","content":"","imgs":null,"is_elite":"0","is_stick":"0","last_post_time":"0","group_id":"1","created_time":"2017-05-03T14:55:06+00:00","updated_time":"2017-05-03T14:55:06+00:00","post_num":"0","hit_num":"0","reward_coin":"0","type":"default","last_post_member":[],"user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}},{"id":"1","title":"6666","content":"6666666","imgs":[""],"is_elite":"0","is_stick":"0","last_post_time":"0","group_id":"1","created_time":"2017-05-02T14:34:10+00:00","updated_time":"2017-05-03T09:29:31+00:00","post_num":"0","hit_num":"1","reward_coin":"0","type":"default","last_post_member":[],"user":{"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}},{"id":"2","title":"测试xss攻击","content":"在目标站点任意表单处插入如下JS代码。 这里假设目标站点的所有表单没有过滤html标签。即&lt;&gt;符合这些没有被过滤\n","imgs":[],"is_elite":"0","is_stick":"0","last_post_time":"0","group_id":"1","created_time":"2017-05-03T09:24:21+00:00","updated_time":"2017-05-03T09:24:21+00:00","post_num":"0","hit_num":"1","reward_coin":"0","type":"default","last_post_member":[],"user":{"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}}]
     * total : 6
     */

    private String total;
    private List<Forum> resources;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Forum> getResources() {
        return resources;
    }

    public void setResources(List<Forum> resources) {
        this.resources = resources;
    }

    public static class Forum {
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
         * hit_num : 12
         * reward_coin : 0
         * type : default
         * last_post_member : {"id":"6","nickname":"admin","avatar":"http://112.74.36.71:8000/files/user/2017/05-03/0918499e1d5f262350.jpg"}
         * user : {"id":"5","nickname":"BurNIng","avatar":"http://112.74.36.71:8000/files/user/2017/05-02/160311f37f41783842.jpg"}
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
        private LastPostMemberBean last_post_member;
        private User user;
        private List<String> imgs;

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

        public LastPostMemberBean getLast_post_member() {
            return last_post_member;
        }

        public void setLast_post_member(LastPostMemberBean last_post_member) {
            this.last_post_member = last_post_member;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<String> getImgs() {
            return imgs;
        }

        public void setImgs(List<String> imgs) {
            this.imgs = imgs;
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

        public static class User {
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
    }
}
