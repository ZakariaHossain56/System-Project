package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imagepro.ui.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText newpass, confirmnewpass;
    private Button passsave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newpass = findViewById(R.id.newpass);
        confirmnewpass = findViewById(R.id.confirmnewpassword);
        passsave = findViewById(R.id.passsave);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String newPassword = newpass.getText().toString();
        String confirmnewPassword = confirmnewpass.getText().toString();

        if(TextUtils.isEmpty(newPassword)){
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
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePassword.this, SettingsFragment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ChangePassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Enter a new password", Toast.LENGTH_SHORT).show();
        }
    }
}