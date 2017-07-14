package com.holmeslei.rxjavademo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.holmeslei.rxjavademo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_transform_operator)
    public void onTransformClick(View view) {
        startActivity(new Intent(this,TransformActivity.class));
    }

    @OnClick(R.id.bt_filter_operator)
    public void onFilterClick(View view) {

    }

    @OnClick(R.id.bt_compose_operator)
    public void onComposeClick(View view) {

    }
}
