package com.holmeslei.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.holmeslei.rxjavademo.R;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Description:   组合转换符
 * author         xulei
 * Date           2017/7/17 16:21
 */
public class ComposeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        merge();
        concat();
        zip();
        startWith();
        switchOnNext();
        combineLatest();
        join();
    }

    /**
     * merge组合操作符
     * 将两个Observable发射的事件序列组合并成一个事件序列
     * 合并后发射的数据时无序的
     * mergeDelayError：合并过程中产生错误并不会中断合并，而是将产生的错误放到所有元素都合并完成之后再执行
     */
    private void merge() {
        //将一个发送字母的Observable与发送数字的Observable合并发射
        final String[] words = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        //字母Observable，每200ms发射一次
        Observable<String> wordSequence = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return words[position.intValue()];
                    }
                })
                .take(words.length);
        //数字Observable，每500ms发射一次
        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS).take(4);
        Observable.merge(wordSequence, numberSequence)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("rx_test", "merge：" + serializable.toString());
                    }
                });
        //Observable.merge(Observable[]) 还可以将多个Observable集合合并为一个事件序列

        //mergeDelayError操作符
        //字母Observable，每200ms发射一次，模拟过程中产生一个异常
        Observable<String> wordSequence1 = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        Long cache = position;
                        if (cache == 3) {
                            cache = cache / 0;
                        }
                        return words[position.intValue()];
                    }
                })
                .take(words.length);
        //数字Observable，每500ms发射一次
        Observable<Long> numberSequence1 = Observable.interval(500, TimeUnit.MILLISECONDS).take(4);
        Observable.mergeDelayError(wordSequence1, numberSequence1)
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("rx_test", "mergeDelayError：" + serializable.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("rx_test", "mergeDelayError：" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.e("rx_test", "mergeDelayError：onComplete");
                    }
                });
    }

    /**
     * concat组合操作符
     * 将多个Obserbavle发射的的数据进行合并发射
     * 合并后发射的数据是有序的
     */
    private void concat() {
        Observable<String> wordSequence = Observable.just("A", "B", "C", "D", "E");
        Observable<Integer> numberSequence = Observable.just(1, 2, 3, 4, 5);
        Observable<String> nameSequence = Observable.just("Sherlock", "Holmes", "Xu", "Lei");
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
     * 用于合并两个Observable发射的数据项，根据Func2函数生成一个新的值并发射出去
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
     * startWith组合操作符
     * 用于在源Observable发射的数据前插入数据
     */
    private void startWith() {
        Observable.just(4, 5, 6, 7)
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
     * switchOnNext(Observable<? extends Observable<? extends T>>组合操作符
     * 用来将一个发射多个小Observable的源Observable转化为一个Observable，然后发射这个多个小Observable所发射的数据
     */
    private void switchOnNext() {
        //若小Observable正在发射数据时，源Observable又发射了新的小Observable，
        //则前一个小Observable还未发射的数据会被抛弃，直接发射新的小Observable所发射的数据

        //每隔500ms产生一个Observable
        Observable<Observable<Long>> observable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        //每隔200毫秒产生一组数据（0,10,20,30,40)
                        return Observable.interval(200, TimeUnit.MILLISECONDS)
                                .map(new Func1<Long, Long>() {
                                    @Override
                                    public Long call(Long aLong) {
                                        return aLong * 10;
                                    }
                                }).take(5);
                    }
                }).take(2);
        Observable.switchOnNext(observable)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //由打印数据发现第一个小Observable打印到10则停止了发射数据，开始发射下一个Observable的数据了
                        Log.e("rx_test", "switchOnNext：" + aLong);
                    }
                });
    }

    /**
     * combineLatest(Observable, Observable, Func2)组合操作符
     * 用于将两个Observale最近发射的数据以Func2函数的规则进行组合并发射
     * 字母序列： . . . A . . B . . C . . D . . E . . F . . G . . H . . I
     * 数字序列： . . . . . 0 . . . . 1 . . . . 2 . . . . 3 . . . . 4
     * 发射结果： A0 B0 C0 C1 D1 E1 E2 F2 F3 G3 H3 H4 I4
     * <p>
     * 若更换两Observale顺序
     * 数字序列： . . . . . 0 . . . . 1 . . . . 2 . . . . 3 . . . . 4
     * 字母序列： . . . A . . B . . C . . D . . E . . F . . G . . H . . I
     * 发射结果： 0A 0B 0C 1C 1D 2D 2E 2F 3F 3G 3H 4H 4I
     */
    private void combineLatest() {
        //引用merge的例子
        final String[] words = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        Observable<String> wordSequence = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long position) {
                        return words[position.intValue()];
                    }
                })
                .take(words.length);
        Observable<Long> numberSequence = Observable.interval(500, TimeUnit.MILLISECONDS)
                .take(5);
        Observable.combineLatest(wordSequence, numberSequence,
                new Func2<String, Long, String>() {
                    @Override
                    public String call(String s, Long aLong) {
                        return s + aLong;
                    }
                })
                .subscribe(new Action1<Serializable>() {
                    @Override
                    public void call(Serializable serializable) {
                        Log.e("rx_test", "combineLatest：" + serializable.toString());
                    }
                });
    }

    /**
     * join(Observable, Func1, Func1, Func2)组合操作符
     * 用于ObservableA与ObservableB发射的数据进行排列组合
     * Observable：ObservableB
     * Func1：决定ObsrvableA发射出来的数据的有效期
     * Func1：决定ObsrvableB发射出来的数据的有效期
     * Func2：接收从ObservableA和ObservableB发射出来的数据，并将这两个数据组合后返回。
     */
    private void join() {
        //把第一个数据源A作为基座窗口，他根据自己的节奏不断发射数据元素，
        //第二个数据源B，每发射一个数据，我们都把它和第一个数据源A中已经发射的数据进行一对一匹配；
        //举例来说，如果某一时刻B发射了一个数据“B”,此时A已经发射了0，1，2，3共四个数据，
        //那么我们的合并操作就会把“B”依次与0,1,2,3配对，得到四组数据： [0, B][1, B] [2, B] [3, B]

        //产生字母的序列,周期为1000ms
        String[] words = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        Observable<String> observableA = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return words[aLong.intValue()];
                    }
                }).take(8);
        //产0,1,2,3,4,5,6,7的序列,延时500ms发射,周期为1000ms
        Observable<Long> observableB = Observable.interval(500, 1000, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong;
                    }
                }).take(words.length);
        //join
        observableA.join(observableB,
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        //ObservableA发射的数据有效期为600ms
                        return Observable.timer(600, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        //ObservableB发射的数据有效期为600ms
                        return Observable.timer(600, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String, Long, String>() {
                    @Override
                    public String call(String s, Long aLong) {
                        return s + aLong;
                    }
                }
        ).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e("rx_test", "join：" + s);
            }
        });


        //groupJoin：与join的不同之处在于第四个参数的的传入函数不一致，又包装了小的Observable
        observableA.groupJoin(observableB,
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        return Observable.timer(600, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.timer(600, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String, Observable<Long>, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String s, Observable<Long> longObservable) {
                        return longObservable.map(new Func1<Long, String>() {
                            @Override
                            public String call(Long aLong) {
                                return s + aLong;
                            }
                        });
                    }
                })
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.e("rx_test", "groupJoin：" + s);
                            }
                        });
                    }
                });
    }
}
