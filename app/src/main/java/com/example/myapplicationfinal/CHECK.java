package com.example.myapplicationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CHECK extends AppCompatActivity {

    Button buttonStoreOwner, buttonBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        buttonStoreOwner = findViewById(R.id.buttonStoreOwner);
        buttonBrowser = findViewById(R.id.buttonBrowser);

        buttonStoreOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CHECK.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.user = null;
                Intent intent = new Intent(CHECK.this, MainPage.class);
                startActivity(intent);
            }
        });
    }
}
