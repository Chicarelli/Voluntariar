package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ListaDeParticipantes extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  String id;
  private AlertDialog alerta;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lista_de_participantes);

    if(getIntent().hasExtra("id")){
      id = getIntent().getStringExtra("id");
    }

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

                   AlertDialog.Builder builder = new AlertDialog.Builder(ListaDeParticipantes.this);

                   builder.setTitle("Opções");

                   builder.setMessage("O que deseja fazer?");

                   builder.setPositiveButton("Excluir Participante", new DialogInterface.OnClickListener() {

                     public void onClick(DialogInterface arg0, int arg1) {
                         String id1 = getIntent().getStringExtra("id");

                       String usuario = mParticipacaoAdapter.getItem(position).getIdParticipatingMember();

                       Toast.makeText(ListaDeParticipantes.this, usuario, LENGTH_SHORT).show();
                       //mEventoAdapter.getItem(position).getId())

                       
                       db.collection("participating").document(usuario+id1)
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
                   });

                   builder.setNegativeButton("Verificar Participante", new DialogInterface.OnClickListener(){

                     public void onClick(DialogInterface arg0, int arg1){
                       Toast.makeText(ListaDeParticipantes.this, "É isso ai não", LENGTH_SHORT).show();
                       verificarParticipante(mParticipacaoAdapter.getItem(position).getIdParticipatingMember());
                       //Toast.makeText(ListaDeParticipantes.this, mParticipacaoAdapter.getItem(position).getIdParticipatingMember(), LENGTH_SHORT).show();
                     }
                   });

                   alerta = builder.create();

                   alerta.show();
                  }
                });

              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(ListaDeParticipantes.this, "Falhou", LENGTH_SHORT).show();
      }

    });
  }

  public void verificarParticipante(String id){
    Intent intent = new Intent(ListaDeParticipantes.this, PerfilMembros.class);
    intent.putExtra("id", id);
    startActivity(intent);;
  }



}