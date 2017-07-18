package com.holmeslei.rxjavademo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.model.Community;
import com.holmeslei.rxjavademo.model.House;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Description:   组合转换符
 * author         xulei
 * Date           2017/7/17 16:21
 */
public class ComposeActivity extends AppCompatActivity {
    private List<Community> communities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        initData();
        merge();
        startWith();
        concat();
        zip(); 
    }

    /**
     * merge组合操作符
     * 将两个Observable发射的事件序列组合并成一个事件序列
     * 合并后发射的数据时无序的
     */
    private void merge() {
        //将一个发送字母的Observable与发送数字的Observable合并发射
        final String[] words = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        //字母Observable，每300ms发射一次
        Observable<String> wordSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return words[position.intValue()];
                    }
                })
                .take(words.length);
        //数字Observable，每500ms发射一次
        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(5);
        Observable.merge(wordSequence, numberSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("rx_test", "merge：" + serializable.toString());
                    }
                });
        //Observable.merge(Observable[]) 还可以将多个Observable集合合并为一个事件序列
    }

    /**
     * startWith组合操作符
     * 用于在源Observable发射的数据前插入数据
     */
    private void startWith() {
        Observable.just(4, 5, 6, 7, 8, 9)
                .startWith(1, 2, 3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.e("rx_test", "startWith：" + integer);
                    }
                });
        //startWith(Iterable<T>)：可在序列发射前插入Iterable数据
        //startWith(Observable<T>)：可在序列发射前插入另一Observable发射的数据
        Observable<String> baseObservable = Observable.just("A", "B", "C", "D", "E");
        Observable<String> anOtherObservable = Observable.just("sherlock", "xu");
        baseObservable.startWith(anOtherObservable)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e("rx_test", "startWith(Observable)：" + s);
                    }
                });
    }

    /**
     * concat组合操作符
     * 将多个obserbavle发射的的数据进行合并发射
     * 合并后发射的数据时有序的
     */
    private void concat() {
        Observable<String> wordSequence = Observable.just("A", "B", "C", "D", "E");
        Observable<Integer> numberSequence = Observable.just(1, 2, 3, 4, 5);
        Observable<String> nameSequence = Observable.just("Sherlock", "Richard", "Xu");
        Observable.concat(wordSequence, numberSequence, nameSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("rx_test", "concat：" + serializable.toString());
                    }
                });
    }

    /**
     * zip(Observable, Observable, Func2)组合操作符
     * 合并两个Observable发射的数据项，根据Func2函数生成一个新的值并发射出去
     * 若其中一个Observable发射完毕或出现异常，另一个也随之停止发射
     */
    private void zip() {
        Observable<String> wordSequence = Observable.just("A", "B", "C", "D", "E");
        Observable<Integer> numberSequence = Observable.just(1, 2, 3, 4, 5, 6);
        Observable.zip(wordSequence, numberSequence, new Func2<String, Integer, String>() {
            @Override
            public String call(String s, Integer integer) {
                return s + integer;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("rx_test", "zip：" + s);
                //由打印结果可看出numberSequence观测序列最后的6并没有发射出来，由于wordSequence观测序列已发射完所有数据，所以组合序列也停止发射数据了
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
