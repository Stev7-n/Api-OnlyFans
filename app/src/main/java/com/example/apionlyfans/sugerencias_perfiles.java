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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class sugerencias_perfiles extends AppCompatActivity {

    ImageButton home;
    ImageButton explorar;
    ImageButton posts;
    ImageButton perfil;
    FirebaseAuth firebaseAuth;

    ImageView fotoPerfilUsuario;
    TextView nombrePerfil;
    TextView usuarioPerfil;
    Button follow;
    RecyclerView recyclerViewPerfiles;
    adaptadorPerfiles adaptador;
    List<adaptadorPerfiles.Perfil> perfilList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias_perfiles);

        home = findViewById(R.id.home);
        explorar = findViewById(R.id.explorar);
        posts = findViewById(R.id.posts);
        perfil = findViewById(R.id.perfil);

        firebaseAuth = FirebaseAuth.getInstance();

        fotoPerfilUsuario = findViewById(R.id.fotoPerfilUsuario);
        nombrePerfil = findViewById(R.id.nombrePerfil);
        usuarioPerfil = findViewById(R.id.usuarioPerfil);
        follow = findViewById(R.id.follow);

        recyclerViewPerfiles = findViewById(R.id.recyclerViewPerfiles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerViewPerfiles.setLayoutManager(layoutManager);

        perfilList = new ArrayList<>();
        adaptador = new adaptadorPerfiles(perfilList);
        recyclerViewPerfiles.setAdapter(adaptador);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sugerencias_perfiles.this, menu_inicial.class);
                startActivity(intent);
                finish();
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sugerencias_perfiles.this, sugerencias_perfiles.class);
                startActivity(intent);
                finish();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sugerencias_perfiles.this, subir_posts.class);
                startActivity(intent);
                finish();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sugerencias_perfiles.this, perfil_ajustes.class);
                startActivity(intent);
                finish();
            }
        });

        obtenerPerfiles();

    }
    public void obtenerPerfiles() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                perfilList.clear();

                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    String foto = snapshot1.child("fotoPerfil").getValue(String.class);
                    String nombre = snapshot1.child("firstName").getValue(String.class);
                    String usuario = snapshot1.child("lastName").getValue(String.class);

                    adaptadorPerfiles.Perfil perfil = new adaptadorPerfiles.Perfil(foto, nombre, usuario);
                    perfilList.add(perfil);
                }

                adaptador.notifyDataSetChanged();
                recyclerViewPerfiles.scrollToPosition(perfilList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}