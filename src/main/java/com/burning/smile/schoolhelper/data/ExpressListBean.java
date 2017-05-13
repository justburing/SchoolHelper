package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/3/13.
 */
public class ExpressListBean implements Serializable {

    /**
     * resources : [{"id":"1","type":"1","address_id":"0","title":"测试","detail":"测试","is_urgent":"0","offer":"3","status":"0","receiver_id":"0","created_time":"03-07 01:24","publisher":{"id":"3","nickname":"admin","avatar":""}}]
     * total : 1
     */

    private String total;
    private List<Express> resources;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Express> getResources() {
        return resources;
    }

    public void setResources(List<Express> resources) {
        this.resources = resources;
    }

    public static class Express implements Serializable {
        /**
         * id : 1
         * type : 1
         * address_id : 0
         * title : 测试
         * detail : 测试
         * is_urgent : 0
         * offer : 3
         * status : 0
         * receiver_id : 0
         * created_time : 03-07 01:24
         * publisher : {"id":"3","nickname":"admin","avatar":""}
         */

        private String id;
        private String type;
        private String address_id;
        private String title;
        private String detail;
        private String is_urgent;
        private String offer;
        private String status;
        private String receiver_id;
        private String created_time;
        private PublisherBean publisher;
        private Receiver receiver;

        public Receiver getReceiver() {
            return receiver;
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAddress_id() {
            return address_id;
        }

        public void setAddress_id(String address_id) {
            this.address_id = address_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getIs_urgent() {
            return is_urgent;
        }

        public void setIs_urgent(String is_urgent) {
            this.is_urgent = is_urgent;
        }

        public String getOffer() {
            return offer;
        }

        public void setOffer(String offer) {
            this.offer = offer;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReceiver_id() {
            return receiver_id;
        }

        public void setReceiver_id(String receiver_id) {
            this.receiver_id = receiver_id;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public PublisherBean getPublisher() {
            return publisher;
        }

        public void setPublisher(PublisherBean publisher) {
            this.publisher = publisher;
        }

        public static class PublisherBean implements Serializable {
            /**
             * id : 3
             * nickname : admin
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

        public static class Receiver implements Serializable {
            /**
             * id : 3
             * nickname : admin
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
