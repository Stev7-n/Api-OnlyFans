package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class perfil_ajustes extends AppCompatActivity {

    ImageView cambiarFotoPerfil;
    FirebaseAuth firebaseAuth;

    TextView nombreUsuarioPerfil;
    TextView nombreUsuarioPerfilJunto;
    Button editarPerfil;
    RecyclerView recycler_publicaciones_propias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_ajustes);

        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);

        firebaseAuth = FirebaseAuth.getInstance();

        nombreUsuarioPerfil = findViewById(R.id.nombreUsuarioPerfil);
        nombreUsuarioPerfilJunto = findViewById(R.id.nombreUsuarioPerfilJunto);

        editarPerfil = findViewById(R.id.editarPerfil);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.buttonHome) {
                    Intent intentHome = new Intent(perfil_ajustes.this, menu_inicial.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonSugerencias) {
                    Intent intentSugerencias = new Intent(perfil_ajustes.this, sugerencias_perfiles.class);
                    startActivity(intentSugerencias);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonPost) {
                    Intent intentPost = new Intent(perfil_ajustes.this, subir_posts.class);
                    startActivity(intentPost);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonProfile) {
                    Intent intentProfile = new Intent(perfil_ajustes.this, perfil_ajustes.class);
                    startActivity(intentProfile);
                    finish();
                    return true;
                }

                return false;
            }
        });

        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, ajustar_perfil.class);
                startActivity(intent);
            }
        });

        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    String fullName = firstName + " " + lastName;
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
}