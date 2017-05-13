package com.burning.smile.schoolhelper.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by smile on 2017/3/14.
 */
public class FunkLab {

    public static FunkLab sFunkLab;
    private List<Funk> funks;


    public static FunkLab getInstance() {
        if (sFunkLab == null) {
            sFunkLab = new FunkLab();
        }
        return sFunkLab;
    }

    public FunkLab() {
        funks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Funk funk = new Funk();
            funk.setId(i + "");
            funk.setTitle("测试数据" + i);
            funk.setContent("测试数据测数据测试数据测试数据" + i);
            funk.setType(i % 2 == 0 ? "玩偶" : "书籍");
            funk.setTime(new SimpleDateFormat("MM-dd HH:mm").format(new Date()));
            Funk.Publisher publisher = new Funk.Publisher();
            publisher.setId(i + "");
            publisher.setNickName("BurNIng");
            funk.setPublisher(publisher);
            funks.add(funk);
        }
    }

    public List<Funk> getFunks() {
        return funks;
    }

}
