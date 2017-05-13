package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/4/6.
 */
public class FunkListBean implements Serializable {

    /**
     * resources : [{"id":"2","title":"我的小熊～谁来接走？ ","thumb":"http://www.xyb-dev.com/files/goods/70628d1823171.jpg","price":"128.00","status":"1","ups_num":"0","post_num":"0","hits":"0","created_time":"1970-01-01T00:00:00+00:00","updated_time":"1970-01-01T00:00:00+00:00","publisher":{"id":"3","nickname":"波波","avatar":"http://www.xyb-dev.com/files/user/70628d1823171.jpg"},"category":"玩偶"},{"id":"1","title":"卖一个新入手的玩偶哦，卡哇伊～～～只要99,贱卖！！！","thumb":"http://www.xyb-dev.com/files/goods/70628d1823171.jpg","price":"99.00","status":"1","ups_num":"0","post_num":"0","hits":"0","created_time":"1970-01-01T00:00:00+00:00","updated_time":"1970-01-01T00:00:00+00:00","publisher":{"id":"3","nickname":"波波","avatar":"http://www.xyb-dev.com/files/user/70628d1823171.jpg"},"category":"玩偶"}]
     * total : 2
     */

    private String total;
    private List<Funk> resources;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Funk> getResources() {
        return resources;
    }

    public void setResources(List<Funk> resources) {
        this.resources = resources;
    }

    public static class Funk implements Serializable {
        /**
         * id : 2
         * title : 我的小熊～谁来接走？
         * thumb : http://www.xyb-dev.com/files/goods/70628d1823171.jpg
         * price : 128.00
         * status : 1
         * ups_num : 0
         * post_num : 0
         * hits : 0
         * created_time : 1970-01-01T00:00:00+00:00
         * updated_time : 1970-01-01T00:00:00+00:00
         * publisher : {"id":"3","nickname":"波波","avatar":"http://www.xyb-dev.com/files/user/70628d1823171.jpg"}
         * category : 玩偶
         */

        private String id;
        private String title;
        private List<String> imgs;
        private String price;
        private String status;
        private String ups_num;
        private String post_num;
        private String hits;
        private String created_time;
        private String updated_time;
        private PublisherBean publisher;
        private String category;
        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

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

        public List<String> getImgs() {
            return imgs;
        }

        public void setImgs(List<String> imgs) {
            this.imgs = imgs;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUps_num() {
            return ups_num;
        }

        public void setUps_num(String ups_num) {
            this.ups_num = ups_num;
        }

        public String getPost_num() {
            return post_num;
        }

        public void setPost_num(String post_num) {
            this.post_num = post_num;
        }

        public String getHits() {
            return hits;
        }

        public void setHits(String hits) {
            this.hits = hits;
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

        public PublisherBean getPublisher() {
            return publisher;
        }

        public void setPublisher(PublisherBean publisher) {
            this.publisher = publisher;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public static class PublisherBean {
            /**
             * id : 3
             * nickname : 波波
             * avatar : http://www.xyb-dev.com/files/user/70628d1823171.jpg
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
