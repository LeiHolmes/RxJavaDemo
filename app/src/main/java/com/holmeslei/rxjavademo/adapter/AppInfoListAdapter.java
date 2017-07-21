package com.holmeslei.rxjavademo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holmeslei.rxjavademo.R;
import com.holmeslei.rxjavademo.model.AppInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description:
 * author         xulei
 * Date           2017/7/20
 */

public class AppInfoListAdapter extends RecyclerView.Adapter<AppInfoListAdapter.MyViewHolder> {
    private Context context;
    private List<AppInfo> appInfoList;

    public AppInfoListAdapter(Context context, List<AppInfo> appInfoList) {
        this.context = context;
        this.appInfoList = appInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ivHead.setImageDrawable(appInfoList.get(position).getAppIcon());
        holder.tvAppName.setText(appInfoList.get(position).getAppName());
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_head)
        ImageView ivHead;
        @BindView(R.id.item_iv_app_name)
        TextView tvAppName;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
