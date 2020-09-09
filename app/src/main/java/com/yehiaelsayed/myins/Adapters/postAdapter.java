package com.yehiaelsayed.myins.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yehiaelsayed.myins.CommentsActivity;
import com.yehiaelsayed.myins.Fragments.ProfileFragment;
import com.yehiaelsayed.myins.Models.Notification;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.Models.post;
import com.yehiaelsayed.myins.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {

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

        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileID", posts.getPostUser());
                editor.apply();
                ((FragmentActivity)mcontext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container,
                        new ProfileFragment()).commit();
                //MainActivity.bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("publisherID", posts.getPostUser());
                editor.putString("publisherPostID", posts.getPostId());
                editor.apply();
                mcontext.startActivity(new Intent(mcontext, CommentsActivity.class));
            }
        });

        holder.viewcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("publisherID", posts.getPostUser());
                editor.putString("publisherPostID", posts.getPostId());
                editor.apply();
                mcontext.startActivity(new Intent(mcontext, CommentsActivity.class));
            }
        });



        TestLikes(posts.getPostId(),posts.getPostUser(), holder.like);
        getNumberOfLikes(holder.likeNumber, posts.getPostId());
        getNumberOfComments(holder.viewcomment, posts.getPostId());
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("UnLiked")){
                   FirebaseDatabase
                            .getInstance().getReference()
                            .child("Likes")
                            .child(posts.getPostId())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                            .child(posts.getPostUser());
                    String notiKey = reference.push().getKey();
                    HashMap<String,Object> notiMap = new HashMap<>();
                    notiMap.put("notificationId",notiKey);
                    notiMap.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notiMap.put("seen",false);
                    notiMap.put("message"," liked your photo");
                    notiMap.put("postID",posts.getPostId());
                    reference.child(notiKey).setValue(notiMap);


                } else {
                    FirebaseDatabase
                            .getInstance().getReference()
                            .child("Likes")
                            .child(posts.getPostId())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();


                }
            }
        });
        TestSaves(posts.getPostId(), holder.save);
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("UnSaved")){
                            FirebaseDatabase
                            .getInstance().getReference()
                            .child("Saves")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(posts.getPostId()).setValue(true);

                } else {

                    FirebaseDatabase
                            .getInstance().getReference()
                            .child("Saves")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(posts.getPostId()).removeValue();

                }
            }
        });


    }
    private void getNumberOfComments(final TextView viewcomment, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    viewcomment.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
                } else {
                    viewcomment.setText("No Comments Yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void TestLikes(final String postid , final String postUser, final ImageView imageView){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child(user.getUid()).exists()) {
                                imageView.setImageResource(R.drawable.heart_clicked);
                                imageView.setTag("Liked");
                            } else {
                                imageView.setImageResource(R.drawable.heart_not_clicked);
                                imageView.setTag("UnLiked");
                            }

                        } else {
                            imageView.setImageResource(R.drawable.heart_not_clicked);
                            imageView.setTag("UnLiked");

                        }
                        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                                .child(postUser);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                    {
                                        Notification notification =snapshot.getValue(Notification.class);

                                        if(notification.getPostID()==postid&&
                                                notification.getUserId()==
                                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                                &&imageView.getTag().equals("UnLiked"))
                                        {
                                            reference.child(notification.getNotificationId()).removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void TestSaves(final String postid , final ImageView imageView){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(postid).exists()) {
                        imageView.setImageResource(R.drawable.saved);
                        imageView.setTag("Saved");
                    } else {
                        imageView.setImageResource(R.drawable.saveic);
                        imageView.setTag("UnSaved");
                    }

                } else {
                    imageView.setImageResource(R.drawable.saveic);
                    imageView.setTag("UnSaved");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNumberOfLikes(final TextView likes , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    likes.setText("Liked by " + dataSnapshot.getChildrenCount() + " Users");
                } else {
                    likes.setText("Liked by 0 Users");
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
