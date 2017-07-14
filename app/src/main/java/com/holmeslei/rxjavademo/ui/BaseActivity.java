package com.holmeslei.rxjavademo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.holmeslei.rxjavademo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
/**
 * Description:   RxJava基本实现
 * author         xulei
 * Date           2017/7/14 17:36
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        //基本操作
        initObserver();
        initObserverByChain();
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

    @OnClick(R.id.bt_transform_operator)
    public void onTransformClick(View view) {
        startActivity(new Intent(this, TransformActivity.class));
    }

    @OnClick(R.id.bt_filter_operator)
    public void onFilterClick(View view) {
        startActivity(new Intent(this, FilterActivity.class));
    }

    @OnClick(R.id.bt_compose_operator)
    public void onComposeClick(View view) {

    }
}
