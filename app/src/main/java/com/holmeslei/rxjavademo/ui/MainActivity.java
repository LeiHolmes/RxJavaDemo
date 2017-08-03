package com.holmeslei.rxjavademo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.holmeslei.rxjavademo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description:   RxJava1Demo讲解
 * 整体学习顺序:    CreatActivity-->TransformActivity-->FilterActivity-->ComposeActivity-->
 * PracticeActivity-->BackPressureActivity
 * author         xulei
 * Date           2017/8/3 11:41
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_create_operator, R.id.bt_transform_operator, R.id.bt_filter_operator, R.id.bt_compose_operator,
            R.id.bt_practice, R.id.bt_back_pressure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_create_operator: //创建操作符
                startActivity(new Intent(this, CreatActivity.class));
                break;
            case R.id.bt_transform_operator: //转换操作符
                startActivity(new Intent(this, TransformActivity.class));
                break;
            case R.id.bt_filter_operator: //过滤操作符
                startActivity(new Intent(this, FilterActivity.class));
                break;
            case R.id.bt_compose_operator: //组合操作符
                startActivity(new Intent(this, ComposeActivity.class));
                break;
            case R.id.bt_practice: //实践练习
                startActivity(new Intent(this, PracticeActivity.class));
                break;
            case R.id.bt_back_pressure: //背压问题
                startActivity(new Intent(this, BackPressureActivity.class));
                break;
        }
    }
}
