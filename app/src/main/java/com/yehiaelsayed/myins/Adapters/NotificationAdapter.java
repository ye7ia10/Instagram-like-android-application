package com.yehiaelsayed.myins.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yehiaelsayed.myins.Fragments.PostDetailsFragment;
import com.yehiaelsayed.myins.Fragments.ProfileFragment;
import com.yehiaelsayed.myins.Models.Notification;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yehiaelsayed.myins.Models.post;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private ArrayList<Notification> notifications;
    private Context mContext;

    public NotificationAdapter(ArrayList<Notification> notifications, Context mContext) {
        this.notifications = notifications;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(mContext).inflate(R.layout.notification_layout, parent, false);

        NotificationHolder viewHolder = new NotificationHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationHolder holder, int position) {
        final Notification notification= notifications.get(position);
        if(notification.getPostID()!=null) {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts")
                    .child(notification.getPostID());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        post post =dataSnapshot.getValue(post.class);
                        Picasso.get().load(post.getPostUrl()).placeholder(R.drawable.profile).into(holder.post);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postID", notification.getPostID());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.frag_container,
                            new PostDetailsFragment()).commit();
                }
            });

        }
        else
        {
            holder.post.setVisibility(View.GONE);
        }
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(notification.getUserId());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    User user =dataSnapshot.getValue(User.class);
                    String editedMsg =user.getFullname()+notification.getMessage();
                    SpannableString ss =new SpannableString(editedMsg);
                    StyleSpan boldSpan =new StyleSpan(Typeface.BOLD);
                    ss.setSpan(boldSpan,0,user.getFullname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.message.setText(ss);
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileID",notification.getUserId());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.frag_container,
                        new ProfileFragment()).commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class  NotificationHolder extends RecyclerView.ViewHolder
    {
        TextView message;
        CircleImageView profile;
        ImageView post;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            mContext=itemView.getContext();
            message=itemView.findViewById(R.id.message_notifications);
            profile=itemView.findViewById(R.id.profile_notifications);
            post=itemView.findViewById(R.id.notifications_pic);

        }
    }
}
