package com.example.myins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri uri;
    private String Myurl = "";
    private StorageReference reference;
    private StorageTask task;
    private ImageView cancel;
    private ImageView post;
    private EditText caption;
    private Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        cancel = (ImageView)findViewById(R.id.cancel);
        post = (ImageView)findViewById(R.id.postImg);
        caption = (EditText) findViewById(R.id.caption);
        postBtn = (Button) findViewById(R.id.postBtn);

        reference = FirebaseStorage.getInstance().getReference("Posts");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });

        /**
         * Crop Using Arthur Hope Library
         */
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);



    }

    private String getFileExtension(Uri uri1){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }

    private void UploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait While Uploading Post .. ");
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
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                        String PostKey = databaseReference.push().getKey();

                        HashMap<String, Object> postData = new HashMap<>();
                        postData.put("postId", PostKey);
                        postData.put("postUser", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        postData.put("postUrl", Myurl);
                        postData.put("postCaption", caption.getText().toString());

                        databaseReference.child(PostKey).setValue(postData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()){
                                            Toast.makeText(PostActivity.this, "Post Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                                            finish();
                                        }  else {
                                            Toast.makeText(PostActivity.this, "Post Uploading Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });



                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Post Uploading Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {

            progressDialog.dismiss();
            Toast.makeText(PostActivity.this, "Please Choose an Image", Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * Purring image into imageView
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            uri = activityResult.getUri();
            post.setImageURI(uri);
        } else {
            Toast.makeText(PostActivity.this, "Error getting photo", Toast.LENGTH_SHORT).show();
        }
    }
}
