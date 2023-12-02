package com.example.imagepro.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imagepro.R;


public class ContactUsFragment extends Fragment {

    private LinearLayout facebook, messenger,gmail, whatsapp, x, youtube, theme_contactus;

    private String url;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contact_us_fragment,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_contactus.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_contactus.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        facebook = view.findViewById(R.id.facebook);
        messenger = view.findViewById(R.id.messenger);
        gmail = view.findViewById(R.id.gmail);
        whatsapp = view.findViewById(R.id.whatsapp);
        x = view.findViewById(R.id.x);
        youtube = view.findViewById(R.id.youtube);

        theme_contactus = view.findViewById(R.id.theme_contactus);


        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "https://m.facebook.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });


        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                url = "https://www.messenger.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "https://mail.google.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "https://www.whatsapp.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "https://twitter.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "https://www.youtube.com";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });



    }



}