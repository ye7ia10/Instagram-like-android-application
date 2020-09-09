package com.yehiaelsayed.myins.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yehiaelsayed.myins.Adapters.UserAdapter;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FollowFragment extends Fragment {
    private TextView userName;
    private Button showFollowers;
    private RecyclerView followers;
    private ImageView imageView;
    private String ProfileID;
    private String ButtonPressed;
    private ArrayList<User> followersList;
    private ArrayList<String> list;
    private UserAdapter userAdapter , adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_follow, container, false);
        imageView = view.findViewById(R.id.back_follow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container,
                        new ProfileFragment()).commit();
            }
        });
        showFollowers = view.findViewById(R.id.followers);

        followers =  view.findViewById(R.id.recFollowers);
        followers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        followers.setLayoutManager(linearLayoutManager);
        followersList = new ArrayList<>();
        list = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), followersList);
        followers.setAdapter(userAdapter);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        ProfileID = sharedPreferences.getString("profileID", "none");
        ButtonPressed = sharedPreferences.getString("button", "none");
        userName = view.findViewById(R.id.userNameFollow);
        getUserName();


        if (ButtonPressed.equals("followers")){
            showFollowers.setText("Followers");
            getFollowers();

        } else {
            showFollowers.setText("Following");
            getFollowing();
        }

        return view;
    }



    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow");
        reference.child(ProfileID)
                .child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String str = snapshot.getKey();
                            list.add(str);
                        }
                        showFR();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow");
        reference.child(ProfileID)
                .child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            list.add(snapshot.getKey());
                        }
                        showFR();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showFR() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String s : list) {
                        if (s.equals(user.getUid())) {
                            followersList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserName() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(ProfileID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userName.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
