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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class ListaDeParticipantes extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  String id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lista_de_participantes);

    if (getIntent().hasExtra("id")) {
      id = getIntent().getStringExtra("id");
    }
    fetchList();
  }

  public void fetchList(){
    final ListView mParticipacaoListView = (ListView) findViewById(R.id.participantesListView);

    db.collection("participating")
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

                    Toast.makeText(ListaDeParticipantes.this, participacaoEvento.getIdParticipatingMember(), LENGTH_SHORT).show();
                  }
                }
                final ParticipantesAdapter mParticipacaoAdapter = new ParticipantesAdapter(ListaDeParticipantes.this, mParticipantesList);
                mParticipacaoListView.setAdapter(mParticipacaoAdapter);

                mParticipacaoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                    PopupMenu popup = new PopupMenu(ListaDeParticipantes.this, view);

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
    Toast.makeText(ListaDeParticipantes.this, "Clicou", LENGTH_SHORT).show();

    Map<String, Object> aproved = new HashMap<String,Object>();
    aproved.put("Evento", id);
    aproved.put("Membro", uidMembro);

    db.collection("aprovedMembers")
            .document(id)
            .collection("participantes")
            .document()
            .set(aproved)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ListaDeParticipantes.this, "Membro Participando", LENGTH_SHORT).show();
                excluirParticipante(uidMembro, mParticipacaoAdapter, position);
              }
            });
  }

  public void verificarParticipante(String id){
    Intent intent = new Intent(ListaDeParticipantes.this, PerfilMembros.class);
    intent.putExtra("id", id);
    startActivity(intent);;
  }

  private void excluirParticipante(String uidMember, final ParticipantesAdapter mParticipacaoAdapter, final int position) {
    db.collection("participating").document(uidMember+id)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Toast.makeText(ListaDeParticipantes.this, "Participante Excluído", LENGTH_SHORT).show();
                mParticipacaoAdapter.remove(mParticipacaoAdapter.getItem(position));
                mParticipacaoAdapter.notifyDataSetChanged();
              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(ListaDeParticipantes.this, "Falhou!", LENGTH_SHORT).show();
      }
    });
  }
}

