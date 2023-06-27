package com.example.apionlyfans;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ajustar_perfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView cambiarFotoPerfil;
    TextInputEditText cambiarFirstName;
    TextInputEditText cambiarLastName;
    TextInputEditText cambiarUsuario;
    TextInputEditText cambiarEdad;
    TextInputEditText cambiarNumeroTelefono;
    TextInputEditText cambiarNacionalidad;
    Button guardarCambios;
    Button cerrarSesion;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustar_perfil);

        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);
        cambiarFirstName = findViewById(R.id.cambiarFirstName);
        cambiarLastName = findViewById(R.id.cambiarLastName);
        cambiarUsuario = findViewById(R.id.cambiarUsuario);
        cambiarEdad = findViewById(R.id.cambiarEdad);
        cambiarNumeroTelefono = findViewById(R.id.cambiarNumeroTelefono);
        cambiarNacionalidad = findViewById(R.id.cambiarNacionalidad);
        guardarCambios = findViewById(R.id.guardarCambios);
        cerrarSesion = findViewById(R.id.cerrarSesion);

        firebaseAuth = FirebaseAuth.getInstance();

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                irMain();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatosUsuario();
            }
        });

        cambiarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFotoDeGaleria();
            }
        });
    }

    public void seleccionarFotoDeGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(ajustar_perfil.this).load(selectedImageUri).into(cambiarFotoPerfil);
        }
    }

    private void guardarDatosUsuario() {
        String nuevoFirstName = cambiarFirstName.getText().toString();
        String nuevoLastName = cambiarLastName.getText().toString();
        String nuevoUsuario = cambiarUsuario.getText().toString();
        String edad = cambiarEdad.getText().toString();
        String numeroTelefono = cambiarNumeroTelefono.getText().toString();
        String nacionalidad = cambiarNacionalidad.getText().toString();

        if (nuevoFirstName.isEmpty() || nuevoLastName.isEmpty() || nuevoUsuario.isEmpty() || edad.isEmpty() || numeroTelefono.isEmpty() || nacionalidad.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCadena(nuevoFirstName)) {
            Toast.makeText(this, "El campo 'First Name' contiene caracteres inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCadena(nuevoLastName)) {
            Toast.makeText(this, "El campo 'Last Name' contiene caracteres inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCadena(nuevoUsuario)) {
            Toast.makeText(this, "El campo 'Usuario' contiene caracteres inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCadena(nacionalidad)) {
            Toast.makeText(this, "El campo 'Nacionalidad' contiene caracteres inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edadInt = Integer.parseInt(edad);
        if (edadInt < 18) {
            Toast.makeText(this, "Debes ser mayor de edad para crear una cuenta en esta aplicación", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No se puede guardar los datos. El usuario no está autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datosUsuario = new HashMap<>();

        datosUsuario.put("firstName", nuevoFirstName);
        datosUsuario.put("lastName", nuevoLastName);
        datosUsuario.put("usuario", nuevoUsuario);
        datosUsuario.put("edad", edad);
        datosUsuario.put("numeroTelefono", numeroTelefono);
        datosUsuario.put("nacionalidad", nacionalidad);

        if (selectedImageUri != null) {
            subirFotoPerfil(datosUsuario);
        } else {
            guardarDatosUsuarioSinFoto(datosUsuario);
        }
    }

    private boolean validarCadena(String cadena) {
        String regex = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<> ]+$";
        return cadena.matches(regex);
    }

    private void subirFotoPerfil(final Map<String, Object> datosUsuario) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("perfil_fotos").child(userId);

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        datosUsuario.put("fotoPerfil", imageUrl);
                        guardarDatosUsuarioConFoto(datosUsuario);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ajustar_perfil.this, "Error al subir la foto de perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarDatosUsuarioConFoto(final Map<String, Object> datosUsuario) {
        usersRef.updateChildren(datosUsuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ajustar_perfil.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                            irPerfilAjustes();
                        } else {
                            Toast.makeText(ajustar_perfil.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuarioSinFoto(final Map<String, Object> datosUsuario) {
        usersRef.updateChildren(datosUsuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ajustar_perfil.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                            irPerfilAjustes();
                        } else {
                            Toast.makeText(ajustar_perfil.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void irPerfilAjustes() {
        Intent intent = new Intent(ajustar_perfil.this, bar_bottom.class);
        startActivity(intent);
        finish();
    }

    private void irMain() {
        Intent intent = new Intent(ajustar_perfil.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
