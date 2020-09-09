package com.yehiaelsayed.myins;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yehiaelsayed.myins.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView changeImage;
    private EditText fullName;
    private EditText userName;
    private EditText bio;
    private EditText email;
    private String imageUrl;
    private DatabaseReference ref;
    private StorageReference storRef;
    private Uri uri;
    private StorageTask task;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.image_edit);
        changeImage = findViewById(R.id.image_edit_text);
        fullName = findViewById(R.id.full_name_edit);
        userName = findViewById(R.id.user_name_edit);
        bio = findViewById(R.id.bio_edit);
        email = findViewById(R.id.email_edit);
        ref  = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullName.setText(user.getFullname());
                userName.setText(user.getUsername());
                bio.setText(user.getBio());
                email.setText(user.getEmail());
                email.setKeyListener(null);
                Picasso.get().load(user.getImage()).resize(120,120)
                        .centerCrop().placeholder(R.drawable.profile).resize(120,120).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        storRef= FirebaseStorage.getInstance().getReference("profile_images");
        ImageView close =findViewById(R.id.close_edit);
        ImageView save =findViewById(R.id.save_edit);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUpdates();
                //onBackPressed();
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(AccountSettingsActivity.this);

            }
        });
    }

    private String getFileExtension(Uri uri1){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }

    private void uploadUpdates() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please Wait While Updating profile");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (uri != null) {
            final StorageReference storageReference = storRef.child("/"+FirebaseAuth.getInstance().getCurrentUser().getUid()
                    +"/"+System.currentTimeMillis() +
                    "." + getFileExtension(uri));
            task=storageReference.putFile(uri);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        progressDialog.dismiss();
                        return task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        Log.d("check2", downloadUri.toString()+"\n ");
                        imageUrl = downloadUri.toString();
                        HashMap<String,Object> userMap= new HashMap<>();
                        userMap.put("fullname",fullName.getText().toString());
                        userMap.put("username",userName.getText().toString());
                        userMap.put("bio",bio.getText().toString());
                        userMap.put("image",imageUrl);
                        progressDialog.dismiss();
                        ref.updateChildren(userMap);


                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(AccountSettingsActivity.this, "Error Uploading Data", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else
        {
            HashMap<String,Object> userMap= new HashMap<>();
            userMap.put("fullname",fullName.getText().toString());
            userMap.put("username",userName.getText().toString());
            userMap.put("bio",bio.getText().toString());
            ref.updateChildren(userMap);
            progressDialog.dismiss();
            Toast.makeText(AccountSettingsActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            uri = activityResult.getUri();
            profileImage.setImageURI(uri);
        } else {
            Toast.makeText(AccountSettingsActivity.this, "Error getting photo", Toast.LENGTH_SHORT).show();
        }
    }


}
