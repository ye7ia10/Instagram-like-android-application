package com.yehiaelsayed.myins.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yehiaelsayed.myins.Adapters.NotificationAdapter;
import com.yehiaelsayed.myins.Models.Notification;
import com.yehiaelsayed.myins.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private ArrayList<Notification> notifications;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.recycleNotifications);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications,getContext());
        recyclerView.setAdapter(adapter);
        openNotifications();

        return  view;
    }

    private void openNotifications() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    notifications.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        Notification notification= snapshot.getValue(Notification.class);

                        if(notification!=null)
                        {
                            if(!notification.isSeen()) {
                                notification.setSeen(true);
                                reference.child(notification.getNotificationId()).child("seen").setValue(true);
                            }
                            notifications.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
