package com.example.myins.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myins.Models.User;
import com.example.myins.Models.post;
import com.example.myins.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter  extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder>  {
    private List<post> myposts;
    private Context mContext;

    public MyPostsAdapter(List<post> myposts, Context mContext) {
        this.myposts = myposts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View postview = LayoutInflater.from(mContext).inflate(R.layout.foto_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(postview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final post posts = myposts.get(position);
            Picasso.get().load(posts.getPostUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return myposts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext=itemView.getContext();
            imageView = itemView.findViewById(R.id.foto);
        }
    }
}
