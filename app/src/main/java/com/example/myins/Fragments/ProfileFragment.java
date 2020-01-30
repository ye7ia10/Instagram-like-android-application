package com.example.myins.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myins.AccountSettingsActivity;
import com.example.myins.Adapters.MyPostsAdapter;
import com.example.myins.Adapters.UserAdapter;
import com.example.myins.Adapters.postAdapter;
import com.example.myins.Models.User;
import com.example.myins.Models.post;
import com.example.myins.R;
import com.example.myins.SignInActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {


    private Button settings;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserRef;
    private DatabaseReference FollowRef;
    private DatabaseReference Posts;
    private TextView userName , FullName , Bio;
    private TextView posts , followers , following;
    private ImageButton gridit, saved;
    private CircleImageView circleImageView;
    private ImageView options;
    private String ProfileID;
    private int PostsCount = 0;
    private RecyclerView recyclerView;
    private List<post> list;
    private MyPostsAdapter myPostsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        settings=view.findViewById(R.id.account_settings);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        ProfileID = sharedPreferences.getString("profileID", "none");

        firebaseAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.profileUserName);
        FullName = view.findViewById(R.id.fullNameProfile);
        Bio = view.findViewById(R.id.BioProfile);
        followers = view.findViewById(R.id.followersProfile);
        following = view.findViewById(R.id.followingProfile);
        posts = view.findViewById(R.id.postsProfile);
        circleImageView = view.findViewById(R.id.ProfileActImage);
        options = view.findViewById(R.id.optionsProfile);
        gridit = view.findViewById(R.id.GridView);
        saved = view.findViewById(R.id.SavedPhotos);
        recyclerView = view.findViewById(R.id.recycler_view_photos);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        myPostsAdapter = new MyPostsAdapter(list , getContext());
        recyclerView.setAdapter(myPostsAdapter);


        /**/
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (settings.getText().toString().equals("Edit Profile")){
                  startActivity(new Intent(getContext(), AccountSettingsActivity.class));
              } else if (settings.getText().toString().equals("Follow")){
                  FirebaseDatabase.getInstance().getReference().child("follow")
                          .child(firebaseAuth.getCurrentUser().getUid()).child("following")
                          .child(ProfileID).setValue(true)
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  FirebaseDatabase.getInstance().getReference().child("follow")
                                          .child(ProfileID).child("followers")
                                          .child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                              }
                          });
                  settings.setText("Following");
              } else if (settings.getText().toString().equals("Following")){
                  FirebaseDatabase.getInstance().getReference().child("follow")
                          .child(firebaseAuth.getCurrentUser().getUid()).child("following")
                          .child(ProfileID).removeValue()
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  FirebaseDatabase.getInstance().getReference().child("follow")
                                          .child(ProfileID).child("followers")
                                          .child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                              }
                          });
                  settings.setText("Follow");
              }
            }
        });
        if (ProfileID.equals(firebaseAuth.getCurrentUser().getUid())){
            saved.setVisibility(View.VISIBLE);
            getUserDataSelf();
            settings.setText("Edit Profile");
        } else {
            saved.setVisibility(View.GONE);
            checkFollowingStatus(ProfileID);
            getUserDataOther();

        }
        retrievePosts();

        return view;
    }

    private void getUserDataSelf() {
        UserRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    userName.setText(user.getUsername());
                    FullName.setText(user.getFullname());
                    if (!TextUtils.isEmpty(user.getBio())){
                        Bio.setText(user.getBio());
                    } else {Bio.setText("Insta User");}
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Posts = FirebaseDatabase.getInstance().getReference("Posts");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        post posts = snapshot.getValue(post.class);
                        if (posts.getPostUser().equals(firebaseAuth.getCurrentUser().getUid())){
                            PostsCount++;
                        }
                    }
                    posts.setText(String.valueOf(PostsCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FollowRef = FirebaseDatabase.getInstance().getReference("follow");
        FollowRef.child(firebaseAuth.getCurrentUser().getUid()).child("followers").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            followers.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
                        } else {
                            followers.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        FollowRef.child(firebaseAuth.getCurrentUser().getUid()).child("following").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            following.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
                        } else {
                            following.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getUserDataOther() {
        UserRef = FirebaseDatabase.getInstance().getReference("Users").child(ProfileID);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    userName.setText(user.getUsername());
                    FullName.setText(user.getFullname());
                    if (!TextUtils.isEmpty(user.getBio())){
                        Bio.setText(user.getBio());
                    } else {Bio.setText("Insta User");}
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Posts = FirebaseDatabase.getInstance().getReference("Posts");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        post posts = snapshot.getValue(post.class);
                        if (posts.getPostUser().equals(ProfileID)){
                            PostsCount++;
                        }
                    }
                    posts.setText(String.valueOf(PostsCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FollowRef = FirebaseDatabase.getInstance().getReference("follow");
        FollowRef.child(ProfileID).child("followers").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            followers.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
                        } else {
                            followers.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        FollowRef.child(ProfileID).child("following").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            following.setText(String.valueOf((int) dataSnapshot.getChildrenCount()));
                        } else {
                            following.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void checkFollowingStatus(final String userId) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("follow")
                .child(firebaseAuth.getCurrentUser().getUid()).child("following");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).exists())
                {

                   settings.setText("Following");

                }
                else
                {
                    settings.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrievePosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        post posts = snapshot.getValue(post.class);
                        if (posts.getPostUser().equals(ProfileID)){
                            list.add(posts);
                        }
                    }
                }
                Collections.reverse(list);
                myPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
