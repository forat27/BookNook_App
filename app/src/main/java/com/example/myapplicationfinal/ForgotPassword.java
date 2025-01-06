package com.example.myapplicationfinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationfinal.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding b;
    Button btnReset, gobackFP;
    EditText edtEmail;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String strEmail;
    ActivityForgotPasswordBinding binding;


    @SuppressLint("WrongViewCast")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        b = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();


        b.gobackFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        b.createnewacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        edtEmail = findViewById(R.id.email_FP);
        mAuth = FirebaseAuth.getInstance();
//Reset Button Listener
        binding.btnResetFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty((strEmail))) {
                    ResetPassword();
                } else {
                    edtEmail.setError("Email field can't be empty");
                }
            }

        });

        binding.gobackFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }

            ///BUT WHY???????????
            private void onBackPressed() {
            }
        });

    }

        private void ResetPassword() {
            binding.btnResetFP.setVisibility(View.INVISIBLE);
            binding.forgetPasswordProgressbar.setVisibility(View.VISIBLE);

            mAuth. sendPasswordResetEmail (strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ForgotPassword.this,"Reset Password link has been sent to you registered email.",Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(ForgotPassword.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String[] m = e.toString().split(":");
                    Toast.makeText(getApplicationContext(), m[1], Toast.LENGTH_SHORT).show();
                    binding.forgetPasswordProgressbar.setVisibility(View.VISIBLE);
                    binding.btnResetFP.setVisibility(View.INVISIBLE);



                }
            });
        }

}