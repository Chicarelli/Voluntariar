package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ListaDeParticipantes extends AppCompatActivity {
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  String id;

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
                List<ParticipacaoEvento> mParticipantesList = new ArrayList<>();
                if(task.isSuccessful()){
                  for (QueryDocumentSnapshot document: task.getResult()){
                    ParticipacaoEvento participacaoEvento = document.toObject(ParticipacaoEvento.class);

                    mParticipantesList.add(participacaoEvento);

                    Toast.makeText(ListaDeParticipantes.this, participacaoEvento.getIdParticipatingMember(), LENGTH_SHORT).show();
                  }
                }
                final ParticipantesAdapter mParticipacaoAdapter = new ParticipantesAdapter(ListaDeParticipantes.this, mParticipantesList);
                mParticipacaoListView.setAdapter(mParticipacaoAdapter);

              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(ListaDeParticipantes.this, "Falhou", LENGTH_SHORT).show();
      }
    });
  }
}