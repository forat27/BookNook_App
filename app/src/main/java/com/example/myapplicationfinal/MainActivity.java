package com.example.myapplicationfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationfinal.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth fbu;
    String SharedPreferences1 = "DB";
    static FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = getSharedPreferences(SharedPreferences1, MODE_PRIVATE);
        String c = sp.getString("Name", "").toString();
        if (c.equals("true")) {
            String Email = sp.getString("email", "").toString();
            String Pass = sp.getString("pass", "").toString();
            LOGINFB(Email, Pass);
        }

        binding.LogInBtID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.EmailLIID.getText().toString().trim();
                String pass = binding.PasswordLIID.getText().toString().trim();
                // Added email validation
                if (!isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Added password validation
                if (!isValidPassword(pass)) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                LOGINFB(email, pass);
            }
        });

        binding.GoToSUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.ForgotPassID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            }
        });
    }

    // Added email validation method
    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Added password validation method
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public void LOGINFB(String email, String pass) {
        fbu = FirebaseAuth.getInstance();
        fbu.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = authResult.getUser();
                if (binding.RememberMeID.isChecked()) {
                    SharedPreferences sp = getSharedPreferences(SharedPreferences1, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Name", "true");
                    editor.putString("email", email);
                    editor.putString("pass", pass);
                    editor.apply();
                }

                Toast.makeText(getApplicationContext(), "You're Logged In.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String[] m = e.toString().split(":");
                Toast.makeText(getApplicationContext(), m[1], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
