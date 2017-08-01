package com.holmeslei.rxjavademo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.holmeslei.rxjavademo.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
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
        simulateBackPressure();
    }

    /**
     * 模拟背压问题
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
                    Log.i("back_pressure", aLong + "");
                });
    }
}
