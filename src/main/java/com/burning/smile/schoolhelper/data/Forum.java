package com.burning.smile.schoolhelper.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smile on 2017/3/14.
 */
public class Forum implements Serializable {

    private String id;
    private String commendNum;
    private String messageNum;
    private String title;
    private String time;
    private boolean isCommend;
    private List<Integer> imgs;
    private Publisher publisher;

    public boolean isCommend() {
        return isCommend;
    }

    public void setCommend(boolean commend) {
        isCommend = commend;
    }

    public String getCommendNum() {
        return commendNum;
    }

    public void setCommendNum(String commendNum) {
        this.commendNum = commendNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(String messageNum) {
        this.messageNum = messageNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Integer> getImgs() {
        return imgs;
    }

    public void setImgs(List<Integer> imgs) {
        this.imgs = imgs;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public static class Publisher {
        private String id;
        private String nickName;
        private String avatar;
        private String level;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

}
