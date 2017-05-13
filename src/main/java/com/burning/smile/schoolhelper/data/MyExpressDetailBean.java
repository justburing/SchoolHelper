package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/5/7.
 */
public class MyExpressDetailBean {

    /**
     * id : 2
     * type : 2
     * address_id : 0
     * title : 二区快递，请送到一区3号楼223
     * detail : 【菜鸟驿站】双11包裹剧增，请您务必在21:30前凭提货码（12208）至浙江理工生活二区博研店菜鸟驿站领走11月21日中通包裹，询18968005153。不用排队取快递，在应用市场下载阿里旗下app菜鸟裹裹，就能享受包裹侠送货上门和寄件优惠！。
     * is_urgent : 1
     * offer : 4
     * status : 1
     * receiver_id : 0
     * created_time : 03-01 09:24
     * publisher : {"id":"3","nickname":"波波","avatar":"http://www.xyb-dev.com/files/user/2017/04-07/063818a563ff409641.jpg"}
     * applys : [{"id":"8","nickname":"admin","avatar":""},{"id":"9","nickname":"天天凯","avatar":""}]
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
    private List<ApplysBean> applys;
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

    public List<ApplysBean> getApplys() {
        return applys;
    }

    public void setApplys(List<ApplysBean> applys) {
        this.applys = applys;
    }

    public static class PublisherBean {
        /**
         * id : 3
         * nickname : 波波
         * avatar : http://www.xyb-dev.com/files/user/2017/04-07/063818a563ff409641.jpg
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

    public static class ApplysBean {
        /**
         * id : 8
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
