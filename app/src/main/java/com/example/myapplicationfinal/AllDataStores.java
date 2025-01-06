package com.example.myapplicationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplicationfinal.databinding.ActivityAllDataStoresBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AllDataStores extends AppCompatActivity {

    private ActivityAllDataStoresBinding b;
    private FirebaseFirestore fbs;
    private FirebaseAuth fbu;
    private ArrayList<UserProfile> listStore;
    private UserProfileAdapter adapter;
    private TextView randomTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityAllDataStoresBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        fbs = FirebaseFirestore.getInstance();
        fbu = FirebaseAuth.getInstance();

        listStore = new ArrayList<>();
        adapter=new UserProfileAdapter(this,listStore);
        b.Recyclermain22.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b.Recyclermain22.setLayoutManager(layoutManager);
        b.Recyclermain22.setHasFixedSize(true);

        randomTextView = findViewById(R.id.randomTextView); // Adjust id based on your layout

        loadLastUserProfiles();

        b.alldatastores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                startActivity(i);
                finish();
            }
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
                    Toast.makeText(this, "Failed to load user profiles", Toast.LENGTH_SHORT).show();
                   // Log.e(TAG, "Failed to load user profiles", e);
                });
    }

    private void loadUserProfiles(Set<String> userIds) {
        for (String userId : userIds) {
            fbs.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String profilePicUrl = documentSnapshot.getString("img");
                            listStore.add(new UserProfile(name, profilePicUrl,userId));
                            adapter.notifyDataSetChanged();
                          //  Log.d(TAG, "User profile loaded: " + name);
                        } else {
                         //   Log.d(TAG, "User profile not found for ID: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                        //Log.e(TAG, "Failed to load user profile for ID: " + userId, e);
                    });
        }
    }
    private void loadData() {
        fbs.collection("All Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    UserProfile m = new UserProfile(
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("profilePicUrl"),
                            documentSnapshot.getString("userId")

                    );
                    listStore.add(m);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
