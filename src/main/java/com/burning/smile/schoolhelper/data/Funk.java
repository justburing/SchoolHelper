package com.burning.smile.schoolhelper.data;

import java.io.Serializable;

/**
 * Created by smile on 2017/3/14.
 */
public class Funk implements Serializable {

    private String id;
    private String type;
    private String time;
    private String title;
    private String content;
    private String img;
    private Publisher publisher;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
