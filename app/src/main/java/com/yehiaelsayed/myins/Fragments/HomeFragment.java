package com.yehiaelsayed.myins.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yehiaelsayed.myins.Adapters.StoryAdapter;
import com.yehiaelsayed.myins.Adapters.postAdapter;
import com.yehiaelsayed.myins.Models.Story;
import com.yehiaelsayed.myins.Models.post;
import com.yehiaelsayed.myins.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private postAdapter postAdapter;
    private List<post> listposts;
    private List<String> following;
    private RecyclerView recyclerViewStory;
    private StoryAdapter storyAdapter;
    private List<Story> stories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclePosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        listposts = new ArrayList<>();
        postAdapter = new postAdapter(getContext(), listposts);
        recyclerView.setAdapter(postAdapter);

        recyclerViewStory = view.findViewById(R.id.recycleStories);
        recyclerViewStory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext()
         , RecyclerView.HORIZONTAL , false);
        recyclerViewStory .setLayoutManager(linearLayoutManager1);
        stories = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), stories);
        recyclerViewStory.setAdapter(storyAdapter);


        Retrieve();





        return  view;

    }

    private void getStories() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long time = System.currentTimeMillis();
                stories.clear();
                stories.add(new Story("", "" , 0 , 0,
                 FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : following){
                    Story story = null;
                    int counter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if (time > story.getTimeStart() && time < story.getTimeEnd()){
                                counter++;
                        }
                    }
                    if (counter > 0){
                        stories.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Retrieve (){

        following = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        following.add(snapshot.getKey());
                    }

                    showPost();
                    getStories();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showPost (){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listposts.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        post posts = snapshot.getValue(post.class);
                        for (String id : following){
                            if (id.equals(posts.getPostUser())){
                                listposts.add(posts);
                            }
                        }
                    }

                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
