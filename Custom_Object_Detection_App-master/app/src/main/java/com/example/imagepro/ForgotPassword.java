package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    ConstraintLayout theme_forgotpass;
    EditText forgotemail;
    Button send;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("dm",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("dark",true)){
            theme_forgotpass.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        else{
            theme_forgotpass.setBackgroundResource((R.drawable.splash_background));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotemail = findViewById(R.id.forgotemail);
        send = findViewById(R.id.send);

        theme_forgotpass = findViewById(R.id.theme_forgotpass);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = forgotemail.getText().toString();
                if (!mail.isEmpty()) {
                    mAuth.sendPasswordResetEmail(mail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(ForgotPassword.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                } else {
                    Toast.makeText(ForgotPassword.this, "Enter your email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}