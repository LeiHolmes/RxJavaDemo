package com.holmeslei.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.model.Community;
import com.holmeslei.rxjavademo.model.House;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
        take();
        skip();
        debounce();
        distinct();
        elementAt();
        first();
        last();
    }

    /**
     * filter过滤操作符
     * 过滤序列中不想要的值，返回满足条件的值
     */
    private void filter() {
        //结合flatmap，过滤出各小区中房源大小大于120平的房子
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                })
                .filter(new Func1<House, Boolean>() {
                    @Override
                    public Boolean call(House house) {
                        return house.getSize() > 120f;
                    }
                })
                .subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "filter：大于120平的房子：" + house.getCommunityName() + "小区，大小：" + house.getSize());
                    }
                });
    }

    /**
     * take过滤转换符
     * take：获取序列前n个元素并发射
     * takeLast：获取序列后n个元素并发射
     * takeUntil(Observable)：订阅并开始发射原始Observable，同时监视我们提供的第二个Observable。
     * 如果第二个Observable发射了一项数据或者发射了一个终止通知，takeUntil()返回的Observable会停止发射原始Observable并终止
     * takeUntil(Func1)：通过Func1中的call方法来判断是否需要终止当前Observable发射数据
     * takeWhile：类似于takeUntil(Func1)，当Observable发射的数据不满足条件时中止Observable的发射。
     */
    private void take() {
        //take：获取前两个小区名
        Observable.from(communities)
                .take(2)
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "take：前两个小区：" + community.getCommunityName());
                    }
                });
        //takeLast：获取后两个小区名
        Observable.from(communities)
                .takeLast(2)
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "takeLast：后两个小区：" + community.getCommunityName());
                    }
                });
        //takeUntil(Observable)
        Observable<Long> observableA = Observable.interval(300, TimeUnit.MILLISECONDS);
        Observable<Long> observableB = Observable.interval(800, TimeUnit.MILLISECONDS);
        observableA.takeUntil(observableB)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e("rx_test", "takeUntil(Observable)：" + "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("rx_test", "takeUntil(Observable)：onError：" + e.getMessage());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("rx_test", "takeUntil(Observable)：onNext：" + aLong);
                    }
                });
        //takeUntil(Func1)：与flatmap结合过滤直到房价大于500时中断当前小区Observable发射House
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                })
                .takeUntil(new Func1<House, Boolean>() {
                    @Override
                    public Boolean call(House house) {
                        return house.getPrice() > 500;
                    }
                })
                .subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "takeUntil(Func1)：大于500时中断发射：" + house.getCommunityName() + "小区，房价：" + house.getPrice());
                    }
                });
        //takeWhile：当发射的数据等于3时中止发射
        Observable.just(1, 2, 3, 4, 5)
                .takeWhile(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer != 3;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("rx_test", "takeWhile：" + integer);
                    }
                });
    }

    /**
     * skip过滤操作符
     * skip：忽略发射Observable的前n项数据
     * skipLast：忽略发射Observable的后n项数据
     */
    private void skip() {
        //忽略前两个小区数据
        Observable.from(communities)
                .skip(2)
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "skip：忽略前两个小区：" + community.getCommunityName());
                    }
                });
        //忽略后两个小区数据
        Observable.from(communities)
                .skipLast(2)
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "skip：忽略后两个小区：" + community.getCommunityName());
                    }
                });
    }

    /**
     * debounce过滤操作符
     * debounce(long, TimeUnit)：过滤由Observable发射的速率过快的数据，起到限流的作用
     * debounce(Func1)：根据Func1的call方法中的函数来过滤
     */
    private void debounce() {
        //debounce(long, TimeUnit)：可结合RxBinding(Jake Wharton使用RxJava封装的Android UI组件)使用，防止button重复点击。
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    for (int i = 1; i < 10; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(i * 100); //分别延时100，200，300，400，500......900ms发射数据
                    }
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e("rx_test", "debounce：" + "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e("rx_test", "debounce：" + integer);
                        //由输出结果可以看出由于设定限流时间为500ms，所以1-4并没有被发射而是被过滤了
                    }
                });
        //debounce(Func1)：Func1中的中的call方法返回了一个临时的Observable，如果原始的Observable在发射一个新的数据时，
        //上一个数据根据Func1的call方法生成的临时Observable还没结束，那么上一个数据就会被过滤掉
    }

    /**
     * distinct过滤操作符
     * distinct：只允许还没有发射过的数据通过，达到去除序列中重复项的作用
     * distinctUntilChanged：当前数据项与前一项是否相同来去重
     */
    private void distinct() {
        //去除重复数字
        Observable.just(1, 2, 2, 3, 4, 5, 6, 6, 6, 7)
                .distinct()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("rx_test", "distinct：去重：" + integer);
                    }
                });
        //根据某属性去重，去除各小区大小相同的房源
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                })
                .distinct(new Func1<House, Float>() {
                    @Override
                    public Float call(House house) {
                        return house.getSize();
                    }
                }).
                subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "distinct(Func1)：去重：" + house.getCommunityName() + "小区，大小：" + house.getSize());
                    }
                });

        //向前去重复数据
        Observable.just(1, 2, 2, 3, 4, 2, 3, 5, 5)
                .distinctUntilChanged()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("rx_test", "distinctUntilChanged：向前去重：" + integer);
                    }
                });
        //根据某属性向前去重，去除各小区名相同的房源
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses())
                                .distinctUntilChanged(new Func1<House, String>() {
                                    @Override
                                    public String call(House house) {
                                        return house.getCommunityName();
                                    }
                                });
                    }
                })
                .subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "distinctUntilChanged(Func1)：向前去重：" + house.getCommunityName() + "小区，大小：" + house.getSize());
                    }
                });
    }

    /**
     * elementAt过滤操作符
     * 获取Observable的第n项数据并当做唯一数据发射
     */
    private void elementAt() {
        Observable.from(communities)
                .elementAt(1)
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "elementAt：第二个小区：" + community.getCommunityName());
                    }
                });
    }

    /**
     * first过滤操作符
     * 只发射序列中的第一个数据项
     */
    private void first() {
        //发送第一个数据项
        Observable.from(communities)
                .first()
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "first：" + community.getCommunityName());
                    }
                });
        //发送符合条件的第一个数据项：过滤第一个名为马德里春天的小区
        Observable.from(communities)
                .first(new Func1<Community, Boolean>() {
                    @Override
                    public Boolean call(Community community) {
                        return "马德里春天".equals(community.getCommunityName());
                    }
                })
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "first(Func1)：" + community.getCommunityName());
                    }
                });
    }

    /**
     * last过滤操作符
     * 只发射序列中的最后一个数据项。
     */
    private void last() {
        //发送最后一个数据项
        Observable.from(communities)
                .last()
                .subscribe(new Action1<Community>() {
                    @Override
                    public void call(Community community) {
                        Log.e("rx_test", "last：" + community.getCommunityName());
                    }
                });
        //发送符合条件的最后一个数据项：过滤最后一个小区名为马德里春天的房源
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                })
                .last(new Func1<House, Boolean>() {
                    @Override
                    public Boolean call(House house) {
                        return "马德里春天".equals(house.getCommunityName());
                    }
                })
                .subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "last：" + house.getCommunityName() + "小区，大小：" + house.getSize());
                    }
                });
    }

    /**
     * 添加假数据
     */
    private void initData() {
        communities = new ArrayList<>();
        List<House> houses1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                houses1.add(new House(105.6f, i, 200, "简单装修", "东方花园"));
            } else {
                houses1.add(new House(144.8f, i, 520, "豪华装修", "东方花园"));
            }
        }
        communities.add(new Community("东方花园", houses1));

        List<House> houses2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                houses2.add(new House(88.6f, i, 166, "中等装修", "马德里春天"));
            } else {
                houses2.add(new House(123.4f, i, 321, "精致装修", "马德里春天"));
            }
        }
        communities.add(new Community("马德里春天", houses2));

        List<House> houses3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                houses3.add(new House(188.7f, i, 724, "豪华装修", "帝豪家园"));
            } else {
                houses3.add(new House(56.4f, i, 101, "普通装修", "帝豪家园"));
            }
        }
        communities.add(new Community("帝豪家园", houses3));
    }
}
