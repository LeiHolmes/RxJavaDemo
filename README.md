# RxJavaDemo
### 前言  
　　最近在学习RxJava的过程中,越发感受到这个框架的强大，对于提升开发效率，降低维护成本有很大的作用。有必要强行安利一波，一起来看看RxJava简介，基本概念，原理与使用，本篇主要介绍RxJava的简介。
　　<!--more-->
### 响应式编程
　　RxJava是ReactiveX中使用Java语言实现的版本，那什么是ReactiveX呢？ReactiveX就是一种新兴的编程模式--响应式编程，定义为一种基于异步数据流概念的编程模式，可分解为"观察者模式+迭代器模式+函数式编程"。
### 扩展的观察者模式
　　有关观察者模式的概念不懂得童鞋可自行google，这里就不再赘述。RxJava扩展了观察者模式，通过使用可观察的对象序列流来表述一系列事件，订阅者进行占点观察并对序列流做出反应（或持久化或输出显示等等）；借鉴迭代器模式，对多个对象序列进行迭代输出，订阅者可以依次处理不同的对象序列；使用函数式编程思想，简化繁琐的逻辑代码。  
　　而RxJava的核心便是被观察者Observables与观察者Observer，由Observables发出一系列的事件，Observer通过subscribe()方法进行订阅接收事件并进行处理，类似观察者模式，不同之处在于，若没有观察者，被观察者是不会发出任何事件的。  
　　所以说，**RxJava本质上是一个异步操作库，是一个能让你用极其简洁的逻辑去处理繁琐复杂任务的异步事件库。**
### 例子
　　概念理解起来未免晦涩难懂，我们来看一个例子：  
　　有这样一个需求：开发一个类似房天下的App，某功能需要检索出某一片区所有住宅小区中购房总价<200W的房源并展示出来。先看原始实现方式：

```java
 new Thread(new Runnable() {
    @Override
    public void run() {
        //获取要查询的小区集合
        List<Community> communities = getCommunities();
        for (Community community : communities) {
            //获取小区中的房源集合
            List<House> houses = community.getHouses();
            for (House house : houses) {
                if (house.getPrice() < 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //显示查询出来的房源信息
                            ShowSearchedHousesMessage();
                        }
                    });
                }
            }
        }
    }
}).start();
```
　　RxJava实现方式：
```java
//获取要查询的小区集合
List<Community> communities = getCommunities();
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
                return house.getPrice() < 200;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<House>() {
            @Override
            public void call(House house) {
                //显示查询出来的房源信息
                ShowSearchedHousesMessage();
            }
        });
```
　　RxJava的实现方式看上去更复杂了，但是它的逻辑很清晰简洁，修改起来很方便，后期维护起来也能提高工作效率。再来看下配合Lambda表达式：
```java
//获取要查询的小区集合
List<Community> communities = getCommunities();
Observable.from(communities)
        .flatMap(community -> Observable.from(community.getHouses())
        .filter(house -> house.getPrice() < 200)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(house -> ShowSearchedHousesMessage());
```
　　搭配上Lambda表达式，代码的简洁度是不是更上一层楼呢？不懂Lambda表达式的童鞋可以跳转到我写的另一篇讲解Lambda表达式的博文：
　　[Android开发之Lambda表达式基本语法与应用](http://xulei.tech/2017/07/24/Android开发之Lambda表达式基本语法与应用/)
### 总结
　　到此，本篇关于RxJava的简介就介绍完毕了，本篇的目的主要是给大家引入一个RxJava的概念，以便后期更轻松的理解RxJava的原理与运作。下一篇我们再来一起详细研究RxJava的原理及如何使用。  
　　技术渣一枚，有写的不对的地方欢迎大神们留言指正，有什么疑惑或者建议也可以在Issues中提出，我会及时回复。

