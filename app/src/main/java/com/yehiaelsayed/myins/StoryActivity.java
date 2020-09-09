package com.yehiaelsayed.myins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class StoryActivity extends AppCompatActivity {

    private Uri uri;
    private String Myurl = "";
    private StorageReference reference;
    private StorageTask task;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        reference = FirebaseStorage.getInstance().getReference("Story");

        /**
         * Crop Using Arthur Hope Library
         */
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(StoryActivity.this);

    }

    private String getFileExtension(Uri uri1){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }

    private void UploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading story");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait While Uploading your story .. ");
        progressDialog.show();

        if (uri != null){
            final StorageReference storageReference = reference.child(System.currentTimeMillis() +
                    "." + getFileExtension(uri));
            task = storageReference.putFile(uri);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        return task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        Myurl = downloadUri.toString();



                        //save to database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String PostKey = databaseReference.push().getKey();

                        HashMap<String, Object> postData = new HashMap<>();
                        postData.put("storyId", PostKey);
                        postData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        postData.put("imageUri", Myurl);
                        postData.put("timeStart", System.currentTimeMillis());
                        postData.put("timeEnd", System.currentTimeMillis() + 86400000);

                        databaseReference.child(PostKey).setValue(postData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()){
                                            Toast.makeText(StoryActivity.this, "Story Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(StoryActivity.this, MainActivity.class));
                                            finish();
                                        }  else {
                                            Toast.makeText(StoryActivity.this, "Post Uploading Failed", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });



                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(StoryActivity.this, "Story Uploading Failed", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } else {

            progressDialog.dismiss();
            Toast.makeText(StoryActivity.this, "Please Choose an Image", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            uri = activityResult.getUri();

            UploadImage();
        } else {
            Toast.makeText(StoryActivity.this, "Error getting photo", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoryActivity.this, MainActivity.class));
            finish();
        }
    }
}
