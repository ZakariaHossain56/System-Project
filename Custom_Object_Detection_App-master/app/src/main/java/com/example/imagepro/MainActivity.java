package com.example.imagepro;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import com.example.imagepro.EmergencyAssistanceHelper;
import org.opencv.android.OpenCVLoader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private Toolbar toolbar;



    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private NavController navController;
    Location currentLocation;
    boolean downloadSuccessful,downloadUpdate,colorChange = false;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_KEY = "isFirstTime";
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 456;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("usedCount");

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
                R.id.nav_settings,R.id.nav_update,R.id.nav_premium,R.id.nav_emergency,
                R.id.signout)
                .setOpenableLayout(drawerLayout)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        saveCount();
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
                else if(item.getItemId() == R.id.nav_emergency)
                {
                    String contactNumber = "+8801856770744";
                    String emergencyMessage = "Emergency! Please help!";
                  //  EmergencyAssistanceHelper.sendEmergencyMessage(getApplicationContext(), contactNumber, emergencyMessage);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, request it
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                SMS_PERMISSION_REQUEST_CODE
                        );
                    } else {
                        // Permission is already granted, you can proceed with sending SMS
                        // Your SMS sending logic goes here
                       // Toast.makeText(MainActivity.this, "GOAt", Toast.LENGTH_SHORT).show();
                        checkAndRequestLocationPermission();
                        // Get current location
                         currentLocation = getCurrentLocation();
                         if(currentLocation!=null)
                         {

                             String locationSharingLink = "https://www.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                             Toast.makeText(getApplicationContext(), locationSharingLink, Toast.LENGTH_SHORT).show();

                             EmergencyAssistanceHelper.sendEmergencyMessage(getApplicationContext(), contactNumber, emergencyMessage+"/nMy Current Location : "+locationSharingLink);
                         }

                    }
                }

                int id = item.getItemId();
                if(id == R.id.nav_home || id == R.id.nav_profile || id == R.id.nav_how_to_use  ||
                        id == R.id.nav_about_sign_language || id == R.id.nav_history  ||
                        id == R.id.nav_aboutus || id == R.id.nav_privacy || id == R.id.nav_terms
                        || id == R.id.nav_contactus || id == R.id.nav_settings || id == R.id.nav_premium){
                    navController.navigate(id);
                    navView.setCheckedItem(id);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }


    private void checkAndRequestLocationPermission() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            // Permission is already granted, you can proceed with location-related tasks
            // For example, start location updates
            // startLocationUpdates();
          //  currentLocation = getCurrentLocation();
            currentLocation = getCurrentLocation();
            if(currentLocation!=null)
            {
                String contactNumber = "+8801856770744";
                String emergencyMessage = "Emergency! Please help!";
                String locationSharingLink = "https://www.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                Toast.makeText(getApplicationContext(), locationSharingLink, Toast.LENGTH_SHORT).show();

                EmergencyAssistanceHelper.sendEmergencyMessage(getApplicationContext(), contactNumber, emergencyMessage+"/nMy Current Location : "+locationSharingLink);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with sending SMS
                // Your SMS sending logic goes here
               // Toast.makeText(this, "bolod", Toast.LENGTH_SHORT).show();

                String contactNumber = "+8801856770744";
                String emergencyMessage = "Emergency! Please help!";
                currentLocation = getCurrentLocation();
                String locationSharingLink="https://www.google.com/maps?q=";// = "https://www.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                if(currentLocation!=null) locationSharingLink = "https://www.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();

                Toast.makeText(this, locationSharingLink, Toast.LENGTH_SHORT).show();
                EmergencyAssistanceHelper.sendEmergencyMessage(getApplicationContext(), contactNumber, emergencyMessage+"/nMy Current Location : "+locationSharingLink);

            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
                Toast.makeText(this, "Goru", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with sending SMS
                // Your SMS sending logic goes here
                // Toast.makeText(this, "bolod", Toast.LENGTH_SHORT).show();

                String contactNumber = "+8801856770744";
                String emergencyMessage = "Emergency! Please help!";
                currentLocation = getCurrentLocation();
                String locationSharingLink = "https://www.google.com/maps?q=";// + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                if(currentLocation!=null) locationSharingLink = "https://www.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();

                Toast.makeText(this, locationSharingLink, Toast.LENGTH_SHORT).show();
                EmergencyAssistanceHelper.sendEmergencyMessage(getApplicationContext(), contactNumber, emergencyMessage+"/nMy Current Location : "+locationSharingLink);

            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
                // Toast.makeText(this, "Goru", Toast.LENGTH_SHORT).show();
                currentLocation = getCurrentLocation();
            }
        }
    }

    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        return null;
    }

    // Handle the result of the permission request


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

    public void saveCount()
    {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("tokenUsed", MODE_PRIVATE);

// Get all keys in the SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();
        Toast.makeText(this, allEntries.size() +"  hahahahaha", Toast.LENGTH_SHORT).show();
// Iterate over the keys and read the corresponding values
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value1 = entry.getValue().toString();
            int value = Integer.parseInt(value1);
            // Depending on the data types you stored, you can cast the value to the appropriate type
            int intValue = (Integer) value;
            // Handle integer value
            SharedPreferences.Editor editorSP = sharedPreferences.edit();
            final DatabaseReference finalRef=dbref.child(key);
            finalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method is called once with the value at the specified location.
                    if (dataSnapshot.exists()) {
                        Integer value = dataSnapshot.getValue(Integer.class);

                        Log.d(TAG, "Value: " + value);
                        Integer newValue = value + intValue;

                        // Update the value in the database
                        //finalRef.setValue(newValue);
                        dbref.child(key).setValue(newValue);
                        editorSP.putInt(key,0);
                        editorSP.apply();
                        // Now you have the single value, you can use it as needed

                    } else {
                        dbref.child(key).setValue(intValue);
                        editorSP.putInt(key,0);
                        editorSP.apply();
                        Log.d(TAG, "Key not found");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });

           /* if (value instanceof String) {
                String stringValue = (String) value;
                // Handle string value
                Toast.makeText(this, "String bal", Toast.LENGTH_SHORT).show();
            } else if (value instanceof Integer) {

            } else if (value instanceof Boolean) {
                boolean boolValue = (Boolean) value;
                // Handle boolean value
            }*/
            // Add more conditions for other data types as needed
            // ...
        }



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