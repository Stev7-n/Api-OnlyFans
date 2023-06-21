package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class perfil_ajustes extends AppCompatActivity {

    ImageButton home;
    ImageButton explorar;
    ImageButton posts;
    ImageButton perfil;
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

        home = findViewById(R.id.home);
        explorar = findViewById(R.id.explorar);
        posts = findViewById(R.id.posts);
        perfil = findViewById(R.id.perfil);
        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);

        firebaseAuth = FirebaseAuth.getInstance();

        nombreUsuarioPerfil = findViewById(R.id.nombreUsuarioPerfil);
        nombreUsuarioPerfilJunto = findViewById(R.id.nombreUsuarioPerfilJunto);

        editarPerfil = findViewById(R.id.editarPerfil);

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