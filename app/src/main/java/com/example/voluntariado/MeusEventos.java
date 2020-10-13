package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.ArrayList;
import java.util.List;

public class MeusEventos extends AppCompatActivity {

    private String id = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private Eventos mi;

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        RecyclerView rv = findViewById(R.id.rv_meus_eventos_lista);

        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(MeusEventos.this));
        rv.setAdapter(adapter);

        fetchEventos();
    }

    private void fetchEventos() {
        db.collection("eventos")
                .whereEqualTo("proprietario", this.id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                //Toast.makeText(MeusEventos.this, documentChanges.toString(), Toast.LENGTH_SHORT).show();
                if (documentChanges != null) {
                    for (DocumentChange doc : documentChanges) {
                           Eventos evento = doc.getDocument().toObject(Eventos.class);
                            adapter.add(new EventoItem(evento));

                    }
                }
            }
        });
    }


    private class EventoItem extends Item<GroupieViewHolder> {

        private final Eventos eventos;


        private EventoItem (Eventos eventos) {
            this.eventos = eventos;
        }


        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {

            TextView titulo = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_titulo);
            TextView data = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_data);
            TextView hora = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_hora);

            titulo.setText(eventos.getTitulo());
            data.setText(eventos.getData());
            hora.setText(eventos.getHora());
        }

        @Override
        public int getLayout() {

            return R.layout.item_meus_eventos;
        }
    }

}