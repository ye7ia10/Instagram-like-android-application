package com.example.myins;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myins.Fragments.HomeFragment;
import com.example.myins.Fragments.NotificationsFragment;
import com.example.myins.Fragments.ProfileFragment;
import com.example.myins.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    Fragment selector = null;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            this.finish();
            startActivity(intent);
        }

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(OnSelectedListner);


        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container
                , new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener OnSelectedListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home :
                            selector = new HomeFragment();
                            break;
                        case R.id.nav_search :
                            selector = new SearchFragment();
                            break;
                        case R.id.nav_add :
                            Intent intent = new Intent(MainActivity.this, PostActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_likes :
                            selector = new NotificationsFragment();
                            break;
                        case R.id.nav_profile :
                            selector = new ProfileFragment();
                            break;
                    }

                    if (selector != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container
                        , selector).commit();
                    }

                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId() == R.id.nav_home){
            super.onBackPressed();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user == null){
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            this.finish();
            startActivity(intent);
        }
    }
}
