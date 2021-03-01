package com.android.assignment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Interface.OnLoadMoreListener;
import Model.ProfileDetails;


public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    List<ProfileDetails> list;

    public ListAdapter(Context context, List<ProfileDetails> userDetails, RecyclerView video_list) {
        this.context = context;
        this.list = userDetails;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) video_list.getLayoutManager();
        video_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        Log.d("loadmore", "totalItemCount --" + totalItemCount + "lastVisibleItem --" + lastVisibleItem + "visibleThreshold --" + visibleThreshold);
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            return new ListAdapter.ViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder1, int i) {
        if (viewHolder1 instanceof ViewHolder) {
            ListAdapter.ViewHolder holder = (ListAdapter.ViewHolder) viewHolder1;
            ProfileDetails seriesData = list.get(i);

            holder.tv_first.setText("First Name: " +seriesData.getFirst_Name());
            holder.tv_last.setText("Last Name: " +seriesData.getLast_Name());
            holder.tv_email.setText("Email: "+seriesData.getEmail());
            Glide.with(context).load(seriesData.getAvatar()).placeholder(R.mipmap.ic_launcher)
                    .into(holder.image);


        } else if (viewHolder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder1;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

    }

    @Override
    public int getItemCount() {
        return list.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView image;
        TextView tv_first,tv_last,tv_email;

      public ViewHolder(View view) {
            super(view);
          image=view.findViewById(R.id.image);
          tv_first=view.findViewById(R.id.tv_first);
          tv_last=view.findViewById(R.id.tv_last);
          tv_email=view.findViewById(R.id.tv_email);
            //iv_icon.setOnClickListener(this);
        }


    }




    public void setData(List<ProfileDetails> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void setLoaded() {
        isLoading = false;
    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }


}
