package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class menu_inicial extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<PostAdapter.Post> postList;
    PostAdapter postAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.buttonHome) {
                    Intent intentHome = new Intent(menu_inicial.this, menu_inicial.class);
                    startActivity(intentHome);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonSugerencias) {
                    Intent intentSugerencias = new Intent(menu_inicial.this, sugerencias_perfiles.class);
                    startActivity(intentSugerencias);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonPost) {
                    Intent intentPost = new Intent(menu_inicial.this, subir_posts.class);
                    startActivity(intentPost);
                    finish();
                    return true;
                } else if (itemId == R.id.buttonProfile) {
                    Intent intentProfile = new Intent(menu_inicial.this, perfil_ajustes.class);
                    startActivity(intentProfile);
                    finish();
                    return true;
                }

                return false;
            }
        });




        recyclerView = findViewById(R.id.recyclerView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);

        postAdapter.obtenerPublicaciones();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            irMain();
        }
    }

    private void irMain() {
        Intent intent = new Intent(menu_inicial.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
