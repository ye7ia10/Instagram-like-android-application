package com.yehiaelsayed.myins.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yehiaelsayed.myins.Fragments.PostDetailsFragment;
import com.yehiaelsayed.myins.Models.post;
import com.yehiaelsayed.myins.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postID", posts.getPostId());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.frag_container,
                            new PostDetailsFragment()).commit();
                }
            });
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
