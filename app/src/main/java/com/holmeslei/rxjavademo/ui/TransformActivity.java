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
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

public class TransformActivity extends AppCompatActivity {
    private Community[] communities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform);
        initData();
        //基本操作
        initObserver();
        initObserverByChain();
        //转换操作符
        map();
        flatMap();
//        concatMap();
//        flatMapIterable();
//        switchMap();
        scan();
        groupBy();
    }

    /**
     * 基本操作
     */
    private void initObserver() {
        //step1 创建观察者Observer,或者Action1：简洁就一个回调call()
        Observer observer = new Observer() {
            @Override
            public void onCompleted() { //不再有新的事件发出时回调
                Log.e("rx_test", "onCompleted");
            }

            @Override
            public void onError(Throwable e) { //事件处理出现异常时回调
                Log.e("rx_test", "onError");
            }

            @Override
            public void onNext(Object o) { //相当于普通观察者模式的update()
                Log.e("rx_test", "onNext:" + o.toString());
            }
        };

        //step2 创建被观察者Observable
        //第一种方式
        Observable<Object> observable1 = Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> subscriber) {
                //被观察者的数据操作更新
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext("xulei" + i);
                }
                subscriber.onCompleted();
            }
        });
        //第二种方式
        Observable observable2 = Observable.just("one", "two", "three");
        //第三种方式
        String[] parameters = {"one", "two", "three"};
        Observable observable3 = Observable.from(parameters);

        //step3 被观察者Observable订阅观察者Observer
        observable1.subscribe(observer);
    }

    /**
     * 链式编程
     */
    private void initObserverByChain() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            /**
             * call()方法中的参数Subscriber其实就是subscribe()方法中的观察者Observer。
             */
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                //被观察者的数据操作更新
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext("xulei" + i);
                }
                subscriber.onCompleted();
                //相当于调用下面Observer的onNext与onCompleted。因为Subscriber是Observer的抽象实现类
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                Log.e("rx_test", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("rx_test", "onError");
            }

            @Override
            public void onNext(Object o) {
                Log.e("rx_test", "onNext:" + o.toString());
            }
        });
    }

    /**
     * map转换操作符
     * 一对一转换
     */
    private void map() {
        //将一组Integer转换成String
        Observable.just(1, 2, 3, 4, 5)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "This is " + integer;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("rx_test", s);
                    }
                });
        //将Community集合转换为每一个Community并获取其name
        Observable.from(communities)
                .map(new Func1<Community, String>() {
                    @Override
                    public String call(Community community) {
                        return community.getCommunityName();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String communityName) {
                        Log.e("rx_test", "小区名称为：" + communityName);
                    }
                });
    }

    /**
     * flatmap转换操作符
     * 一对多转换
     */
    private void flatMap() {
        //将Community集合转换为每一套房子，获取其房子大小
        Observable.from(communities)
                .flatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                }).subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                Log.e("rx_test", "flatMap：房子大小为：" + house.getSize());
            }
        });
    }

    /**
     * concatMap转换操作符
     * 解决了flatMap()的交叉问题，能把发射的值连续在一起
     */
    private void concatMap() {
        //将Community集合转换为每一套房子，获取其房子大小
        Observable.from(communities)
                .concatMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                }).subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                Log.e("rx_test", "concatMap：房子大小为：" + house.getSize());
            }
        });
    }

    /**
     * flatMapIterable转换操作符
     * 与flatMap()相似
     * 不同之处在于flatMapIterable()转化多个Observable是使用Iterable作为源数据的
     */
    private void flatMapIterable() {
        //将Community集合转换为每一套房子，获取其房子大小
        Observable.from(communities)
                .flatMapIterable(new Func1<Community, Iterable<House>>() {
                    @Override
                    public Iterable<House> call(Community community) {
                        return community.getHouses();
                    }
                }).subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                Log.e("rx_test", "flatMapIterable：房子大小为：" + house.getSize());
            }
        });
    }

    /**
     * switchMap转换操作符
     * 与flatMap()相似
     * 每当源Observable发射新数据项(Observable)时，
     * 它将取消订阅并停止监视之前那个数据项产生的Observable，
     * 并开始监视当前发射的这一个。
     */
    private void switchMap() {
        //将Community集合转换为每一套房子，获取其房子大小
        Observable.from(communities)
                .switchMap(new Func1<Community, Observable<House>>() {
                    @Override
                    public Observable<House> call(Community community) {
                        return Observable.from(community.getHouses());
                    }
                }).subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                Log.e("rx_test", "switchMap：房子大小为：" + house.getSize());
            }
        });
    }

    /**
     * scan转换操作符
     * 对一个序列的数据应用一个函数，
     * 并将这个函数的结果发射出去作为下个数据应用合格函数时的第一个参数使用。
     */
    private void scan() {
        //例如：先输出1，再将1+2=3作为下个数据发出，3+3=6再作为下个数据发出，以此类推。
        Observable.just(1, 2, 3, 4, 5)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e("rx_test", "scan：" + integer);
            }
        });
    }

    /**
     * groupBy转换操作符
     * 排序分类
     */
    private void groupBy() {
        List<House> houseList = new ArrayList<>();
        houseList.add(new House(105.6f, 1, 200, "简单装修", "东方花园"));
        houseList.add(new House(144.8f, 3, 300, "豪华装修", "马德里春天"));
        houseList.add(new House(88.6f, 2, 170, "简单装修", "东方花园"));
        houseList.add(new House(123.4f, 1, 250, "简单装修", "帝豪家园"));
        houseList.add(new House(144.8f, 6, 350, "豪华装修", "马德里春天"));
        houseList.add(new House(105.6f, 4, 210, "普通装修", "东方花园"));
        houseList.add(new House(188.7f, 3, 400, "精致装修", "帝豪家园"));
        houseList.add(new House(88.6f, 2, 180, "普通装修", "东方花园"));
        //根据小区名称进行排序
        Observable<GroupedObservable<String, House>> groupedObservableObservable = Observable
                .from(houseList)
                .groupBy(new Func1<House, String>() {
                    @Override
                    public String call(House house) {
                        return house.getCommunityName();
                    }
                });
        Observable.concat(groupedObservableObservable)
                .subscribe(new Action1<House>() {
                    @Override
                    public void call(House house) {
                        Log.e("rx_test", "groupBy：" + "小区：" + house.getCommunityName() + "，大小：" + house.getSize());
                    }
                });
    }

    /**
     * 添加假数据
     */
    private void initData() {
        List<House> houses1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses1.add(new House(105.6f, i, 200, "简单装修", "东方花园"));
            } else {
                houses1.add(new House(144.8f, i, 520, "豪华装修", "东方花园"));
            }
        }

        List<House> houses2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses2.add(new House(88.6f, i, 166, "中等装修", "马德里春天"));
            } else {
                houses2.add(new House(123.4f, i, 321, "精致装修", "马德里春天"));
            }
        }

        List<House> houses3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses3.add(new House(188.7f, i, 724, "豪华装修", "帝豪家园"));
            } else {
                houses3.add(new House(56.4f, i, 101, "普通装修", "帝豪家园"));
            }
        }

        communities = new Community[]{new Community("东方花园", houses1),
                new Community("马德里春天", houses2), new Community("帝豪家园", houses3)};
    }
}