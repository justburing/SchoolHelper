package com.burning.smile.schoolhelper.data;

import com.burning.smile.schoolhelper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by smile on 2017/3/14.
 */
public class ForumLab {

    public static ForumLab sForumLab;
    private List<Forum> forums;


    public static ForumLab getInstance() {
        if (sForumLab == null) {
            sForumLab = new ForumLab();
        }
        return sForumLab;
    }

    public ForumLab() {
        forums = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Forum forum = new Forum();
            forum.setId(i + "");
            forum.setTitle("测试数据测数据测试数据测试数据" + i);
            forum.setCommendNum((i * 2) + "");
            forum.setMessageNum((i * 3) + "");
            forum.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new Date()));
            if (i % 2 == 0) {
                forum.setCommend(false);
            } else {
                forum.setCommend(true);
            }
            List<Integer> imgs = new ArrayList<>();
            if (i % 2 == 0) {
                if (i == 0) {

                } else {
                    imgs.add(R.mipmap.ic_test);
                    imgs.add(R.mipmap.ic_test);
                    imgs.add(R.mipmap.ic_test);
                }
            } else {
                if (i < 13) {
                    imgs.add(R.mipmap.ic_test);
                } else if (i == 17) {

                } else {
                    imgs.add(R.mipmap.ic_test);
                    imgs.add(R.mipmap.ic_test);
                }
            }
            forum.setImgs(imgs);
            Forum.Publisher publisher = new Forum.Publisher();
            publisher.setId(i + "");
            publisher.setNickName("BurNIng");
            publisher.setLevel((i + 1) + "");
            forum.setPublisher(publisher);
            forums.add(forum);
        }
    }

    public List<Forum> getForums() {
        return forums;
    }

    public void addForum(Forum forum) {
        forums.add(forum);
    }

}
