package com.yehiaelsayed.myins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yehiaelsayed.myins.Models.Story;
import com.yehiaelsayed.myins.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private ImageView storyPhoto;
    private CircleImageView USrPhoto;
    private TextView StoryUsrName;
    private View skip, reverse;
    private StoriesProgressView storiesProgressView;
    private int counter;
    private long press = 0L;
    private long limit = 500L;
    private List<String> storries;
    private List<String> images;
    private String UserID;
    private int x = 0;

    private LinearLayout r_seen;
    private TextView seenCount;
    private ImageView deleteStory;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    press = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now-press;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_story);

        storyPhoto = findViewById(R.id.theStory);
        USrPhoto = findViewById(R.id.storyProfile);
        storiesProgressView = findViewById(R.id.stories);
        StoryUsrName = findViewById(R.id.storyUsrName);
        reverse = findViewById(R.id.reverse);
        skip = findViewById(R.id.skip);
        r_seen = findViewById(R.id.linea);
        seenCount = findViewById(R.id.seenCounter);
        deleteStory = findViewById(R.id.deleteStory);

        r_seen.setVisibility(View.GONE);
        deleteStory.setVisibility(View.GONE);

        UserID = getIntent().getStringExtra("user");
        if (UserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            deleteStory.setVisibility(View.VISIBLE);
        }
        getStories(UserID);
        getUserInfo(UserID);

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        deleteStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(UserID).child(storries.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(DisplayStoryActivity.this, "Story Deleted", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onNext() {

        Picasso.get().load(images.get(++counter)).into(storyPhoto);
        addView(storries.get(counter));
        getNumber(storries.get(counter));

    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0){
            return;
        }
        Picasso.get().load(images.get(++counter)).into(storyPhoto);
        getNumber(storries.get(counter));

    }

    @Override
    public void onComplete() {
        if (!UserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && x == 0) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications")
                    .child(UserID);
            String notiKey = reference.push().getKey();
            HashMap<String, Object> notiMap = new HashMap<>();
            notiMap.put("notificationId", notiKey);
            notiMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notiMap.put("seen", false);
            notiMap.put("message", " Viewed Your Story");
            reference.child(notiKey).setValue(notiMap);
            x++;

        }
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String user){
        storries = new ArrayList<>();
        images = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(user);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storries.clear();
                images.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    long current = System.currentTimeMillis();
                    Story story = snapshot.getValue(Story.class);
                    if (current > story.getTimeStart() && current < story.getTimeEnd()){
                        storries.add(story.getStoryId());
                        images.add(story.getImageUri());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(DisplayStoryActivity.this);
                try{
                    storiesProgressView.startStories(counter);

                } catch (Exception e){
                    finish();
                }
                Picasso.get().load(images.get(counter)).into(storyPhoto);
                addView(storries.get(counter));
                getNumber(storries.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserInfo(String user){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                Picasso.get().load(user1.getImage()).placeholder(R.drawable.profile)
                        .into(USrPhoto);
                StoryUsrName.setText(user1.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addView(String story){
        FirebaseDatabase.getInstance().getReference("Story").child(UserID)
                .child(story)
                .child("views")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue("true");

    }

    private void getNumber(String story){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(UserID)
                .child(story)
                .child("views");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    seenCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
