package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.app_bar_main);

        toolbar = view.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_profile, R.id.nav_how_to_use, R.id.nav_about_sign_language,R.id.nav_history,R.id.contactus,R.id.privacypolicy,R.id.aboutus,R.id.signout)
                .setOpenableLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(navController == null) return false;

                if(item.getItemId() == R.id.contactus){
                    contactUs();
                    return false;
                }
                else if(item.getItemId() == R.id.privacypolicy){
                    showPolicy();
                    return false;
                }
                else if(item.getItemId() == R.id.aboutus){
                    showAboutUs();
                    return false;
                }
                else if(item.getItemId() == R.id.signout){
                    signOutUser();
                    return false;
                }

                int id = item.getItemId();
                if(id == R.id.nav_home || id == R.id.nav_profile || id == R.id.nav_how_to_use  || id == R.id.nav_about_sign_language || id == R.id.nav_history  ){
                    navController.navigate(id);
                    navView.setCheckedItem(id);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }

    private void contactUs(){
        //rest of the code for contact us
        showToast("Contact us");
    }

    private void showAboutUs(){
        //rest of the code for about us
        showToast("About us");
    }

    private void showPolicy(){
        //rest of the code for policy
        showToast("Policy clicked");
    }

    private void signOutUser(){
        //rest of the code for sign out
        showToast("sign out clicked");
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(item.getItemId() == android.R.id.home){ // use android.R.id
            drawerLayout.openDrawer(GravityCompat.START);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}