package com.example.myapplicationfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationfinal.databinding.ActivityAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Add extends AppCompatActivity {

    FirebaseStorage storage;
    ActivityAddBinding b;
    StorageReference ref;
    BottomNavigationView bv;
    FirebaseFirestore fbs;
    FirebaseAuth fba;
    ActivityResultLauncher<Intent> url;
    Map<String, Object> data;
    Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        b = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        fbs = FirebaseFirestore.getInstance();
        bv = b.menu;

        fba = FirebaseAuth.getInstance();
        ref = FirebaseStorage.getInstance().getReference();
        bv.setSelectedItemId(R.id.add);

        b.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                url.launch(intent);
            }
        });

        url = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == RESULT_OK) {
                    Intent take = o.getData();
                    imguri = take.getData();
                    b.imgView.setImageURI(imguri);
                }
            }
        });

        b.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUploadOnClick();
            }
        });

        bv.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.add) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainPage.class));
                finish();
                return true;
            } else
                return false;
        });
    }

    private void btnUploadOnClick() {
        String title = b.booktitleADD.getText().toString().trim();
        String price = b.bookpriceADD.getText().toString().trim();
        String location = b.locationADD.getText().toString().trim();
        String bkstname = b.bookstoreADD.getText().toString().trim();

        if (title.length() == 0) {
            Toast.makeText(Add.this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        } else if (price.length() == 0) {
            Toast.makeText(Add.this, "Please enter a price", Toast.LENGTH_SHORT).show();
            return;
        } else if (location.length() == 0) {
            Toast.makeText(Add.this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        } else if (bkstname.length() == 0) {
            Toast.makeText(Add.this, "Please enter a bookstore name", Toast.LENGTH_SHORT).show();
            return;
        }

        data = new HashMap<>();
        String id = UUID.randomUUID().toString();

        data.put("ID", id);
        data.put("Book", title);
        data.put("BookStore", bkstname);
        data.put("Price", price);
        data.put("Location", location);
        data.put("userId", fba.getUid());
        data.put("timestamp", System.currentTimeMillis());

        StorageReference file = ref.child(id);
        file.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        data.put("img", uri.toString());
                        fba = FirebaseAuth.getInstance();
                        fbs.collection("Data").document(fba.getUid()).collection("Data")
                                .document(id).set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Add.this, "Added", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Add.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        data.put("IDusers", fba.getUid());
                        fbs.collection("All Data").document(id).set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Add.this, "Post Added", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Add.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Intent i = new Intent(getApplicationContext(), MainPage.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        });
    }
}
