package com.yehiaelsayed.myins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private  CallbackManager mCallbackManager;
    private  String TAG="faceBook";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth=FirebaseAuth.getInstance();
        Button signUpBtn = findViewById(R.id.signup_link_btn);
        Button logInBtn= findViewById(R.id.signin_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        //facebook signIn
        //Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void loginUser() {
        EditText emailEdit=findViewById(R.id.email_signin);
        EditText passwordEdit =findViewById(R.id.password_signin);
        String email =emailEdit.getText().toString();
        String password =passwordEdit.getText().toString();
        if(TextUtils.isEmpty(email))
        {
           Toast.makeText(this,"email is Required !!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"password is Required !!",Toast.LENGTH_LONG).show();
        }
        else
        {
            final ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setMessage("please wait while logging ...");
            progressDialog.setTitle("log in");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Intent intent =new Intent(SignInActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        String messaqge =task.getException().toString();
                        Toast.makeText(SignInActivity.this,"Error :"+messaqge,Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();

                    }

                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(user.getUid());
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists())
                                    {
                                        putFacbookUserInfo(token);
                                    }
                                    else
                                    {
                                        Intent intent =new Intent(SignInActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    //saving new facebook user info
    private void putFacbookUserInfo(AccessToken token)
    {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String fullName=object.getString("first_name")+" "+
                                    object.getString("last_name");
                            String userName=object.getString("name");
                            String email=object.getString("email");
                            String currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");
                            HashMap<String,Object> userMap= new HashMap<>();
                            userMap.put("uid",currentUserId);
                            userMap.put("fullname",fullName);
                            userMap.put("username",userName);
                            userMap.put("email",email);
                            userMap.put("bio","hey I am using insta");
                            userMap.put("image","https://firebasestorage.googleapis.com/v0/b/myins-7aa34.appspot.com/o/default_images%2Fprofile.png?alt=media&token=b6eb9810-3c06-41d1-827c-c79aabd54139");
                            userRef.child(currentUserId).setValue(userMap).addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignInActivity.this,"Account has been created successfully",Toast.LENGTH_LONG).show();
                                        Intent intent =new Intent(SignInActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        String messaqge =task.getException().toString();
                                        Toast.makeText(SignInActivity.this,"Error :"+messaqge,Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
