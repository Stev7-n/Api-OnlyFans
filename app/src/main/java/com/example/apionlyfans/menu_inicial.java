package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class menu_inicial extends AppCompatActivity {

    ImageButton home;
    ImageButton explorar;
    ImageButton posts;
    ImageButton perfil;
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<PostAdapter.Post> postList;
    PostAdapter postAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);

        home = findViewById(R.id.home);
        explorar = findViewById(R.id.explorar);
        posts = findViewById(R.id.posts);
        perfil = findViewById(R.id.perfil);

        firebaseAuth = FirebaseAuth.getInstance();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu_inicial.this, menu_inicial.class);
                startActivity(intent);
                finish();
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu_inicial.this, sugerencias_perfiles.class);
                startActivity(intent);
                finish();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu_inicial.this, subir_posts.class);
                startActivity(intent);
                finish();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menu_inicial.this, perfil_ajustes.class);
                startActivity(intent);
                finish();
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
