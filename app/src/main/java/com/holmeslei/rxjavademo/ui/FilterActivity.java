package com.holmeslei.rxjavademo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.model.Community;
import com.holmeslei.rxjavademo.model.House;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Description:   RxJava过滤操作符
 * author         xulei
 * Date           2017/7/14 17:37
 */
public class FilterActivity extends AppCompatActivity {
    private List<Community> communities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initData();
        filter();
    }

    private void filter() {
        //结合flatmap，过滤出各小区中房源大小大于120平的房子
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses())
                                .filter(new Func1<House, Boolean>() {
                                    @Override
                                    public Boolean call(House house) {
                                        return house.getSize() > 120f;
                                    }
                                });
                    }
                }).subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                Log.e("rx_test", "filter：大于120平的房子：" + house.getCommunityName() + "小区，大小：" + house.getSize());
            }
        });
    }

    /**
     * 添加假数据
     */
    private void initData() {
        communities = new ArrayList<>();
        List<House> houses1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses1.add(new House(105.6f, i, 200, "简单装修", "东方花园"));
            } else {
                houses1.add(new House(144.8f, i, 520, "豪华装修", "东方花园"));
            }
        }
        communities.add(new Community("东方花园", houses1));

        List<House> houses2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses2.add(new House(88.6f, i, 166, "中等装修", "马德里春天"));
            } else {
                houses2.add(new House(123.4f, i, 321, "精致装修", "马德里春天"));
            }
        }
        communities.add(new Community("马德里春天", houses2));

        List<House> houses3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses3.add(new House(188.7f, i, 724, "豪华装修", "帝豪家园"));
            } else {
                houses3.add(new House(56.4f, i, 101, "普通装修", "帝豪家园"));
            }
        }
        communities.add(new Community("帝豪家园", houses3));
    }
}
