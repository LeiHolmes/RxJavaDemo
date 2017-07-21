package com.holmeslei.rxjavademo.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.adapter.AppInfoListAdapter;
import com.holmeslei.rxjavademo.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    private void initData() {
        final PackageManager pm = getPackageManager();
        //获取所有应用信息集合
        List<ApplicationInfo> infoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Observable.from(infoList)
                .filter(new Func1<ApplicationInfo, Boolean>() {
                    @Override
                    public Boolean call(ApplicationInfo applicationInfo) {
                        //过滤系统应用
                        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0;
                    }
                })
                .map(new Func1<ApplicationInfo, AppInfo>() {
                    @Override
                    public AppInfo call(ApplicationInfo applicationInfo) {
                        //转换为自定义的AppInfo类
                        AppInfo appInfo = new AppInfo();
                        appInfo.setAppIcon(applicationInfo.loadIcon(pm));
                        appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                        return appInfo;
                    }
                })
                .subscribeOn(Schedulers.io()) //io线程
                .observeOn(AndroidSchedulers.mainThread()) //AndroidUI线程
                .subscribe(new Subscriber<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        appInfoList.add(appInfo);
                    }
                });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvAppList.setLayoutManager(manager);
        adapter = new AppInfoListAdapter(this, appInfoList);
        rvAppList.setAdapter(adapter);
    }
}
