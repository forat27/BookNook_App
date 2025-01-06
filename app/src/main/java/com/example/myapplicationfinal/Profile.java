package com.example.myapplicationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplicationfinal.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private FirebaseFirestore fbs;
    private String userId;
    private TextView usernameTextView, nameTextView, locationTextView, mobileTextView, bioTextView;
    private ImageView profilePicImageView;
    private Adapter adapter;
    private ArrayList<book_card> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       userId= getIntent().getStringExtra("userId");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        } else {
            if (userId==null)
                userId = currentUser.getUid();
        }
        binding.menu.setVisibility(MainActivity.user == null? View.INVISIBLE:View.VISIBLE);
        binding.menu.setSelectedItemId(R.id.profile);
        binding.Settingsv.setVisibility(MainActivity.user == null? View.INVISIBLE:View.VISIBLE);
        fbs = FirebaseFirestore.getInstance();
        usernameTextView = binding.usernameTextView;
        nameTextView = binding.nameTextView;
        locationTextView = binding.locationTextView;
        mobileTextView = binding.mobileTextView;
        bioTextView = binding.bioTextView;
        profilePicImageView = binding.profilePic;

        binding.menu.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profile) {
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainPage.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.add) {
                startActivity(new Intent(getApplicationContext(), Add.class));
                finish();
                return true;
            } else {
                return false;
            }
        });

        binding.Settingsv.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
            finish();
        });

        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.Recyclermain.setLayoutManager(layoutManager);
        binding.Recyclermain.setAdapter(adapter);

        loadUserProfile();
        loadData();
    }

    private void loadUserProfile() {
        fbs.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String name = documentSnapshot.getString("name");
                        String location = documentSnapshot.getString("location");
                        String mobile = documentSnapshot.getString("mobile");
                        String bio = documentSnapshot.getString("bio");
                        String imageUrl = documentSnapshot.getString("img");

                        usernameTextView.setText(username);
                        nameTextView.setText(name);
                        locationTextView.setText(location);
                        mobileTextView.setText(mobile);
                        bioTextView.setText(bio);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(profilePicImageView);
                        } else {
                            Toast.makeText(Profile.this, "No profile picture found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Profile.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadData() {
        fbs.collection("Data").document(userId).collection("Data").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        book_card m = new book_card(
                                documentSnapshot.getString("Book"),
                                documentSnapshot.getString("BookStore"),
                                documentSnapshot.getString("Price"),
                                documentSnapshot.getString("img"),
                                userId,
                                documentSnapshot.getString("Location")
                        );
                        list.add(m);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
