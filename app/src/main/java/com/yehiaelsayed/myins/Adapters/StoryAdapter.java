package com.yehiaelsayed.myins.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yehiaelsayed.myins.DisplayStoryActivity;
import com.yehiaelsayed.myins.Models.Story;
import com.yehiaelsayed.myins.Models.User;
import com.yehiaelsayed.myins.R;
import com.yehiaelsayed.myins.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends  RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private Context mContext;
    private List<Story> mList;

    public StoryAdapter(Context mContext, List<Story> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story, parent, false);
            return new StoryAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Story story = mList.get(position);
        userInfo(holder, story.getUserId(), position);

        if (holder.getAdapterPosition() != 0){
            seenStory(story.getUserId(), holder);
        }
        if (holder.getAdapterPosition() == 0) {
            myStory(holder.story_plus, holder.AddStoryText, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() == 0){
                    myStory(holder.story_plus, holder.AddStoryText, true);
                } else {
                   Intent intent = new Intent(mContext, DisplayStoryActivity.class);
                   intent.putExtra("user", story.getUserId());
                   mContext.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView story_photo, story_plus , story_seen;
        private TextView UserNameStory , AddStoryText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_seen = itemView.findViewById(R.id.story_photo_seen);
            story_plus = itemView.findViewById(R.id.add_story);
            UserNameStory = itemView.findViewById(R.id.userName_Story);
            AddStoryText = itemView.findViewById(R.id.add_story_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return  0;
        }
        return  1;
    }

    private void userInfo(final ViewHolder viewHolder, String UserId , final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(UserId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage()).into(viewHolder.story_photo);
                if (pos != 0){
                    Picasso.get().load(user.getImage()).into(viewHolder.story_seen);
                    viewHolder.UserNameStory.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final ImageView imageView , final TextView textView , final boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long time = System.currentTimeMillis();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Story story = snapshot.getValue(Story.class);
                        if (time > story.getTimeStart() && time < story.getTimeEnd()) {
                            count++;
                        }
                    }
                }

                    if (click){

                        if (count > 0){
                            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(mContext, DisplayStoryActivity.class);
                                            intent.putExtra("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            mContext.startActivity(intent);
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent
                                                     = new Intent(mContext, StoryActivity.class);
                                            mContext.startActivity(intent);
                                            alertDialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            Intent intent
                                    = new Intent(mContext, StoryActivity.class);
                            mContext.startActivity(intent);
                        }

                    } else {

                        if (count > 0){
                            textView.setText("My Story");
                            imageView.setVisibility(View.GONE);
                        } else {
                            textView.setText("Add_Story");
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenStory (String userID, final ViewHolder viewHolder){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            int i = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.child("views")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                     && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeEnd()){
                                            i++;
                    }
                }

                if (i > 0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
