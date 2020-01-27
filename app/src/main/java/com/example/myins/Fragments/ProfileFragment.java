package com.example.myins.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myins.R;
import com.example.myins.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    Button btn;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        btn = view.findViewById(R.id.logoutb);
        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

}
