package com.holmeslei.rxjavademo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.holmeslei.rxjavademo.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
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
        reactivePull();
        controlByFilter();
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
     */
    private void controlByFilter() {
        //使用sample过滤操作符，每隔200ms取里时间点最近的事件
        Observable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .sample(200, TimeUnit.MILLISECONDS)
                .subscribe((Long aLong) -> Log.e("rx_test", "controlByFilter：sample：" + aLong));
    }
}
