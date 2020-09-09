package com.yehiaelsayed.myins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yehiaelsayed.myins.Adapters.CommentAdapter;
import com.yehiaelsayed.myins.Models.Comment;
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

public class CommentsActivity extends AppCompatActivity {
    private EditText comment;
    private CircleImageView profileImage;
    private Button sendCommentBtn;
    private RecyclerView recyclerView;
    private String postid , publisher;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        SharedPreferences sharedPreferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        publisher = sharedPreferences.getString("publisherID", "none");
        postid = sharedPreferences.getString("publisherPostID", "none");

        comment = (EditText) findViewById(R.id.commentText);
        profileImage = (CircleImageView) findViewById(R.id.profileimgcomments);
        sendCommentBtn = (Button) findViewById(R.id.postComment);
        recyclerView = (RecyclerView) findViewById(R.id.recComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList =new ArrayList<>();
        commentAdapter  =new CommentAdapter(this ,commentList);
        recyclerView.setAdapter(commentAdapter);

        getComments();

        ImageView imageView = (ImageView) findViewById(R.id.back_comment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(comment.getText().toString())){
                    Toast.makeText(CommentsActivity.this, "Please Write a comment", Toast.LENGTH_SHORT).show();
                } else {
                    ADDCOMMENT();
                }
            }
        });

        getImage();


    }

    private void getComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");
        reference.child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    commentList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        commentList.add(comment);
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Picasso.get().load(user.getImage())
                                .placeholder(R.drawable.profile)
                                .into(profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void ADDCOMMENT() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("comment", comment.getText().toString());
        reference.child(postid).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Notifications")
                        .child(publisher);
                String notiKey = reference.push().getKey();
                HashMap<String,Object> notiMap = new HashMap<>();
                notiMap.put("notificationId",notiKey);
                notiMap.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                notiMap.put("seen",false);
                notiMap.put("message"," commented on your photo");
                notiMap.put("postID",postid);
                reference.child(notiKey).setValue(notiMap);
                Toast.makeText(CommentsActivity.this, "Your Comment Posted Success", Toast.LENGTH_SHORT).show();
            }
        });
        comment.setText("");

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
