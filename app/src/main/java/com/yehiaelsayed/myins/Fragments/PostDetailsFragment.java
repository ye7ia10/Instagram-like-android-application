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
import android.widget.ImageView;

import com.yehiaelsayed.myins.Adapters.postAdapter;
import com.yehiaelsayed.myins.Models.post;
import com.yehiaelsayed.myins.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PostDetailsFragment extends Fragment {
    private String postID;
    private postAdapter postAdapter;
    private List<post> list;
    private RecyclerView recyclerView;
    private ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postID = sharedPreferences.getString("postID", "none");
        recyclerView = view.findViewById(R.id.recPostDetails);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        postAdapter = new postAdapter(getContext(), list);
        recyclerView.setAdapter(postAdapter);
        imageView = view.findViewById(R.id.back_Profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container,
                        new ProfileFragment()).commit();
            }
        });


        GetPost();




        return  view;
    }

    private void GetPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                post posts = dataSnapshot.getValue(post.class);
                list.add(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
