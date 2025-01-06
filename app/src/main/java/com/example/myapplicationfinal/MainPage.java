package com.example.myapplicationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationfinal.databinding.ActivityMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainPage extends AppCompatActivity {

    private static final String TAG = "MainPage";

    ActivityMainPageBinding b;
    FirebaseAuth fbu;
    FirebaseFirestore fbs;
    ArrayList<book_card> list;
    Adapter adapter;

    TextView welcomeTextView;
    RecyclerView userProfilesRecyclerView;
    UserProfileAdapter userProfileAdapter;
    List<UserProfile> userProfileList;


    private TextView randomTextView;
    private Handler handler = new Handler();
    // Define your custom texts here
    private String[] randomTexts = {
            "Welcome to our app!",
            "A reader lives a thousand lives before he dies.",
            "The best books are those who tell you what you know already.",
            "Books are a uniquely portable magic.",
            "A book is a gift you can open again and again",
            "Have a great day!",
            "Today a reader, tomorrow a leader.",
            "A word after a word after a word is power.",
            "If you don’t like to read, you haven’t found the right book.",
            "Some books leave us free and some books make us free.",
            "Once you learn to read, you will be forever free.",
            "A room without books is like a body without a soul.",
            "Books may well be the only true magic."

    };
    private Random random = new Random();


    private boolean isStoreOwner = false; // Flag to check if the user is a store owner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        fbs = FirebaseFirestore.getInstance();
        fbu = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        adapter = new Adapter(this, list);
        b.Recyclermain.setAdapter(adapter);
        LinearLayoutManager hh = new LinearLayoutManager(getApplicationContext());
        hh.setOrientation(LinearLayoutManager.HORIZONTAL);
        b.Recyclermain.setLayoutManager(hh);
        b.Recyclermain.setHasFixedSize(true);
        b.menu.setSelectedItemId(R.id.home);

        welcomeTextView = findViewById(R.id.welcomeTextView); // Assuming this is the ID of TextView in activity_main_page.xml

        // Initialize user profiles RecyclerView
        userProfilesRecyclerView = findViewById(R.id.Recyclermain22);
        userProfilesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userProfileList = new ArrayList<>();
        userProfileAdapter = new UserProfileAdapter(this, userProfileList);
        userProfilesRecyclerView.setAdapter(userProfileAdapter);

        loadProfilePicture();
        loadUsername();


        // Check if the user is a store owner
        checkIfStoreOwner();

        randomTextView = findViewById(R.id.randomTextView);

        // Start the text update
        startTextUpdate();



        b.ViewAllID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllData.class);
                intent.putExtra("bookList", "1");
                startActivity(intent);
            }
        });

        b.viewallstores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllDataStores.class);
                startActivity(intent);
            }
        });

        b.menu.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
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


        Loaddata();
        loadLastUserProfiles();
    }


    private void startTextUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set a random text
                int index = random.nextInt(randomTexts.length);
                randomTextView.setText(randomTexts[index]);

                // Schedule the next update
                handler.postDelayed(this, 3000); // Update every 3 seconds
            }
        }, 4000); // Initial delay
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }


    private void checkIfStoreOwner() {
        if (MainActivity.user==null){
            b.profilePic.setVisibility(View.INVISIBLE);
            b.profilePicbrowser.setVisibility(View.VISIBLE);
            b.welcomeTextView.setVisibility(View.INVISIBLE);
            b.welcomeTextViewbrowser.setVisibility(View.VISIBLE);
            b.menu.setVisibility(View.INVISIBLE);

        }else {
            b.profilePic.setVisibility(View.VISIBLE);
            b.profilePicbrowser.setVisibility(View.INVISIBLE);
            b.welcomeTextView.setVisibility(View.VISIBLE);
            b.welcomeTextViewbrowser.setVisibility(View.INVISIBLE);
            b.menu.setVisibility(View.VISIBLE);
        }
    }

    private void loadUsername() {
        fbs.collection("users").document(fbu.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        if (username != null && !username.isEmpty()) {
                            welcomeTextView.setText("Hi, " + username);

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void Loaddata() {
        fbs.collection("All Data").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                book_card m = new book_card(
                        documentSnapshot.getString("Book"),
                        documentSnapshot.getString("BookStore"),
                        documentSnapshot.getString("Price"),
                        documentSnapshot.getString("img"),
                        documentSnapshot.getString("IDusers"),
                        documentSnapshot.getString("Location")
                );
                list.add(m);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void loadLastUserProfiles() {
        fbs.collection("All Data").orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> userIds = new HashSet<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String userId = document.getString("IDusers");
                        if (userId != null) {
                            userIds.add(userId);
                        }
                    }
                    loadUserProfiles(userIds);
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainPage.this, "Failed to load user profiles", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load user profiles", e);
                });
    }

    private void loadUserProfiles(Set<String> userIds) {
        for (String userId : userIds) {
            fbs.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String profilePicUrl = documentSnapshot.getString("img");
                            userProfileList.add(new UserProfile(name, profilePicUrl, userId));
                            userProfileAdapter.notifyDataSetChanged();
                            Log.d(TAG, "User profile loaded: " + name);
                        } else {
                            Log.d(TAG, "User profile not found for ID: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to load user profile for ID: " + userId, e);
                    });
        }
    }

    private void loadProfilePicture() {

        fbs.collection("users").document(fbu.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePicUrl = documentSnapshot.getString("img");

                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                            Picasso.get().load(profilePicUrl)
                                    .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image while loading
                                    .error(R.drawable.baseline_account_circle_24) // Image to show if loading fails
                                    .into(b.profilePic);
                        } else {
                            Toast.makeText(MainPage.this, "No profile picture found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainPage.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainPage.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                });
    }
}
