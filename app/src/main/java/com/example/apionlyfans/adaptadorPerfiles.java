package com.example.apionlyfans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adaptadorPerfiles extends RecyclerView.Adapter<adaptadorPerfiles.perfilViewHolder> {

    private List<Perfil> perfilList;
    private boolean isFollowing = false;

    public adaptadorPerfiles(List<Perfil> perfilList) {
        this.perfilList = perfilList;
    }


    @Override
    public adaptadorPerfiles.perfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_view_perfiles, parent, false);
        return new perfilViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptadorPerfiles.perfilViewHolder holder, int position) {
        adaptadorPerfiles.Perfil post = perfilList.get(position);
        holder.bind(post);

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    holder.follow.setText("Follow");
                    isFollowing = false;
                } else {
                    holder.follow.setText("Following");
                    isFollowing = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return perfilList.size();
    }

    public class perfilViewHolder extends  RecyclerView.ViewHolder {

        private ImageView fotoPerfilUsuario;
        private TextView nombrePerfil;
        private TextView usuarioPerfil;
        private Button follow;

        public perfilViewHolder(View itemView) {
            super(itemView);

            fotoPerfilUsuario = itemView.findViewById(R.id.fotoPerfilUsuario);
            nombrePerfil = itemView.findViewById(R.id.nombrePerfil);
            usuarioPerfil = itemView.findViewById(R.id.usuarioPerfil);
            follow = itemView.findViewById(R.id.follow);

        }

        public void bind(Perfil perfil) {
            nombrePerfil.setText(perfil.getFullName());
            usuarioPerfil.setText(perfil.getNick());

            Glide.with(itemView.getContext())
                    .load(perfil.getFoto())
                    .into(fotoPerfilUsuario);

        }
    }

    public static class  Perfil {

        private String foto;
        private String name;
        private String nick;
        private String identificador;

        public Perfil(String foto, String name, String nick, String identificador) {
            this.foto = foto;
            this.name = name;
            this.nick = nick;
            this.identificador = identificador;
        }

        public String getFoto() {
            return foto;
        }
        public String getFullName() {
            return name + " " + nick;
        }
        public String getNick() {
            return "@" + name.toLowerCase() + nick.toLowerCase();
        }
        public String getIdentificador() {
            return identificador;
        }
    }

    //bar_bottom
    public void obtenerPerfiles() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                perfilList.clear();

                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    String foto = snapshot1.child("fotoPerfil").getValue(String.class);
                    String nombre = snapshot1.child("firstName").getValue(String.class);
                    String usuario = snapshot1.child("lastName").getValue(String.class);
                    String identificador = snapshot1.child("identifier").getValue(String.class);

                    Perfil perfil = new Perfil(foto, nombre, usuario, identificador);
                    perfilList.add(perfil);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
