package com.example.myapplicationfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationfinal.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding b;
    FirebaseAuth fbu;
    FirebaseUser fbu1;
    FirebaseFirestore fbs;
    FirebaseStorage fbsStorage;
    StorageReference storageReference;
    ActivityResultLauncher<Intent> url;
    Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        fbu = FirebaseAuth.getInstance();

        // Initialize Firebase components
        fbu1 = fbu.getCurrentUser();
        fbs = FirebaseFirestore.getInstance();
        fbsStorage = FirebaseStorage.getInstance();
        storageReference = fbsStorage.getReference();

        b.gobacktoSettingsfromEditprofile.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Settings.class);
            startActivity(i);
            finish();
        });

        b.btnSave.setOnClickListener(v -> saveProfileChanges());

        // Load current user's profile data if available
        loadProfileData();

        b.EditProfilePicture.setImageURI(imguri);

        b.EditProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            url.launch(intent);
        });

        url = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if (o.getResultCode() == RESULT_OK) {
                    Intent take = o.getData();
                    imguri = take.getData();
                    b.EditProfilePicture.setImageURI(imguri);
                }
            }
        });


    }

    private void loadProfileData() {
        fbs.collection("users").document(fbu1.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String username = documentSnapshot.getString("username");
                        String location = documentSnapshot.getString("location");
                        String mobile = documentSnapshot.getString("mobile");
                        String bio = documentSnapshot.getString("bio");
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl"); // Assuming this field exists

                        // Set retrieved data into EditText fields
                        b.etName.setText(name);
                        b.etLocation.setText(location);
                        b.etMobile.setText(mobile);
                        b.etBio.setText(bio);


                    } else {
                        Toast.makeText(EditProfile.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveProfileChanges() {
        String name = b.etName.getText().toString().trim();
        String location = b.etLocation.getText().toString().trim();
        String mobile = b.etMobile.getText().toString().trim();
        String bio = b.etBio.getText().toString().trim();

        // Upload image if it was changed
        if (imguri != null) {
            StorageReference fileRef = storageReference.child("profile_images/" + fbu1.getUid() + ".jpg");
            fileRef.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            saveProfileDataToFirestore(name, location, mobile, bio, imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Retrieve the current profile picture URL from Firestore
            fbs.collection("users").document(fbu1.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String currentImageUrl = documentSnapshot.getString("img");
                        saveProfileDataToFirestore(name, location, mobile, bio, currentImageUrl);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfile.this, "Failed to retrieve current profile picture URL", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void saveProfileDataToFirestore(String name, String location, String mobile, String bio, String imageUrl) {
        DocumentReference docRef = fbs.collection("users").document(fbu1.getUid());
        docRef.update("name", name,
                        "location", location,
                        "mobile", mobile,
                        "bio", bio,
                        "img", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Profile.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }
}
