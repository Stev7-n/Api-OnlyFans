package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class sugerencias_perfiles extends AppCompatActivity {
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

        firebaseAuth = FirebaseAuth.getInstance();

        fotoPerfilUsuario = findViewById(R.id.fotoPerfilUsuario);
        nombrePerfil = findViewById(R.id.nombrePerfil);
        usuarioPerfil = findViewById(R.id.usuarioPerfil);
        follow = findViewById(R.id.follow);

        recyclerViewPerfiles = findViewById(R.id.recyclerViewPerfiles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewPerfiles.setLayoutManager(layoutManager);

        perfilList = new ArrayList<>();
        adaptador = new adaptadorPerfiles(perfilList);
        recyclerViewPerfiles.setAdapter(adaptador);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.buttonHome) {
                    Intent intentHome = new Intent(sugerencias_perfiles.this, menu_inicial.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonSugerencias) {
                    Intent intentSugerencias = new Intent(sugerencias_perfiles.this, sugerencias_perfiles.class);
                    startActivity(intentSugerencias);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonPost) {
                    Intent intentPost = new Intent(sugerencias_perfiles.this, subir_posts.class);
                    startActivity(intentPost);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonProfile) {
                    Intent intentProfile = new Intent(sugerencias_perfiles.this, perfil_ajustes.class);
                    startActivity(intentProfile);
                    finish();
                    return true;
                }

                return false;
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