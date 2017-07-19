package com.holmeslei.rxjavademo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.holmeslei.rxjavademo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Description:   RxJava创建操作符
 * author         xulei
 * Date           2017/7/14 17:36
 */
public class CreatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
        create();
        createByChain();
        just();
        from();
        range();
        defer();
        interval();
        timer();
        delay();
    }

    /**
     * create创建操作符
     */
    private void create() {
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
     * create创建操作符链式编程
     */
    private void createByChain() {
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
        //.subscribe()中的参数也可用ActionX替代，好处是只有一个回调方法call()。
    }

    /**
     * just创建操作符
     * 将某个对象转化为Observable对象，并且将其发射出去
     * 可为一个或多个数字，字符串。也可为集合，数组，Iterate对象等。
     */
    private void just() {
        Observable.just(1, 2, 3, 4, 5, 6).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e("rx_test", "just:数字：" + integer);
                //数字或者字符串都是单个发射多次
            }
        });

        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");
        stringList.add("Ha");
        stringList.add("RxJava");
        Observable.just(stringList).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                Log.e("rx_test", "just:集合：" + strings.toString());
                //集合或数组是直接发射集合整体，不会拆分
            }
        });
    }

    /**
     * from创建操作符
     * 将某个对象转化为Observable对象，并且将其发射出去
     * 不同于just，他接收集合或数组，可将集合数组遍历之后拆分发送
     */
    private void from() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");
        stringList.add("Ha");
        stringList.add("RxJava");
        Observable.from(stringList).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("rx_test", "from：" + s);
            }
        });
    }

    /**
     * range(int start, int count)创建操作符
     * 根据初始值start，与数量count，发射count次以start为基数依次增加的值
     */
    private void range() {
        //输出结果 5，6，7，8，9
        Observable.range(5, 5).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e("rx_test", "range：" + integer);
            }
        });
    }

    /**
     * defer创建操作符
     * 只有当Subscriber订阅的时候才会创建一个新的Observable
     * 可确保Observable中的数据都是最新的
     */
    private void defer() {
        //这里与just对比来观察有何不同
        Action1<String> action1 = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("rx_test", s);
            }
        };

        //defer
        Observable<String> deferObservable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                Object o = new Object();
                return Observable.just("defer：hashCode：" + o.hashCode());
            }
        });
        deferObservable.subscribe(action1);
        deferObservable.subscribe(action1);
        deferObservable.subscribe(action1);

        //just
        Observable<String> justObservable = Observable.just("just：hashCode：" + new Object().hashCode());
        justObservable.subscribe(action1);
        justObservable.subscribe(action1);
        justObservable.subscribe(action1);

        //由输出结果可看出
        //deferObservable每次订阅都生成的新的Observable来发射数据，
        //而just只在初始化Observable的时候生成一次
    }

    /**
     * interval创建操作符
     * 创建一个Observabel并每隔一段时间发射一个由0开始增加的数字
     * 周期发射
     * 注意：此Observabel是运行在新的线程，所以更新UI需要在主线程中订阅
     */
    private void interval() {
        //每隔100ms发射一个数字,从0自增
        Observable.interval(100, TimeUnit.MILLISECONDS) //单位为毫秒
                .observeOn(AndroidSchedulers.mainThread())
                .take(5) //发射5次，take为过滤操作符，详细参看FilterActivity
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e("rx_test", "interval：" + aLong);
                    }
                });

    }

    /**
     * timer创建操作符
     * 创建一个Observable并隔一段时间后发射一个特殊的值
     * 仅发射一次
     * 注意：此Observabel是运行在新的线程，所以更新UI需要在主线程中订阅
     */
    private void timer() {
        //隔1s后发射一个数字
        Observable.timer(1, TimeUnit.SECONDS) //单位为秒
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.e("rx_test", "timer：" + aLong);
                    }
                });
    }

    /**
     * delay创建操作符
     * 用于在事件流中，可延迟发送事件流中的某一次发送
     */
    private void delay() {
        Observable.just(1, 2, 3)
                .delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("rx_test", "delay：" + integer);
                    }
                });
    }

    /**
     * 转换操作符跳转
     */
    @OnClick(R.id.bt_transform_operator)
    public void onTransformClick(View view) {
        startActivity(new Intent(this, TransformActivity.class));
    }

    /**
     * 过滤操作符跳转
     */
    @OnClick(R.id.bt_filter_operator)
    public void onFilterClick(View view) {
        startActivity(new Intent(this, FilterActivity.class));
    }

    /**
     * 组合操作符跳转
     */
    @OnClick(R.id.bt_compose_operator)
    public void onComposeClick(View view) {
        startActivity(new Intent(this, ComposeActivity.class));
    }
}
