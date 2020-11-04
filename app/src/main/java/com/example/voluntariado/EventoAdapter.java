package com.example.voluntariado;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
//ACTIVITY RESPONSÁVEL PARA ADAPTAR OS ITEMS DA LISTA
public class EventoAdapter extends ArrayAdapter<Eventos> {

    public EventoAdapter(Context context, List<Eventos> object){
        super(context, 0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null) {
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_eventos, parent, false); //inflando layout do item de eventos
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //nomeando componentes do activity
        TextView titulotxt = (TextView) convertView.findViewById(R.id.evento_titulo);
        final TextView proprietariotxt = (TextView) convertView.findViewById(R.id.item_nome_proprietario);
        TextView servicotxt = (TextView) convertView.findViewById(R.id.item_tipo_servico);

        //pegando posição do item na classe Evento
        Eventos evento = getItem(position);

        //setando os elementos da view com os documentos que pega na classe evento.
        titulotxt.setText(evento.getTitulo());
        //proprietariotxt.setText(evento.getProprietario());
        servicotxt.setText(evento.getTitulo());

        db.collection("users1")
                .whereEqualTo("uuid", evento.getProprietario())
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
