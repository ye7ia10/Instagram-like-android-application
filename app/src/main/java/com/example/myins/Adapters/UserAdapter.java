package com.example.myins.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myins.Models.User;
import com.example.myins.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context mContext;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User mUser= users.get(position);
        holder.fullName.setText(mUser.getFullname());
        holder.userName.setText(mUser.getUsername());
        Picasso.get().load(mUser.getImage()).placeholder(R.drawable.profile).into(holder.profileImage);

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
