package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bar_bottom extends AppCompatActivity {

    //bar_bottom
    View include1;
    View include2;
    View include3;
    View include4;

    //menu_inicial
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<PostAdapter.Post> postList, postList2;
    PostAdapter postAdapter, postAdapter2;

    //sugerencias_perfil
    ImageView fotoPerfilUsuario;
    TextView nombrePerfil;
    TextView usuarioPerfil;
    Button follow;
    RecyclerView recyclerViewPerfiles;
    adaptadorPerfiles adaptador;
    List<adaptadorPerfiles.Perfil> perfilList;


    //subir_post
    private static final int PICK_IMAGE_REQUEST = 1;

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


    //perfil_ajustes
    ImageView cambiarFotoPerfil;
    TextView nombreUsuarioPerfil;
    TextView nombreUsuarioPerfilJunto;
    Button editarPerfil;

    Button publicaciones_propias;

    Button seguidores_usuario;
    RecyclerView recycler_publicaciones_propias;

    RecyclerView recycler_seguidores_usuario;

    private boolean mostrarPublicacionesPropias = true;


    //oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_bottom);


        //bar_bottom
        include1 = findViewById(R.id.include1);
        include2 = findViewById(R.id.include2);
        include3 = findViewById(R.id.include3);
        include4 = findViewById(R.id.include4);

        //bar_bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.buttonHome) {
                    mostrarInclude(include1);
                    ocultarInclude(include2);
                    ocultarInclude(include3);
                    ocultarInclude(include4);
                    return true;
                } else if (itemId == R.id.buttonSugerencias) {
                    ocultarInclude(include1);
                    mostrarInclude(include2);
                    ocultarInclude(include3);
                    ocultarInclude(include4);
                    return true;
                } else if (itemId == R.id.buttonPost) {
                    ocultarInclude(include1);
                    ocultarInclude(include2);
                    mostrarInclude(include3);
                    ocultarInclude(include4);
                    return true;
                } else if (itemId == R.id.buttonProfile) {
                    ocultarInclude(include1);
                    ocultarInclude(include2);
                    ocultarInclude(include3);
                    mostrarInclude(include4);
                    return true;
                } else {
                    return false;
                }
            }

        });


        //menu_inicial
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);

        postAdapter.obtenerPublicaciones();


        //sugerencias_perfiles
        firebaseAuth = FirebaseAuth.getInstance();

        fotoPerfilUsuario = findViewById(R.id.fotoPerfilUsuario);
        nombrePerfil = findViewById(R.id.nombrePerfil);
        usuarioPerfil = findViewById(R.id.usuarioPerfil);
        follow = findViewById(R.id.follow);

        recyclerViewPerfiles = findViewById(R.id.recyclerViewPerfiles);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setReverseLayout(true);
        layoutManager1.setStackFromEnd(true);
        recyclerViewPerfiles.setLayoutManager(layoutManager1);

        perfilList = new ArrayList<>();
        adaptador = new adaptadorPerfiles(perfilList);
        recyclerViewPerfiles.setAdapter(adaptador);

        obtenerPerfiles();


        //subir_post
        tituloSubirPost = findViewById(R.id.tituloSubirPost);
        descripcionSubirPost = findViewById(R.id.descripcionSubirPost);
        subirImagenPost = findViewById(R.id.subirImagenPost);
        seleccionarImagenButton = findViewById(R.id.seleccionarImagenButton);
        subirPost = findViewById(R.id.subirPost);

        firebaseAuth = FirebaseAuth.getInstance();

        publicacionesRef = FirebaseDatabase.getInstance().getReference().child("publicaciones6");
        storageRef = FirebaseStorage.getInstance().getReference().child("imagenes_publicaciones");

        //subir_post
        seleccionarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(bar_bottom.this);
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

        //subir_post
        subirPost.setEnabled(isBotonSubirEnabled);
        subirPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = tituloSubirPost.getText().toString();
                String descripcion = descripcionSubirPost.getText().toString();

                if (titulo.isEmpty() || titulo.matches("\\s+")) {
                    Toast.makeText(bar_bottom.this, "Ingresa un título válido", Toast.LENGTH_SHORT).show();
                } else if (descripcion.isEmpty() || descripcion.matches("\\s+")) {
                    Toast.makeText(bar_bottom.this, "Ingresa una descripción válida", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(bar_bottom.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(bar_bottom.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        //perfil_ajustes
        cambiarFotoPerfil = findViewById(R.id.cambiarFotoPerfil);

        firebaseAuth = FirebaseAuth.getInstance();

        nombreUsuarioPerfil = findViewById(R.id.nombreUsuarioPerfil);
        nombreUsuarioPerfilJunto = findViewById(R.id.nombreUsuarioPerfilJunto);

        editarPerfil = findViewById(R.id.editarPerfil);
        publicaciones_propias = findViewById(R.id.publicaciones_propias);
        seguidores_usuario = findViewById(R.id.seguidores_usuario);

        recycler_publicaciones_propias = findViewById(R.id.recycler_publicaciones_propias);
        postList = new ArrayList<>();
        postAdapter2 = new PostAdapter(postList);


        recycler_seguidores_usuario = findViewById(R.id.recycler_seguidores_usuario);

        //perfil_ajustes
        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bar_bottom.this, ajustar_perfil.class);
                startActivity(intent);
            }
        });

        //perfil_ajustes
        publicaciones_propias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mostrarPublicacionesPropias) {
                    mostrarRecyclerPublicacionesPropias();
                    mostrarPublicacionesPropias = true;
                }
            }
        });

        //perfil_ajustes
        seguidores_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mostrarPublicacionesPropias) {
                    mostrarRecyclerSeguidoresUsuario();
                    mostrarPublicacionesPropias = false;
                }
            }
        });

        //perfil_ajustes
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    String fullName = firstName + " " + lastName;
                    nombreUsuarioPerfil.setText(fullName);
                    nombreUsuarioPerfilJunto.setText("@" + firstName.toLowerCase() + lastName.toLowerCase());

                    String fotoPerfilUrl = dataSnapshot.child("fotoPerfil").getValue(String.class);

                    if (fotoPerfilUrl != null) {
                        Glide.with(bar_bottom.this).load(fotoPerfilUrl).into(cambiarFotoPerfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    //bar_bottom
    private void mostrarInclude(View include) {
        include.setVisibility(View.VISIBLE);
    }

    private void ocultarInclude(View include) {
        include.setVisibility(View.GONE);
    }



    //menu_inicial
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            irMain();
        }
    }

    private void irMain() {
        Intent intent = new Intent(bar_bottom.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    //sugerenciasperfil
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



    //subir_post
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
                                                    Toast.makeText(bar_bottom.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(bar_bottom.this, bar_bottom.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(bar_bottom.this, "Error al subir la publicación", Toast.LENGTH_SHORT).show();
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



    //perfil_ajustes
    private void mostrarRecyclerPublicacionesPropias() {
        recycler_publicaciones_propias.setVisibility(View.VISIBLE);
        recycler_seguidores_usuario.setVisibility(View.GONE);
    }

    private void mostrarRecyclerSeguidoresUsuario() {
        recycler_seguidores_usuario.setVisibility(View.VISIBLE);
        recycler_publicaciones_propias.setVisibility(View.GONE);
    }
}