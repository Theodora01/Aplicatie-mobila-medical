package com.example.aplicatiemedicala;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Articole extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etDoctorName, etSpecialization, etArticleLink;
    private ImageView imageView;
    private Uri imageUri;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articole);

        etDoctorName = findViewById(R.id.etDoctorName);
        etSpecialization = findViewById(R.id.etSpecialization);
        etArticleLink = findViewById(R.id.etArticleLink);
        imageView = findViewById(R.id.ivSelectedImage);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSaveArticle = findViewById(R.id.btnSaveArticle);

        storage = FirebaseStorage.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("articole");

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSaveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArticle();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void saveArticle() {
        final String doctorName = etDoctorName.getText().toString().trim();
        final String specialization = etSpecialization.getText().toString().trim();
        final String articleLink = etArticleLink.getText().toString().trim();

        if (doctorName.isEmpty() || specialization.isEmpty() || articleLink.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Completează toate câmpurile și alege o imagine", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                Map<String, Object> articleData = new HashMap<>();
                                articleData.put("doctorName", doctorName);
                                articleData.put("specialization", specialization);
                                articleData.put("articleLink", articleLink);
                                articleData.put("imageUrl", imageUrl);

                                databaseRef.push().setValue(articleData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Articole.this, "Articol adăugat cu succes", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Articole.this, "Eroare la adăugarea articolului", Toast.LENGTH_SHORT).show();
                                                Log.e("Articole", "Eroare la adăugarea articolului", e);
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Articole.this, "Eroare la încărcarea imaginii", Toast.LENGTH_SHORT).show();
                        Log.e("Articole", "Eroare la încărcarea imaginii", e);
                    }
                });
    }
}
