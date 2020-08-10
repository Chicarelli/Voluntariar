package com.example.voluntariado;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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

        //nomeando componentes do activity
        TextView titulotxt = (TextView) convertView.findViewById(R.id.evento_titulo);
        TextView enderecotxt = (TextView) convertView.findViewById(R.id.evento_endereco);
        TextView datatxt = (TextView) convertView.findViewById(R.id.evento_data);
        TextView horatxt = (TextView) convertView.findViewById(R.id.evento_hora);
        TextView desctxt = (TextView) convertView.findViewById(R.id.evento_desc);

        //pegando posição do item na classe Evento
        Eventos evento = getItem(position);

        //setando os elementos da view com os documentos que pega na classe evento.
        titulotxt.setText(evento.getTitulo());
        enderecotxt.setText(evento.getEndereco());
        datatxt.setText(evento.getData());
        horatxt.setText(evento.getHora());
        desctxt.setText(evento.getDescricao());
        return convertView;


    }

}
