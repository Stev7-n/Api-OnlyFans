package com.example.apionlyfans;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class subir_posts extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth firebaseAuth;

    EditText tituloSubirPost;
    EditText descripcionSubirPost;
    ImageView subirImagenPost;
    Button seleccionarImagenButton;
    Button subirPost;

    DatabaseReference publicacionesRef;
    StorageReference storageRef;

    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private boolean isBotonSubirEnabled = true;
    private Uri selectedImageUri;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_posts);

        tituloSubirPost = findViewById(R.id.tituloSubirPost);
        descripcionSubirPost = findViewById(R.id.descripcionSubirPost);
        subirImagenPost = findViewById(R.id.subirImagenPost);
        seleccionarImagenButton = findViewById(R.id.seleccionarImagenButton);
        subirPost = findViewById(R.id.subirPost);

        firebaseAuth = FirebaseAuth.getInstance();

        publicacionesRef = FirebaseDatabase.getInstance().getReference().child("publicaciones6");
        storageRef = FirebaseStorage.getInstance().getReference().child("imagenes_publicaciones");

        seleccionarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(subir_posts.this);
                builder.setTitle("Seleccionar imagen");
                builder.setItems(new CharSequence[]{"Galería", "Cámara"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                abrirGaleria();
                                break;
                            case 1:
                                abrirCamara();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        subirPost.setEnabled(isBotonSubirEnabled);
        subirPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = tituloSubirPost.getText().toString();
                String descripcion = descripcionSubirPost.getText().toString();

                if (titulo.isEmpty() || titulo.matches("\\s+")) {
                    Toast.makeText(subir_posts.this, "Ingresa un título válido", Toast.LENGTH_SHORT).show();
                } else if (descripcion.isEmpty() || descripcion.matches("\\s+")) {
                    Toast.makeText(subir_posts.this, "Ingresa una descripción válida", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        userId = currentUser.getUid();
                        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String nombreUsuario = dataSnapshot.child("firstName").getValue(String.class);
                                    String apellidoUsuario = dataSnapshot.child("lastName").getValue(String.class);
                                    String fotoUsuario = dataSnapshot.child("fotoPerfil").getValue(String.class);

                                    if (selectedImageUri != null) {
                                        subirImagen(titulo, descripcion, nombreUsuario, apellidoUsuario, fotoUsuario);
                                    } else {
                                        Toast.makeText(subir_posts.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(subir_posts.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                mostrarImagenSeleccionada();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            selectedImageUri = getImageUri(this, imageBitmap);
            mostrarImagenSeleccionada();
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void mostrarImagenSeleccionada() {
        if (selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(subirImagenPost);
        }
    }

    private void subirImagen(String titulo, String descripcion, String nombreUsuario, String apellidoUsuario, String fotoUsuario) {
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                String imagenUrl = downloadUrl.toString();

                                Map<String, Object> publicacion = new HashMap<>();
                                publicacion.put("titulo", titulo);
                                publicacion.put("descripcion", descripcion);
                                publicacion.put("imagen", imagenUrl);
                                publicacion.put("nombreUsuario", nombreUsuario);
                                publicacion.put("apellidoUsuario", apellidoUsuario);
                                publicacion.put("fotoUsuario", fotoUsuario);
                                publicacion.put("userId", userId);

                                publicacionesRef.push().setValue(publicacion)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(subir_posts.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(subir_posts.this, menu_inicial.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(subir_posts.this, "Error al subir la publicación", Toast.LENGTH_SHORT).show();
                                                }
                                                subirPost.setEnabled(true);
                                                isBotonSubirEnabled = true;
                                            }
                                        });
                            }
                        });
                    }
                });

        subirPost.setEnabled(false);
        isBotonSubirEnabled = false;
    }
}