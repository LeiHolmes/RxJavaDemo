package com.holmeslei.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.holmeslei.rxjavademo.model.Community;
import com.holmeslei.rxjavademo.model.House;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {
    private Community[] communities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        //基本操作
        initObserver();
        initObserverByChain();
        //异步

        //转换操作符
        mapOperator();
        flatmapOperator();
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
     */
    private void mapOperator() {
        //将一组Integer转换成String，一对一转换
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
     */
    private void flatmapOperator() {
        //将Community集合转换为每一套房子，获取其名字
    }

    private void initData() {
        List<House> houses1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses1.add(new House(105.6f, i, 200, "简单装修"));
            } else {
                houses1.add(new House(144.8f, i, 520, "豪华装修"));
            }
        }

        List<House> houses2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses2.add(new House(88.6f, i, 166, "中等装修"));
            } else {
                houses2.add(new House(123.4f, i, 321, "精致装修"));
            }
        }

        List<House> houses3 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                houses3.add(new House(188.7f, i, 724, "豪华装修"));
            } else {
                houses3.add(new House(56.4f, i, 101, "普通装修"));
            }
        }

        communities = new Community[]{new Community("东方花园", houses1),
                new Community("马德里春天", houses2), new Community("帝豪家园", houses3)};
    }
}
