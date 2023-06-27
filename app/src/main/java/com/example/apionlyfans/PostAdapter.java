package com.example.apionlyfans;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private DatabaseReference publicacionRef;
    public static String userId;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        publicacionRef = FirebaseDatabase.getInstance().getReference("publicaciones6");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_view_posts, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        final Post post = postList.get(position);
        holder.bind(post);

        DatabaseReference likesRef = publicacionRef.child(post.getId()).child("likes").child(userId);
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.meEncanta.setImageResource(R.drawable.corazon_1);
                } else {
                    holder.meEncanta.setImageResource(R.drawable.corazon_vector2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        holder.meEncanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            likesRef.removeValue();
                        } else {
                            likesRef.setValue(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        actualizarContadorLikes(post.getId(), holder.contadorLikes);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuarioPosts;
        private ImageView fotoPerfilUsuarioMensaje;
        private TextView tituloTextView;
        private TextView descripcionTextView;
        private ImageView fotoPostImageView;
        private ImageButton meEncanta;
        private TextView contadorLikes;

        public PostViewHolder(View itemView) {
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
        public void bind(Post post) {
            tituloTextView.setText(post.getTitle());
            descripcionTextView.setText(post.getDescription());
            nombreUsuarioPosts.setText("@" + post.getNombreUsuario().toLowerCase() + post.getApellidoUsuario().toLowerCase());

            Glide.with(itemView.getContext())
                    .load(post.getUsuarioUrl())
                    .into(fotoPerfilUsuarioMensaje);

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(itemView.getContext())
                    .load(post.getImageUrl())
                    .apply(options)
                    .into(fotoPostImageView);
        }

    }

    public static class Post {
        private String id;
        private String title;
        private String description;
        private String imageUrl;
        private String nombreUsuario;
        private String apellidoUsuario;
        private String usuarioUrl;

        public Post(String id, String title, String description, String imageUrl, String nombreUsuario, String apellidoUsuario, String usuarioUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
            this.nombreUsuario = nombreUsuario;
            this.apellidoUsuario = apellidoUsuario;
            this.usuarioUrl = usuarioUrl;
        }

        public String getId() {
            return id;
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

    public void obtenerPublicaciones(boolean principal) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("publicaciones6");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> newPostList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String userId = snapshot.child("userId").getValue(String.class);
                    String titulo = snapshot.child("titulo").getValue(String.class);
                    String descripcion = snapshot.child("descripcion").getValue(String.class);
                    String imageUrl = snapshot.child("imagen").getValue(String.class);
                    String nombreUsuario = snapshot.child("nombreUsuario").getValue(String.class);
                    String apellidoUsuario = snapshot.child("apellidoUsuario").getValue(String.class);
                    String fotoUsuario = snapshot.child("fotoUsuario").getValue(String.class);

                    Post post = new Post(id, titulo, descripcion, imageUrl, nombreUsuario, apellidoUsuario, fotoUsuario);

                    if (principal){

                        newPostList.add(post);

                    } else if (nombreUsuario != null && PostAdapter.userId.equals(userId)) {

                        newPostList.add(post);

                    }

                }

                updateData(newPostList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateData(List<Post> newPostList) {
        PostDiffCallback diffCallback = new PostDiffCallback(postList, newPostList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        postList.clear();
        postList.addAll(newPostList);
        diffResult.dispatchUpdatesTo(this);
    }

}
