package com.example.myins.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myins.Models.User;
import com.example.myins.Models.post;
import com.example.myins.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class postAdapter  extends  RecyclerView.Adapter<postAdapter.PostViewHolder>{

    FirebaseUser firebaseUser;
    List<post> list;
    Context mcontext;


    public postAdapter(  Context mcontext , List<post> list) {
        this.list = list;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.post_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {

        final post posts = list.get(position);
        Picasso.get().load(posts.getPostUrl()).into(holder.postimg);
        if (posts.getPostCaption().equals("")){
            holder.caption.setVisibility(View.GONE);
        } else {
            holder.caption.setVisibility(View.VISIBLE);
            holder.caption.setText(posts.getPostCaption());
        }

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(posts.getPostUser());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.profileimg);
                    holder.username.setText(user.getUsername());
                    holder.publisher.setText(user.getUsername());

                }
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

    public class PostViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileimg , postimg , like , comment , save;
        public TextView username, likeNumber , publisher , caption , viewcomment;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimg = itemView.findViewById(R.id.profileImg);
            username = itemView.findViewById(R.id.userNamePost);
            postimg = itemView.findViewById(R.id.postPhoto);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comm);
            save = itemView.findViewById(R.id.savePhoto);
            likeNumber = itemView.findViewById(R.id.likesNumber);
            publisher = itemView.findViewById(R.id.publisher);
            caption = itemView.findViewById(R.id.ldesc);
            viewcomment = itemView.findViewById(R.id.ShowComments);
        }
    }


}
