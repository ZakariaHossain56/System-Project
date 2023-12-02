package com.example.imagepro;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imagepro.ui.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText oldpass, newpass, confirmnewpass;
    private Button passsave;
    ConstraintLayout theme_changepass;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_changepass.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_changepass.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldpass = findViewById(R.id.oldpass);
        newpass = findViewById(R.id.newpass);
        confirmnewpass = findViewById(R.id.confirmnewpassword);
        passsave = findViewById(R.id.passsave);

        theme_changepass = findViewById(R.id.theme_changepass);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        passsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = oldpass.getText().toString();
                String newPassword = newpass.getText().toString();
                String confirmnewPassword = confirmnewpass.getText().toString();

                if(TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(ChangePassword.this, "Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(newPassword)){
                    Toast.makeText(ChangePassword.this, "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(confirmnewPassword)){
                    Toast.makeText(ChangePassword.this, "Confirm new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!newPassword.equals(confirmnewPassword)){
                    Toast.makeText(ChangePassword.this, "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (!newPassword.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);
                    String mail = sharedPreferences.getString("email",null);
                    //String pass = sharedPreferences.getString("password",null);
                    String pass = oldPassword;
                    mAuth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(ChangePassword.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        user.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ChangePassword.this, SettingsFragment.class);
                                                            startActivity(intent);
                                                        } else {
                                                            System.out.println(task.getException().getMessage());
                                                            Toast.makeText(ChangePassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(ChangePassword.this, "Wrong password.",
                                                Toast.LENGTH_SHORT).show();

                                        //updateUI(null);
                                    }
                                }
                            });


                } else {
                    Toast.makeText(ChangePassword.this, "Enter a new password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}