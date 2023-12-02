package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imagepro.ui.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    EditText etusername, etemail, etaddress, etphone;
    Button save;
    ConstraintLayout theme_editprofile;

    private String ename, eemail, eaddress, ephone;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_editprofile.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_editprofile.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        theme_editprofile = findViewById(R.id.theme_editprofile);

        etusername = findViewById(R.id.etusername);
        etemail = findViewById(R.id.etemail);
        etaddress = findViewById(R.id.etaddress);
        etphone = findViewById(R.id.etphone);
        save = findViewById(R.id.save);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");


        etusername.setText(name);
        etemail.setText(email);
        etaddress.setText(address);
        etphone.setText(phone);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etusername.getText().toString();
                String email = etemail.getText().toString();
                String address = etaddress.getText().toString();
                String phone = etphone.getText().toString();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Reference to the "userinfo" node for the specific user
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userinfo").child(userId);

                    // Use setValue to update the data
                    userRef.child("name").setValue(name);
                    userRef.child("mail").setValue(email);
                    userRef.child("address").setValue(address);
                    userRef.child("phone").setValue(phone);

                    Toast.makeText(EditProfile.this, "Userinfo updated successfully", Toast.LENGTH_SHORT).show();

                    Intent intent1 = new Intent(EditProfile.this, ProfileFragment.class);
                    startActivity(intent1);

                } else {
                    Toast.makeText(EditProfile.this, "Error updating userinfo", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}