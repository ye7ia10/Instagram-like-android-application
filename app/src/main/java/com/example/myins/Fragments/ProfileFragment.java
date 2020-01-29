package com.example.myins.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myins.AccountSettingsActivity;
import com.example.myins.Models.User;
import com.example.myins.Models.post;
import com.example.myins.R;
import com.example.myins.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {


    private Button settings;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserRef;
    private DatabaseReference FollowRef;
    private DatabaseReference Posts;
    private TextView userName , FullName , Bio;
    private TextView posts , followers , following;
    private CircleImageView circleImageView;
    private ImageView options;
    private int PostsCount = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        settings=view.findViewById(R.id.account_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.profileUserName);
        FullName = view.findViewById(R.id.fullNameProfile);
        Bio = view.findViewById(R.id.BioProfile);
        followers = view.findViewById(R.id.followersProfile);
        following = view.findViewById(R.id.followingProfile);
        posts = view.findViewById(R.id.postsProfile);
        circleImageView = view.findViewById(R.id.ProfileActImage);
        options = view.findViewById(R.id.optionsProfile);

        /**/
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountSettingsActivity.class));

            }
        });

        getUserData();
        return view;
    }

    private void getUserData() {
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

}
