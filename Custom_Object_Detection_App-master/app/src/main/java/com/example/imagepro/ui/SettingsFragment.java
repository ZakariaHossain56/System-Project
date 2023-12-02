package com.example.imagepro.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.imagepro.ChangePassword;
import com.example.imagepro.PrivacyPolicyFragment;
import com.example.imagepro.R;
import com.example.imagepro.TermsandConditionsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsFragment extends Fragment {

    private TextView tvsettingsuser, tvsettingsphone;
    RelativeLayout tvterms, tvprivacy, tvchangepass;

    private SwitchCompat darkmode;

    private LinearLayout llSettings;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_settings_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvsettingsuser = view.findViewById(R.id.tvsettingsuser);
        tvsettingsphone = view.findViewById(R.id.tvsettingsphone);
        tvterms = view.findViewById(R.id.tvterms);
        tvprivacy = view.findViewById(R.id.tvprivacy);
        tvchangepass = view.findViewById(R.id.tvchangepass);

        llSettings = view.findViewById(R.id.llSettings);
        darkmode = view.findViewById(R.id.darkmode);

        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Toggle dark mode based on the state of the SwitchCompat
                if (isChecked) {
                    setDarkMode();
                } else {
                    setLightMode();
                }
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        String userId = currentUser.getUid();

        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference("userinfo");
        DatabaseReference userRef = userInfoRef.child(userId);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user data exists
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    tvsettingsuser.setText(name);
                    tvsettingsphone.setText(phone);

                } else {
                    System.out.println("User not found.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });

        tvterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TermsandConditionsFragment.class);
                startActivity(intent);
            }
        });

        tvprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PrivacyPolicyFragment.class);
                startActivity(intent);
            }
        });

        tvchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePassword.class);
                startActivity(intent);

            }
        });



    }

    private void setDarkMode() {
        // Change the background and text color to represent dark mode
        llSettings.setBackgroundColor(getResources().getColor(R.color.primary));
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dm",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark",true);
        //editor.putString("password",pass);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        llSettings.setBackgroundResource((R.drawable.splash_background));
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dm",MODE_PRIVATE);

        darkmode.setChecked(sharedPreferences.getBoolean("dark",true) );
        if(sharedPreferences.getBoolean("dark",true)){
            llSettings.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            llSettings.setBackgroundResource((R.drawable.splash_background));
        }

    }


    private void setLightMode() {
        // Change the background and text color to represent light mode
        llSettings.setBackgroundResource((R.drawable.splash_background));
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dm",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark",false);
        //editor.putString("password",pass);
        editor.apply();
    }
}