package com.yehiaelsayed.myins.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yehiaelsayed.myins.Fragments.ProfileFragment;
import com.yehiaelsayed.myins.Models.Notification;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context mContext;
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    public UserAdapter(Context context,ArrayList<User> users) {
        this.mContext=context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        // Inflate the custom layout
        View contactView = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User mUser= users.get(position);
        holder.fullName.setText(mUser.getFullname());
        holder.userName.setText(mUser.getUsername());
        Picasso.get().load(mUser.getImage()).placeholder(R.drawable.profile).into(holder.profileImage);
        checkFollowingStatus(holder,mUser.getUid());

        /*Log.d("sa", "onBindViewHolder: "+firebaseUser.getUid()+"\n");
        Log.d("sa", "onBindViewHolder2: "+mUser.getUid()+"\n");*/
        if(mUser.getUid().equals(firebaseUser.getUid()))
        {
            holder.followBtn.setVisibility(View.GONE);
        }
        else
        {
            holder.followBtn.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileID", mUser.getUid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager()
                   .beginTransaction().replace(R.id.frag_container,
                        new ProfileFragment()).commit();
                //MainActivity.bottomNavigationView.setSelectedItemId(R.id.nav_profile);

            }
        });


        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("zasasa", "onSuccess: \n");
                if (holder.followBtn.getText() == "Follow") {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(mUser.getUid()).setValue(true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("follow")
                                            .child(mUser.getUid()).child("followers")
                                            .child(firebaseUser.getUid()).setValue(true);

                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                                            .child(mUser.getUid());
                                    String notiKey = reference.push().getKey();
                                    HashMap<String,Object> notiMap = new HashMap<>();
                                    notiMap.put("notificationId",notiKey);
                                    notiMap.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    notiMap.put("seen",false);
                                    notiMap.put("message"," started following you");
                                    reference.child(notiKey).setValue(notiMap);
                                }
                            });

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(mUser.getUid()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("follow")
                                            .child(mUser.getUid()).child("followers")
                                            .child(firebaseUser.getUid()).removeValue();
                                    final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                                            .child(mUser.getUid());
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists())
                                            {
                                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                                {
                                                    Notification notification =snapshot.getValue(Notification.class);
                                                    if(notification.getPostID()==null&&
                                                            notification.getUserId()==
                                                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                                                            &&holder.followBtn.getText() == "Follow")
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
                            });
                }
            }
        });

    }

    private void checkFollowingStatus(final ViewHolder holder, final String userId) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("follow")
                .child(firebaseUser.getUid()).child("following");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userId).exists())
                {

                    holder.followBtn.setText("Following");

                }
                else
                {
                    holder.followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        Notification notification =snapshot.getValue(Notification.class);
                        if(notification.getPostID()==null&&
                                notification.getUserId()==
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                &&holder.followBtn.getText() == "Follow")
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
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView fullName;
        public TextView userName;
        public CircleImageView profileImage;
        public Button followBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext=itemView.getContext();
            fullName=itemView.findViewById(R.id.user_fullname_search);
            userName=itemView.findViewById(R.id.user_name_search);
            profileImage=itemView.findViewById(R.id.user_profile_image);
            followBtn=itemView.findViewById(R.id.follow_btn_search);
        }
    }
}
