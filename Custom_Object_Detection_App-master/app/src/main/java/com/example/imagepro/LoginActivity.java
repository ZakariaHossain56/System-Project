package com.example.imagepro;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText email , password ;
    String mail , pass ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressBar pg ;
    Button login ;
    TextView goToSignup, forgotpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        goToSignup = findViewById(R.id.gotosignup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        pg = findViewById(R.id.progressbar);
        login = findViewById(R.id.Login);
        forgotpass = findViewById(R.id.forgotpass);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            Intent i = new Intent(LoginActivity.this , MainActivity.class);
            startActivity(i);
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                mail = email.getText().toString();
                pass = password.getText().toString();
                if(TextUtils.isEmpty(mail)){
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pg.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user!=null)
                                    {
                                        SharedPreferences sharedPreferences = getSharedPreferences("sp",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email",mail);
                                        //editor.putString("password",pass);
                                        editor.apply();
                                        Intent i = new Intent(LoginActivity.this , MainActivity.class);
                                        startActivity(i);
                                    }
                                    finish();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                    //updateUI(null);
                                }
                            }
                        });
            }
        });
        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this , SignupActivity.class);
                startActivity(i);
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }
}