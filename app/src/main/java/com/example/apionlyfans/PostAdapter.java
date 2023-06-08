package com.example.apionlyfans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_view_posts, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuarioPosts;
        private TextView tituloTextView;
        private TextView descripcionTextView;
        private ImageView fotoPostImageView;

        public PostViewHolder(View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.tituloPost);
            descripcionTextView = itemView.findViewById(R.id.descripcionPost);
            fotoPostImageView = itemView.findViewById(R.id.fotoPost);
            nombreUsuarioPosts = itemView.findViewById(R.id.nombreUsuarioPosts);
        }

        public void bind(Post post) {
            tituloTextView.setText(post.getTitle());
            descripcionTextView.setText(post.getDescription());
            nombreUsuarioPosts.setText("@" + post.getNombreUsuario());

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(itemView.getContext())
                    .load(post.getImageUrl())
                    .apply(options)
                    .into(fotoPostImageView);
        }

    }

    public static class Post {
        private String title;
        private String description;
        private String imageUrl;
        private String nombreUsuario;

        public Post(String title, String description, String imageUrl, String nombreUsuario) {
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
            this.nombreUsuario = nombreUsuario;
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

        public String getNombreUsuario() {return nombreUsuario;}
    }

    public void obtenerPublicaciones() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("publicaciones3");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String titulo = snapshot.child("titulo").getValue(String.class);
                    String descripcion = snapshot.child("descripcion").getValue(String.class);
                    String imageUrl = snapshot.child("imagen").getValue(String.class);
                    String nombreUsuario = snapshot.child("nombreUsuario").getValue(String.class);

                    Post post = new Post(titulo, descripcion, imageUrl, nombreUsuario);
                    postList.add(post);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
