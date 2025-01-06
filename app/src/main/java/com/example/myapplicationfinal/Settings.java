package com.example.myapplicationfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationfinal.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding b;
    FirebaseAuth FBU;
    String SharedPreferences1 = "DB";


    private Text btnShareApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        FBU=FirebaseAuth.getInstance();



        b.btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppLink();
            }
        });


        b.goback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profile.class);
                startActivity(i);
                finish();
            }
        });

        b.editprofilesettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),EditProfile.class);
                startActivity(i);
                finish();
            }
        });



        b.LOGOUTID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBU.signOut();

                SharedPreferences sp = getSharedPreferences(SharedPreferences1, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Name"," ");
                editor.apply();
                Toast.makeText(getApplicationContext(), "You're Logged Out.", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }


    private void shareAppLink() {
        // Replace with your actual app link
        String appLink = "https://play.google.com/store/apps/details?id=com.example.yourapp";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: " + appLink);
        shareIntent.setType("text/plain");

        // Start the sharing activity
        startActivity(Intent.createChooser(shareIntent, "Share app via"));
    }

}