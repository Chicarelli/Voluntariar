package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class RequestParticipation extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  String id;
  private ListView mParticipacaoListView;
  TextView noSolicitation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lista_de_participantes);

    noSolicitation = findViewById(R.id.noSolicitation);

    if (getIntent().hasExtra("id")) {
      id = getIntent().getStringExtra("id");
    }
    fetchList();
  }

  public void fetchList(){
    mParticipacaoListView = findViewById(R.id.participantesListView);

    db.collection("participating")
            .document(id)
            .collection("solicitacoes")
            .whereEqualTo("eventoID", id)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final List<ParticipacaoEvento> mParticipantesList = new ArrayList<>();
                if(task.isSuccessful()){
                  for (QueryDocumentSnapshot document: task.getResult()){
                    ParticipacaoEvento participacaoEvento = document.toObject(ParticipacaoEvento.class);

                    mParticipantesList.add(participacaoEvento);

                    Toast.makeText(RequestParticipation.this, participacaoEvento.getIdParticipatingMember(), LENGTH_SHORT).show();
                  }
                }
                if(task.getResult().isEmpty()){
                  noSolicitation.setVisibility(View.VISIBLE);
                }
                final ParticipantesAdapter mParticipacaoAdapter = new ParticipantesAdapter(RequestParticipation.this, mParticipantesList);
                mParticipacaoListView.setAdapter(mParticipacaoAdapter);

                mParticipacaoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                    PopupMenu popup = new PopupMenu(RequestParticipation.this, view);

                    popup.getMenuInflater().inflate(R.menu.listadeparticipantes, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                          case R.id.verificarParticipante:
                            verificarParticipante(mParticipacaoAdapter.getItem(position).getIdParticipatingMember());
                            return true;

                          case R.id.aprovarParticipante: aprovarParticipante(mParticipacaoAdapter.getItem(position).getIdParticipatingMember(), mParticipacaoAdapter, position);
                            return true;

                          case R.id.excluirParticipante:
                            excluirParticipante(mParticipacaoAdapter.getItem(position).getIdParticipatingMember(), mParticipacaoAdapter, position);
                            return true;

                          default:
                            return true;
                        }
                      }
                    });
                    popup.show();
                  }
                });
              }
            });
  }

  private void aprovarParticipante(final String uidMembro, final ParticipantesAdapter mParticipacaoAdapter, final int position) {
    Toast.makeText(RequestParticipation.this, "Clicou", LENGTH_SHORT).show();

    Map<String, Object> aproved = new HashMap<String,Object>();
    aproved.put("Evento", id);
    aproved.put("uuid", uidMembro);

    db.collection("aprovedMembers")
            .document(id)
            .collection("participantes")
            .document(uidMembro)
            .set(aproved)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RequestParticipation.this, "Membro Participando", LENGTH_SHORT).show();
                excluirParticipante(uidMembro, mParticipacaoAdapter, position);
              }
            });

    Map<String, Object> pendings = new HashMap<>();
    pendings.put("eventoID", id);
    pendings.put("uidMember", uidMembro);

    db.collection("memberAprovados")
            .document(uidMembro)
            .collection("eventosAprovados")
            .document()
            .set(pendings)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                //
              }
            });

    db.collection("participating")
            .document(id)
            .collection("solicitacoes")
            .document(uidMembro+id)
            .delete();

    db.collection("memberSolicitations")
            .document(uidMembro)
            .collection("eventosSolicitantes")
            .whereEqualTo("eventoID", id)
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        List<DocumentSnapshot> documents = task.getResult().getDocuments();
        if (!documents.isEmpty()){
          String uidEvento = documents.get(0).getId();
          //Toast.makeText(telaDoEvento.this, uidEvento, LENGTH_SHORT).show();
          db.collection("memberSolicitations")
                  .document(uidMembro)
                  .collection("eventosSolicitantes")
                  .document(uidEvento)
                  .delete();
        }
      }
    });

  }

  public void verificarParticipante(String id){
    Intent intent = new Intent(RequestParticipation.this, PerfilMembros.class);
    intent.putExtra("id", id);
    startActivity(intent);;
  }

  private void excluirParticipante(String uidMember, final ParticipantesAdapter mParticipacaoAdapter, final int position) {
    db.collection("participating")
            .document(id)
            .collection("solicitacoes")
            .document(uidMember+id)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Toast.makeText(RequestParticipation.this, "Participante Excluído", LENGTH_SHORT).show();
                mParticipacaoAdapter.remove(mParticipacaoAdapter.getItem(position));
                mParticipacaoAdapter.notifyDataSetChanged();
              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(RequestParticipation.this, "Falhou!", LENGTH_SHORT).show();
      }
    });
  }

  public void backToMain(View view){
    finish();
  }
}

