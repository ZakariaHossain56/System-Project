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


public class TermsandConditionsFragment extends AppCompatActivity {

    LinearLayout theme_terms;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_terms.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_terms.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsand_conditions_fragment);

        theme_terms = findViewById(R.id.theme_terms);
    }
}