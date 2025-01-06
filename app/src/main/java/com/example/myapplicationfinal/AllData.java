package com.example.myapplicationfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplicationfinal.databinding.ActivityAllDataBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllData extends AppCompatActivity {

    private ActivityAllDataBinding b;
    private FirebaseFirestore fbs;
    private FirebaseAuth fbu;
    private ArrayList<book_card> list;
    private Adapter adapter;
    private TextView randomTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityAllDataBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        fbs = FirebaseFirestore.getInstance();
        fbu = FirebaseAuth.getInstance();

        list = new ArrayList<>();
        adapter=new Adapter(this,list);
        b.Recyclermain.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b.Recyclermain.setLayoutManager(layoutManager);
        b.Recyclermain.setHasFixedSize(true);

        randomTextView = findViewById(R.id.randomTextView); // Adjust id based on your layout

        loadData();

        b.alldatagoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainPage.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void loadData() {
        fbs.collection("All Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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
            }
        });
    }
}
