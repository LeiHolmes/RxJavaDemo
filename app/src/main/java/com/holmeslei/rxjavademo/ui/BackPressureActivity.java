package com.holmeslei.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.holmeslei.rxjavademo.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Description:   背压问题
 * 背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略
 * 简而言之，背压是流速控制的一种策略
 * author         xulei
 * Date           2017/7/29 16:20
 */
public class BackPressureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_pressure);
//        simulateBackPressure();
//        reactivePull();
//        controlByFilter();
//        controlByCache();
//        controlBySpecialOperator();
    }

    /**
     * 模拟背压问题
     * 由于被观察者发射数据流速远远快于观察者处理数据的速度导致
     * 抛出rx.exceptions.MissingBackpressureException
     * 两个前提
     * 1.异步环境，被观察者和观察者需处在不同的线程环境中。
     * 2.背压并不是一个像flatMap一样可以在程序中直接使用的操作符，他只是一种控制事件流速的策略。
     */
    private void simulateBackPressure() {
        //被观察者每过1ms发射一个时间
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe((aLong) -> { 
                    //观察者每过1000ms处理一个事件
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("rx_test", "back_pressure：" + aLong);
                });
    }

    /**
     * 响应式拉取
     * 通常的RxJava都是被观察者主动发射数据给观察者，观察者是被动接收的
     * 而响应式拉取是相反的，观察者主动去被观察者那里拉取数据，而被观察者是被动等待通知再发射数据
     * RxJava1中部分Cold Observables才支持背压策略，例如range支持，而interval不支持
     */
    private void reactivePull() {
        //range操作符本身支持背压策略，发送事件的速度可被控制
        Observable.range(1, 10000)
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        //一定要在onStart中通知被观察者先发送一个事件
                        request(1);
                    }

                    @Override
                    public void onCompleted() {
                        Log.e("rx_test", "reactivePull：onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("rx_test", "reactivePull：onError：" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer n) {
                        try {
                            Thread.sleep(1000);
                            Log.e("rx_test", "reactivePull：onNext：" + n);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //处理完毕之后，在通知被观察者发送下一个事件
                        request(1);
                    }
                });
    }

    /**
     * 不支持背压的Observevable做流速控制
     * 通过过滤抛弃大部分事件
     * 可使用sample，throttleFirst等操作符
     */
    private void controlByFilter() {
        //使用sample过滤操作符，每隔200ms取里时间点最近的事件发送
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .sample(200, TimeUnit.MILLISECONDS)
                .subscribe((Long aLong) -> Log.e("rx_test", "controlByFilter：sample：" + aLong));
    }

    /**
     * 通过缓存一部分被观察者发来的数据再读来控制流速
     * 可使用buffer，window等操作符
     */
    private void controlByCache() {
        //使用buffer过滤操作符，将100ms内的事件打包为list发送
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .buffer(100, TimeUnit.MILLISECONDS)
                .subscribe((longs) -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("rx_test", "controlByCache：buffer：" + longs.size());
                });
    }

    /**
     * 通过两种特殊操作符来控制流速
     * onBackpressurebuffer：把observable发送出来的事件做缓存，当request方法被调用的时候，给下层流发送一个item(如果给这个缓存区设置了大小，那么超过了这个大小就会抛出异常)。
     * onBackpressureDrop：将observable发送的事件抛弃掉，直到subscriber再次调用request(n)方法的时候，再发送这之后的n个事件。
     */
    private void controlBySpecialOperator() {
        //使用onBackpressureDrop可是不支持背压的操作符也可响应下游观察者的request(n)
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.e("rx_test", "controlBySpecialOperator：" + "onStart");
                        request(1);
                    }

                    @Override
                    public void onCompleted() {
                        Log.e("rx_test", "controlBySpecialOperator：" + "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e("rx_test", "controlBySpecialOperator：" + aLong);
                        try {
                            Thread.sleep(500);
                            request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        //出现0-15这样连贯的数据，是因为observeOn操作符内部有一个长度为16的缓存区，它会先将请求前16个事件缓存起来
    }
}
