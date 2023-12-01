package com.example.imagepro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imagepro.EditProfile;
import com.example.imagepro.R;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {
    private TextView tvuser, tvaddress, tvphone, tvmail;

    private String name, email, address, phone;
    TextView editinfo;
    FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        tvuser = view.findViewById(R.id.tvuser);
        tvaddress = view.findViewById(R.id.tvaddress);
        tvphone = view.findViewById(R.id.tvphone);
        tvmail = view.findViewById(R.id.tvmail);
        editinfo = view.findViewById(R.id.editinfo);
        editinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


            String userId = currentUser.getUid();

            DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference("userinfo");
            DatabaseReference userRef = userInfoRef.child(userId);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user data exists
                    name = dataSnapshot.child("name").getValue(String.class);
                    email = dataSnapshot.child("mail").getValue(String.class);
                    address = dataSnapshot.child("address").getValue(String.class);
                    phone = dataSnapshot.child("phone").getValue(String.class);

                    String[] addressArray = address.split(", ");

                    // Concatenate substrings with line breaks
                    StringBuilder result = new StringBuilder();
                    for (String address : addressArray) {
                        result.append(address).append("\n");
                    }



                    tvuser.setText(name);
                    tvmail.setText(email);
                    tvaddress.setText(result.toString());
                    tvphone.setText(phone);

//                    System.out.println("Name: " + name);
//                    System.out.println("Email: " + email);
//                    System.out.println("Address: " + address);
//                    System.out.println("Phone: " + phone);

                } else {
                    System.out.println("User not found.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });



    }
}
