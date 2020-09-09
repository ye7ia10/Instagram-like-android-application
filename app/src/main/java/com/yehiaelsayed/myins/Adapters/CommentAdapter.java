package com.yehiaelsayed.myins.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yehiaelsayed.myins.Models.Comment;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context mContext;
    private List<Comment> list;

    public CommentAdapter(Context mContext, List<Comment> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentview = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        CommentAdapter.ViewHolder viewHolder = new CommentAdapter.ViewHolder(commentview);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Comment comment = list.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(comment.getUser())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        holder.userNameComments.setText(user.getUsername());
                        Picasso.get().load(user.getImage())
                                .placeholder(R.drawable.profile)
                                .into(holder.userImageComments);
                        holder.Comment.setText(comment.getComment());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView userImageComments;
        public TextView userNameComments, Comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageComments = itemView.findViewById(R.id.user_profile_comment);
            userNameComments = itemView.findViewById(R.id.user_name_comment);
            Comment = itemView.findViewById(R.id.userComment);
        }
    }
}
