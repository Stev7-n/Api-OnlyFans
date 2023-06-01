package com.example.apionlyfans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registrarse_only extends AppCompatActivity {

    TextInputEditText ingresaFirstName;
    TextInputEditText ingresaLastName;
    TextInputEditText ingresaCorreo;
    TextInputEditText ingresaContraseniaR;
    Button registrarse_only;
    Button loguearse_desde_registro;

    FirebaseAuth firebaseAuth;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse_only);

        ingresaFirstName = findViewById(R.id.ingresaFirstName);
        ingresaLastName = findViewById(R.id.ingresaLastName);
        ingresaCorreo = findViewById(R.id.ingresaCorreo);
        ingresaContraseniaR = findViewById(R.id.ingresaContraseniaR);
        registrarse_only = findViewById(R.id.registrarse_only);
        loguearse_desde_registro = findViewById(R.id.loguearse_desde_registro);

        firebaseAuth = FirebaseAuth.getInstance();

        registrarse_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ingresaCorreo.getText().toString().trim();
                password = ingresaContraseniaR.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(registrarse_only.this, "Ingresa todos los datos para continuar", Toast.LENGTH_SHORT).show();
                } else {
                    if (emailValido(email)) {
                        if (password.length() < 5) {
                            Toast.makeText(registrarse_only.this, "La contraseña debe tener mas de 5 caracteres", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(registrarse_only.this, "Cuenta creada con exito", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(registrarse_only.this, menu_inicial.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(registrarse_only.this, "Una cuenta asociada a este correo ya existe", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else {
                        Toast.makeText(registrarse_only.this, "Direccion de correo invalida", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        loguearse_desde_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registrarse_only.this, iniciar_sesion.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}