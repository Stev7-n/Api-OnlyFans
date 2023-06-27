package com.example.apionlyfans;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    DatabaseReference usersRef;
    FirebaseStorage storage;
    StorageReference storageRef;
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
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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

                                        String firstName = ingresaFirstName.getText().toString().trim();
                                        String lastName = ingresaLastName.getText().toString().trim();

                                        String userId = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserRef = usersRef.child(userId);

                                        currentUserRef.child("firstName").setValue(firstName);
                                        currentUserRef.child("lastName").setValue(lastName);

                                        subirImagenFirebase(userId);

                                    } else {
                                        Toast.makeText(registrarse_only.this, "Una cuenta asociada a este correo ya existe", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
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

    private void subirImagenFirebase(String userId) {
        StorageReference fotoPerfilRef = storageRef.child("perfil_fotos").child(userId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/drawable/user"));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fotoPerfilRef.putBytes(data);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fotoPerfilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fotoPerfilUrl = uri.toString();
                            DatabaseReference currentUserRef = usersRef.child(userId);
                            currentUserRef.child("fotoPerfil").setValue(fotoPerfilUrl);

                            Toast.makeText(registrarse_only.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(registrarse_only.this, bar_bottom.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(registrarse_only.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}