package com.yehiaelsayed.myins.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yehiaelsayed.myins.Adapters.UserAdapter;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<User> mUser ;
    private UserAdapter userAdapter;
    private  EditText editText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);




        recyclerView=view.findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUser=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),mUser);
        recyclerView.setAdapter(userAdapter);

        editText=view.findViewById(R.id.search_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getText().toString()=="")
                {

                }
                else
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    retrieveUsers();
                    searchUsers(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
    private  void searchUsers(String s)
    {

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").
                orderByChild("fullname").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUser.clear();
                if(editText.getText().toString()!="")
                {

                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        User user= snapshot.getValue(User.class);

                        if(user!=null)
                        {

                            mUser.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void retrieveUsers()
    {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(editText.getText().toString()!="")
                {
                    mUser.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        User user= snapshot.getValue(User.class);
                        if(user!=null)
                        {
                         mUser.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
