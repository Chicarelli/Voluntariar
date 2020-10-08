package com.example.voluntariado;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ParticipantesAdapter extends ArrayAdapter<ParticipacaoEvento> {

  String uuid;

  public ParticipantesAdapter(Context context, List<ParticipacaoEvento> object){
    super(context, 0, object);
    this.uuid = uuid;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent){
    if(convertView == null) {
      convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.lista_participantes, parent, false); //inflando layout do item de eventos
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //nomeando componentes do activity
    final TextView proprietariotxt = (TextView) convertView.findViewById(R.id.item_nome_proprietario_lista);


    //pegando posição do item na classe Evento
    ParticipacaoEvento participacaoEvento = getItem(position);

    //setando os elementos da view com os documentos que pega na classe evento.

    //proprietariotxt.setText(evento.getProprietario());

    //BUSCANDO O NOME DO PROPRIETÁRIO UTILIZANDO O UUID DO MESMO NO FIREBASE FIRESTORE COLEÇÃO USERS1 PARA SETAR NO PROPRIETARIO.TXT

    db.collection("users1")
            .whereEqualTo("uuid", participacaoEvento.getIdParticipatingMember())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                  for(QueryDocumentSnapshot document: task.getResult()){
                    User user = document.toObject(User.class);
                    proprietariotxt.setText(user.getNome());

                  }
                } else {
                  Log.d("TAG", "Deu ruim");
                }
              }
            });

    return convertView;
  }


}
