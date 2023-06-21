package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adaptador_publicaciones_propias extends RecyclerView.Adapter<adaptador_publicaciones_propias.publicacionesViewHolder> {

    private List<PostsPropios> postsPropiosList;
    private DatabaseReference publicacionRef;

    public adaptador_publicaciones_propias(List<PostsPropios> postsPropiosList) {
        this.postsPropiosList = postsPropiosList;
    }

    @Override
    public adaptador_publicaciones_propias.publicacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_view_posts, parent, false);
        return new publicacionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptador_publicaciones_propias.publicacionesViewHolder holder, int position) {
        adaptador_publicaciones_propias.PostsPropios post = postsPropiosList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postsPropiosList.size();
    }

    public class publicacionesViewHolder extends  RecyclerView.ViewHolder {

        private TextView nombreUsuarioPosts;
        private ImageView fotoPerfilUsuarioMensaje;
        private TextView tituloTextView;
        private TextView descripcionTextView;
        private ImageView fotoPostImageView;
        private ImageButton meEncanta;
        private TextView contadorLikes;

        public publicacionesViewHolder(View itemView) {
            super(itemView);

            tituloTextView = itemView.findViewById(R.id.tituloPost);
            descripcionTextView = itemView.findViewById(R.id.descripcionPost);
            fotoPostImageView = itemView.findViewById(R.id.fotoPost);
            nombreUsuarioPosts = itemView.findViewById(R.id.nombreUsuarioPosts);
            fotoPerfilUsuarioMensaje = itemView.findViewById(R.id.fotoPerfilUsuarioMensaje);
            meEncanta = itemView.findViewById(R.id.meEncanta);
            contadorLikes = itemView.findViewById(R.id.contadorLikes);

        }

        @SuppressLint("SetTextI18n")
        public void bind(adaptador_publicaciones_propias.PostsPropios postsPropios) {
            tituloTextView.setText(postsPropios.getTitle());
            descripcionTextView.setText(postsPropios.getDescription());
            nombreUsuarioPosts.setText("@" + postsPropios.getNombreUsuario().toLowerCase() + postsPropios.getApellidoUsuario().toLowerCase());

            Glide.with(itemView.getContext())
                    .load(postsPropios.getUsuarioUrl())
                    .into(fotoPerfilUsuarioMensaje);

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(itemView.getContext())
                    .load(postsPropios.getImageUrl())
                    .apply(options)
                    .into(fotoPostImageView);
        }
    }

    public static class  PostsPropios {

        private String title;
        private String description;
        private String imageUrl;
        private String nombreUsuario;
        private String apellidoUsuario;
        private String usuarioUrl;

        public PostsPropios(String title, String description, String imageUrl, String nombreUsuario, String apellidoUsuario, String usuarioUrl) {

            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
            this.nombreUsuario = nombreUsuario;
            this.apellidoUsuario = apellidoUsuario;
            this.usuarioUrl = usuarioUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public String getApellidoUsuario() {
            return apellidoUsuario;
        }

        public String getUsuarioUrl() {
            return usuarioUrl;
        }
    }

    private void actualizarContadorLikes(String postId, final TextView textView) {
        DatabaseReference likesRef = publicacionRef.child(postId).child("likes");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long likesCount = dataSnapshot.getChildrenCount();
                    textView.setText(String.valueOf(likesCount));
                } else {
                    textView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void obtenerPublicaciones() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("publicaciones6");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsPropiosList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String titulo = snapshot.child("titulo").getValue(String.class);
                    String descripcion = snapshot.child("descripcion").getValue(String.class);
                    String imageUrl = snapshot.child("imagen").getValue(String.class);
                    String nombreUsuario = snapshot.child("nombreUsuario").getValue(String.class);
                    String apellidoUsuario = snapshot.child("apellidoUsuario").getValue(String.class);
                    String fotoUsuario = snapshot.child("fotoUsuario").getValue(String.class);

                    PostsPropios postsPropios = new PostsPropios(titulo, descripcion, imageUrl, nombreUsuario, apellidoUsuario, fotoUsuario);
                    postsPropiosList.add(postsPropios);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
