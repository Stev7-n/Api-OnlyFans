package com.example.apionlyfans;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class sugerencias_perfiles extends AppCompatActivity {

    ImageButton home;
    ImageButton explorar;
    ImageButton posts;
    ImageButton perfil;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias_perfiles);

        home = findViewById(R.id.home);
        explorar = findViewById(R.id.explorar);
        posts = findViewById(R.id.posts);
        perfil = findViewById(R.id.perfil);

        firebaseAuth = FirebaseAuth.getInstance();

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
    }
}