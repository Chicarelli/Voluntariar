package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

import static com.example.voluntariado.R.drawable.ic_meu_perfil;

public class PerfilMembros extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id = "";
    ImageView userPhoto;

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_membros);
        puxandoID();
        buscarDadosFirestore(this.id);

        userPhoto = findViewById(R.id.imagePerfil);

        RecyclerView rv = findViewById(R. id. rv_perfilMembros);
        rv.setLayoutManager(new LinearLayoutManager(PerfilMembros.this));

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

    }

    public void puxandoID(){
        if (getIntent().hasExtra("id")){
            String id = getIntent().getStringExtra("id");
            this.id = id;
        }
    }

    public void buscarDadosFirestore(String id){
        db.collection("users1").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            String nome = user.getNome();
                            String email = user.getEmail();
                            String idade = user.getDataNasc();
                            setandoTextView(nome, email, idade);

                            String image = user.getImage();
                            setImage(image);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setImage(String image) {
        if(!image.trim().isEmpty()){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(image);
            refStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(PerfilMembros.this)
                            .load(uri)
                            .into(userPhoto);
                }
            });
        }
    }

    public void setandoTextView(String nome, String email, String idade){
        TextView edtNome = findViewById(R.id.edtNome_perfil_membros);
        TextView edtIdade = findViewById(R.id.edtIdade_perfil_membros);
        TextView edtEmail = findViewById(R.id.edtEmail_perfil_membros);

        edtNome.setText(nome);
        edtEmail.setText(email);
        edtIdade.setText(idade);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(PerfilMembros.this, ChatActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListOfEventosCreated();
    }

    private void setListOfEventosCreated() {
        adapter.clear();

        db.collection("eventos")
                .whereEqualTo("proprietario", id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges != null) {
                            for ( DocumentChange doc: documentChanges) {
                                Eventos evento = doc.getDocument().toObject(Eventos.class);
                                adapter.add(new EventoItem(evento));
                            }
                        }
                    }
                });

    }

    private class EventoItem extends Item<GroupieViewHolder> {

        private final Eventos evento;

        public EventoItem(Eventos evento) {
            this.evento = evento;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView txtTitulo = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_titulo);
            TextView txtData = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_data);
            TextView txtHora = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_hora);

            txtTitulo.setText(evento.getTitulo());
            txtData.setText(evento.getData());
            txtHora.setText(evento.getHora());

        }

        @Override
        public int getLayout() {
            return R.layout.item_meus_eventos;
        }
    }
}