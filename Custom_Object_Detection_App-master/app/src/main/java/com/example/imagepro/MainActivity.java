package com.example.imagepro;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private Toolbar toolbar;



    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private NavController navController;
    boolean downloadSuccessful,downloadUpdate,colorChange = false;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_KEY = "isFirstTime";

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
        colorChange=downloadUpdates(false);
        showToast("colorchange  "+colorChange);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_profile, R.id.nav_how_to_use, R.id.nav_about_sign_language,
                R.id.nav_history,R.id.nav_aboutus,R.id.nav_terms,R.id.nav_contactus,R.id.nav_privacy,
                R.id.nav_settings,R.id.nav_update,
                R.id.signout)
                .setOpenableLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        /*if(colorChange)
        {
            NavigationView navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
           // menu.findItem(R.id.nav_home).setIconTintList(ContextCompat.getColorStateList(this, R.color.black));
            //menu.findItem(R.id.nav_home).set(ContextCompat.getColorStateList(this, R.color.your_custom_color));
            menu.findItem(R.id.nav_update).setTitleCondensed("Download Upadtes");
            int iid = R.id.nav_update;
            //showToast("befbbe");
        }*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if the app is launched for the first time
        boolean isFirstTime = settings.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTime) {
            // Perform initialization for the first launch
            SharedPreferences SP = getSharedPreferences("SP", MODE_PRIVATE);
            SharedPreferences.Editor editor = SP.edit();
            int initialCount = 0;
            editor.putInt("putStringCount", initialCount).apply();
            // Update the flag to indicate that the app has been launched before
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putBoolean(FIRST_TIME_KEY, false);
            editor1.apply();
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(navController == null) return false;


                else if(item.getItemId() == R.id.signout){
                    signOutUser();
                    return false;
                }
                else if(item.getItemId() == R.id.nav_update)
                {

                    downloadUpdates(true);
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_update).setVisible(false);
                }

                int id = item.getItemId();
                if(id == R.id.nav_home || id == R.id.nav_profile || id == R.id.nav_how_to_use  ||
                        id == R.id.nav_about_sign_language || id == R.id.nav_history  ||
                        id == R.id.nav_aboutus || id == R.id.nav_privacy || id == R.id.nav_terms
                        || id == R.id.nav_contactus || id == R.id.nav_settings){
                    navController.navigate(id);
                    navView.setCheckedItem(id);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }







    private void signOutUser(){
        //rest of the code for sign out
        showToast("sign out clicked");
    }

    private boolean downloadUpdates(boolean isDownload){
        downloadUpdate = false;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("videos/");
        getTotalFileCount(isDownload,"videos",new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long totalFiles) {
                // Do something with the total number of files
                if(!isDownload)
                {
                    SharedPreferences SP = getSharedPreferences("SP", MODE_PRIVATE);
                    SharedPreferences.Editor editor = SP.edit();
                    int currentCount = SP.getInt("putStringCount", 0);
                    showToast("currentCount "+currentCount+" totalCount "+totalFiles);
                    if(totalFiles > currentCount)
                    {
                        downloadUpdate = true;
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        // menu.findItem(R.id.nav_home).setIconTintList(ContextCompat.getColorStateList(this, R.color.black));
                        //menu.findItem(R.id.nav_home).set(ContextCompat.getColorStateList(this, R.color.your_custom_color));
                        menu.findItem(R.id.nav_update).setVisible(true);


                        //showToast("byfbeubo");
                        Context context = getApplicationContext();
                        Long newWord = totalFiles-currentCount;
                        NotificationHelper.showNotification(context, "Update Available", "New "+newWord+" sign words available.");
                    }
                }


// Increment the counter
                //int cnt = 1;

                //editor.putInt("putStringCount", currentCount + 1);
            }
        });
        return downloadUpdate;
    }

     public void getTotalFileCount(boolean isDownload , String storagePath, final OnSuccessListener<Long> successListener) {
         FirebaseStorage storage = FirebaseStorage.getInstance();
         StorageReference storageRef = storage.getReference().child(storagePath);

         storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        // Get the total number of files
                        long totalFiles = listResult.getItems().size();

                        // Callback with the total number of files
                        successListener.onSuccess(totalFiles);

                            SharedPreferences SP = getSharedPreferences("SP", MODE_PRIVATE);
                            int currentCount = SP.getInt("putStringCount", 0);
                            int cnt = 0;
                            SharedPreferences.Editor editor = SP.edit();
if(isDownload)
{
    for (StorageReference fileRef : listResult.getItems()) {
        if(currentCount > cnt)
        {
            cnt=cnt+1;
            // editor.putInt("putStringCount", cnt+1).apply();
            //continue;
        }
        //cnt = SP.getInt("putStringCount",0);
        if(downloadFile(fileRef)) {
            //cnt = cnt + 1;
        }
        // editor.putInt("putStringCount", initialCount).apply()

    }
}

                            //cnt = cnt - 1;
                            //editor.putInt("putStringCount", cnt).apply();


                    }
                })
                .addOnFailureListener(exception -> {
                    // Handle errors
                    exception.printStackTrace();
                });
    }
    public boolean downloadFile(StorageReference storageRef)
    {
         downloadSuccessful = false;
        File localFile;
        String fileName = storageRef.getName();
        SharedPreferences videoRef = getSharedPreferences("videoRef",MODE_PRIVATE);
       // String path = videoRef.getString(fileName+".mp4",null);
        if (!videoRef.contains(fileName+".mp4")) {
            try {
                String dirName = "MyCustomDir";
                Context c = getApplicationContext();
                ContextWrapper cw = new ContextWrapper(c);
                File customDir = cw.getDir(dirName, MODE_PRIVATE);
                SharedPreferences SP = getSharedPreferences("SP", MODE_PRIVATE);
                int currentCount = SP.getInt("putStringCount", 0);
                localFile = File
                        .createTempFile(fileName, "mp4",customDir);
                // Log.i(TAG, localFile.getAbsolutePath());
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        SharedPreferences videoRef = getSharedPreferences("videoRef",MODE_PRIVATE);
                        SharedPreferences.Editor editor = videoRef.edit();
                        editor.putString(fileName,localFile.getAbsolutePath());
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Downloaded "+fileName+" "+localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        downloadSuccessful = true;
                        SharedPreferences SP = getSharedPreferences("SP", MODE_PRIVATE);
                        int currentCount = SP.getInt("putStringCount", 0);


                        //int cnt = 0;
                        SharedPreferences.Editor editorD = SP.edit();
                        editorD.putInt("putStringCount", currentCount+1).apply();

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

        return downloadSuccessful;
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