package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    ImageView cambiarFotoPerfil;
    FirebaseAuth firebaseAuth;

    TextView nombreUsuarioPerfil;
    TextView nombreUsuarioPerfilJunto;
    Button editarPerfil;

    Button publicaciones_propias;
    Button seguidores_usuario;
    RecyclerView recycler_publicaciones_propias;
    RecyclerView recycler_seguidores_usuario;


    private boolean mostrarPublicacionesPropias = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_ajustes);

        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);

        firebaseAuth = FirebaseAuth.getInstance();

        nombreUsuarioPerfil = findViewById(R.id.nombreUsuarioPerfil);
        nombreUsuarioPerfilJunto = findViewById(R.id.nombreUsuarioPerfilJunto);

        editarPerfil = findViewById(R.id.editarPerfil);
        publicaciones_propias = findViewById(R.id.publicaciones_propias);
        seguidores_usuario = findViewById(R.id.seguidores_usuario);


        recycler_seguidores_usuario = findViewById(R.id.recycler_seguidores_usuario);

        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(perfil_ajustes.this, ajustar_perfil.class);
                startActivity(intent);
            }
        });

        publicaciones_propias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mostrarPublicacionesPropias) {
                    mostrarRecyclerPublicacionesPropias();
                    mostrarPublicacionesPropias = true;
                }
            }
        });

        seguidores_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mostrarPublicacionesPropias) {
                    mostrarRecyclerSeguidoresUsuario();
                    mostrarPublicacionesPropias = false;
                }
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

    private void mostrarRecyclerPublicacionesPropias() {
        recycler_publicaciones_propias.setVisibility(View.VISIBLE);
        recycler_seguidores_usuario.setVisibility(View.GONE);
    }

    private void mostrarRecyclerSeguidoresUsuario() {
        recycler_seguidores_usuario.setVisibility(View.VISIBLE);
        recycler_publicaciones_propias.setVisibility(View.GONE);
    }
}
