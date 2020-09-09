package com.yehiaelsayed.myins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signInBtn =(Button)findViewById(R.id.signin_link_btn);
        Button signUpBtn =(Button)findViewById(R.id.signup_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        final EditText full = findViewById(R.id.full_name_signup);
        EditText user = findViewById(R.id.user_name_signup);
        EditText email = findViewById(R.id.email_signup);
        EditText password = findViewById(R.id.password_signup);
        final String fullName= full.getText().toString();
        final String userName = user.getText().toString();
        String passWord =password.getText().toString();
        final String user_email =email.getText().toString();
        if(TextUtils.isEmpty(fullName))
        {
            Toast.makeText(this,"Full name is Required !!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(this,"User name is Required !!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(passWord))
        {
            Toast.makeText(this,"password is Required !!",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(user_email))
        {
            Toast.makeText(this,"E-mail is Required !!",Toast.LENGTH_LONG).show();
        }
        else
        {
            final ProgressDialog progressDialog= new ProgressDialog(this);
            progressDialog.setMessage("please wait While making account ...");
            progressDialog.setTitle("sign up");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            final FirebaseAuth mAuth =FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(user_email,passWord).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                             saveUserInfo(fullName,userName,user_email,progressDialog);
                    }
                    else
                    {
                      String messaqge =task.getException().toString();
                      Toast.makeText(SignUpActivity.this,"Error :"+messaqge,Toast.LENGTH_LONG).show();
                      mAuth.signOut();
                      progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void saveUserInfo(String fullName, String userName, String user_email, final ProgressDialog progressDialog) {
        String currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap= new HashMap<>();
        userMap.put("uid",currentUserId);
        userMap.put("fullname",fullName);
        userMap.put("username",userName);
        userMap.put("email",user_email);
        userMap.put("bio","hey I am using insta");
        userMap.put("image","https://firebasestorage.googleapis.com/v0/b/myins-7aa34.appspot.com/o/default_images%2Fprofile.png?alt=media&token=b6eb9810-3c06-41d1-827c-c79aabd54139");
        userRef.child(currentUserId).setValue(userMap).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"Account has been created successfully",Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(SignUpActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    String messaqge =task.getException().toString();
                    Toast.makeText(SignUpActivity.this,"Error :"+messaqge,Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    progressDialog.dismiss();
                }
            }
        });

    }
}
