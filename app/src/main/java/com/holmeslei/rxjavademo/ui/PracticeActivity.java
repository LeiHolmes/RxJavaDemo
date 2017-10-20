package com.holmeslei.rxjavademo.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.adapter.AppInfoListAdapter;
import com.holmeslei.rxjavademo.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Description:   学习了理论需要实践出真知
 * 本例演示使用RxJava查询过滤出手机安装的第三方应用并使用RecyclerView展示出来。
 * 使用Lambda表达式简化代码
 * author         xulei
 * Date           2017/7/24 10:19
 */
public class PracticeActivity extends AppCompatActivity {
    @BindView(R.id.rv_app_list)
    RecyclerView rvAppList;

    private AppInfoListAdapter adapter;
    private List<AppInfo> appInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        ButterKnife.bind(this);
        initRecyclerView();
        initData();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvAppList.setLayoutManager(manager);
        adapter = new AppInfoListAdapter(this, appInfoList);
        rvAppList.setAdapter(adapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        final PackageManager pm = getPackageManager();
        //获取所有应用信息集合
        List<ApplicationInfo> infoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Observable.from(infoList)
                //过滤出已安装的第三方应用
                .filter(new Func1<ApplicationInfo, Boolean>() {
                    @Override
                    public Boolean call(ApplicationInfo applicationInfo) {
                        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0;
                    }
                })
                //转换为自定义的AppInfo类
                .map(new Func1<ApplicationInfo, AppInfo>() {
                    @Override
                    public AppInfo call(ApplicationInfo applicationInfo) {
                        AppInfo appInfo = new AppInfo();
                        appInfo.setAppIcon(applicationInfo.loadIcon(pm));
                        appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                        return appInfo;
                    }
                })
                //Observable被观察者执行在io线程
                .subscribeOn(Schedulers.io())
                //Observer观察者执行在AndroidUI线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        //更新列表UI
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //显示错误信息
                        Toast.makeText(PracticeActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        //添加第三方应用数据到集合
                        appInfoList.add(appInfo);
                    }
                });
    }

    /**
     * 初始化数据
     * Lambda表达式简化
     */
    private void initDataWithLambda() {
        final PackageManager pm = getPackageManager();
        //获取所有应用信息集合
        List<ApplicationInfo> infoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Observable.from(infoList)
                .filter(applicationInfo -> (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
                .map(applicationInfo -> {
                    //转换为自定义的AppInfo类
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppIcon(applicationInfo.loadIcon(pm));
                    appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                    return appInfo;
                })
                .subscribeOn(Schedulers.io()) //io线程
                .observeOn(AndroidSchedulers.mainThread()) //AndroidUI线程
                .subscribe(
                        appInfo -> appInfoList.add(appInfo),
                        throwable -> Toast.makeText(PracticeActivity.this, throwable.getMessage(),
                                Toast.LENGTH_LONG).show(),
                        () -> adapter.notifyDataSetChanged()
                );
    }
}
