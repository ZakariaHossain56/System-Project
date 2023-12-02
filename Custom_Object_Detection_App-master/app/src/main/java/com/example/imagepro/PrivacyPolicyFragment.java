package com.example.imagepro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.imagepro.R;


public class PrivacyPolicyFragment extends AppCompatActivity {

    LinearLayout theme_privacy;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_privacy.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_privacy.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_fragment);

        theme_privacy = findViewById(R.id.theme_privacy);
    }



}