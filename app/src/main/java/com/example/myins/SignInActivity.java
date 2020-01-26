package com.example.myins;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Button signUpBtn =(Button)findViewById(R.id.signup_link_btn);
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
            progressDialog.setMessage("please wait a moment...");
            progressDialog.setTitle("log in");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            FirebaseAuth mAuth =FirebaseAuth.getInstance();
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
                        String messaqge =task.getException().toString();
                        Toast.makeText(SignInActivity.this,"Error :"+messaqge,Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        progressDialog.dismiss();
                    }

                }
            });
        }
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent =new Intent(SignInActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }*/
}
