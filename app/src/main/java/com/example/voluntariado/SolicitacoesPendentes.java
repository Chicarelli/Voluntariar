package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

public class SolicitacoesPendentes extends AppCompatActivity {

  RecyclerView rv;
  private GroupAdapter adapter;
  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  FirebaseFirestore db = FirebaseFirestore.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_solicitacoes_pendentes);

    rv = findViewById(R.id.rv_solicitacoes);

    adapter = new GroupAdapter();
    rv.setLayoutManager(new LinearLayoutManager(SolicitacoesPendentes.this));
    rv.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    adapter.clear();
    fetchList();
  }

  private void fetchList() {
    db.collection("memberSolicitations")
            .document(mAuth.getCurrentUser().getUid())
            .collection("eventosSolicitantes")
            .whereEqualTo("uidMember", mAuth.getCurrentUser().getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                if(!documentSnapshots.isEmpty()){
                  for (DocumentSnapshot doc : documentSnapshots) {
                    fetchEvent(doc.get("eventoID").toString());
                  }
                }
              }
            });
  }

  private void fetchEvent(String eventoID) {
    db.collection("eventos")
            .document(eventoID)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                Eventos evento = documentSnapshot.toObject(Eventos.class);
                adapter.add(new EventoItem(evento));
              }
            });
  }

  public void backToMain(View view){
    finish();
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

      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(SolicitacoesPendentes.this, telaDoEvento.class);
          intent.putExtra("id", eventos.getId());
          startActivity(intent);
        }
      });
    }

    @Override
    public int getLayout() {
      return R.layout.item_meus_eventos;
    }
  }

}