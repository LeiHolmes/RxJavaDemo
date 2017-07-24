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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Description:   学习了理论需要实践出真知
 * 本例演示使用RxJava查询过滤出手机安装的第三方应用并使用RecyclerView展示出来。
 * 使用Lambda表达式简化代码
 * author         xulei
 * Date           2017/7/24 10:19
 */
public class PracticeActivity extends AppCompatActivity {
    RecyclerView rvAppList;

    private AppInfoListAdapter adapter;
    private List<AppInfo> appInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        rvAppList = (RecyclerView) findViewById(R.id.rv_app_list);
        initRecyclerView();
        initData();
    }

    private void initData() {
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
                        throwable -> Toast.makeText(PracticeActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show(),
                        () -> adapter.notifyDataSetChanged()
                );
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvAppList.setLayoutManager(manager);
        adapter = new AppInfoListAdapter(this, appInfoList);
        rvAppList.setAdapter(adapter);
    }
}
