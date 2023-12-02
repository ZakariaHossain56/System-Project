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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText username , email , password , confirm_password ;

    TextView gotosignin;
    String name , mail , pass , confirm_pass, address, phone;
    Button register ;
    FirebaseAuth mAuth ;
    ProgressBar pg ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        register = findViewById(R.id.register);
        pg = findViewById(R.id.progressbar);
        gotosignin = findViewById(R.id.gotosignin);

        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this , LoginActivity.class);
                startActivity(i);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                name = username.getText().toString();
                mail = email.getText().toString();
                //pass = password.getText().toString();
                pass = String.valueOf(password.getText());
                //confirm_pass = confirm_password.getText().toString();
                confirm_pass = String.valueOf(confirm_password.getText());
                address = "Not provided";
                phone = "Not provided";

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignupActivity.this, "Enter your username", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(SignupActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(SignupActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(confirm_pass)) {
                    Toast.makeText(SignupActivity.this, "Re-enter your password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!pass.equals(confirm_pass)) {
                    Toast.makeText(SignupActivity.this, "Password and Confirm Password fields must be same", Toast.LENGTH_SHORT).show();
                }
                else {
                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pg.setVisibility(View.GONE);
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid().toString();
                                        userInfo userinfo = new userInfo(name, mail, address, phone);
                                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("userinfo");
                                        dbref.child(uid).setValue(userinfo);
                                        SharedPreferences sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email", mail);
                                        //editor.putString("password",pass);
                                        editor.apply();
                                        Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                }
                            }
                        });
            }
            }
        });

       // register = findViewById(R.id.register);
    }
}