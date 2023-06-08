package com.example.apionlyfans;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class perfil_ajustes extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageButton home;
    ImageButton explorar;
    ImageButton posts;
    ImageButton perfil;
    ImageView cambiarFotoPerfil;
    FirebaseAuth firebaseAuth;

    TextView nombre_usuario;
    TextView nombreUsuarioPerfil;
    TextView nombreUsuarioPerfilJunto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_ajustes);

        home = findViewById(R.id.home);
        explorar = findViewById(R.id.explorar);
        posts = findViewById(R.id.posts);
        perfil = findViewById(R.id.perfil);
        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);

        firebaseAuth = FirebaseAuth.getInstance();

        nombre_usuario = findViewById(R.id.nombre_usuario);
        nombreUsuarioPerfil = findViewById(R.id.nombreUsuarioPerfil);
        nombreUsuarioPerfilJunto = findViewById(R.id.nombreUsuarioPerfilJunto);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, menu_inicial.class);
                startActivity(intent);
                finish();
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, sugerencias_perfiles.class);
                startActivity(intent);
                finish();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, subir_posts.class);
                startActivity(intent);
                finish();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, perfil_ajustes.class);
                startActivity(intent);
                finish();
            }
        });

        cambiarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFotoDeGaleria();
            }
        });

        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    String fullName = firstName + " " + lastName;
                    nombre_usuario.setText(fullName);
                    nombreUsuarioPerfil.setText(fullName);
                    nombreUsuarioPerfilJunto.setText("@" + firstName.toLowerCase() + lastName.toLowerCase());

                    String fotoPerfilUrl = dataSnapshot.child("fotoPerfil").getValue(String.class);

                    if (fotoPerfilUrl != null) {
                        Glide.with(perfil_ajustes.this).load(fotoPerfilUrl).into(cambiarFotoPerfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void seleccionarFotoDeGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String imageFilePath = obtenerRutaDeArchivo(selectedImageUri);

            Glide.with(this).load(selectedImageUri).into(cambiarFotoPerfil);

            subirFotoPerfil(selectedImageUri);
        }
    }
    private void subirFotoPerfil(Uri imageUri) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("perfil_fotos").child(userId);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        currentUserRef.child("fotoPerfil").setValue(imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    });
                })
                .addOnFailureListener(e -> {
                });
    }

    private String obtenerRutaDeArchivo(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}