package com.example.apionlyfans;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class iniciar_sesion extends AppCompatActivity {

    TextInputEditText ingresaEmail;
    TextInputEditText ingresaContraseina;
    Button registrar_usuario;
    Button iniciar_sesion;
    Button olvidar_contraseina;

    FirebaseAuth firebaseAuth;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        ingresaEmail = findViewById(R.id.ingresaEmail);
        ingresaContraseina = findViewById(R.id.ingresaContrasenia);
        registrar_usuario = findViewById(R.id.registrar_usuario);
        iniciar_sesion = findViewById(R.id.iniciar_sesion);
        olvidar_contraseina = findViewById(R.id.olvidar_contraseina);

        //modelo
        firebaseAuth = FirebaseAuth.getInstance();

        //view
        registrar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(iniciar_sesion.this, registrarse_only.class);
                startActivity(intent);
                finish();
            }
        });

        //view
        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //presenter
                email = ingresaEmail.getText().toString().trim();
                password = ingresaContraseina.getText().toString().trim();

                //presenter
                if (email.isEmpty() || password.isEmpty()) {
                    //toasts vista
                    Toast.makeText(iniciar_sesion.this, "Ingrese el correo y la contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    if (emailValido(email)) {
                        //modelo
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(iniciar_sesion.this, "Logueo correcto", Toast.LENGTH_SHORT).show();
                                    goInicio();
                                } else {
                                    Toast.makeText(iniciar_sesion.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {
                        Toast.makeText(iniciar_sesion.this, "Direccion de correo invalida", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        olvidar_contraseina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ingresaEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(iniciar_sesion.this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(iniciar_sesion.this, "Se ha enviado un correo electrónico para restablecer su contraseña", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(iniciar_sesion.this, "No se pudo enviar el correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            goInicio();
        }
    }

    //
    private void goInicio() {
        Intent intent = new Intent(iniciar_sesion.this, bar_bottom.class);
        startActivity(intent);
        finish();
    }

    //presenter
    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}