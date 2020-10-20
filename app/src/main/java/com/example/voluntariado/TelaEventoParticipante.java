package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.voluntariado.R.drawable.background_caixa_editing_notfocusable;
import static com.example.voluntariado.R.drawable.background_caixa_editing_telaeventoparticipating;

public class TelaEventoParticipante extends AppCompatActivity {

  private String id;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  EditText evento_titulo;
  EditText evento_complemento;
  EditText evento_descricao;
  EditText evento_hora;
  EditText evento_endereco;
  EditText evento_numero;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tela_evento_participante);
    evento_titulo = findViewById(R.id.editing_title);
    evento_complemento = findViewById(R.id.editing_complemento);
    evento_descricao = findViewById(R.id.editing_descricao);
    evento_hora = findViewById(R.id.editing_hora);
    evento_endereco = findViewById(R.id.editing_endereco);
    evento_numero = findViewById(R.id.editing_numero);

    if(getIntent().hasExtra("id")){
      this.id = getIntent().getStringExtra("id");
    }

    recuperarEvento(); //Recuperando o evento pelo ID passado entre intents
    //onFocus();
  }

  private void recuperarEvento() { //Recuperando eventos do Firestore
    db.collection("eventos").document(id)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                 DocumentSnapshot document = task.getResult();
                 if(document.exists()){
                   Eventos eventos = new Eventos();
                   eventos = document.toObject(Eventos.class);
                   setElements(eventos);//Passando objeto eventos para selecionar na tela
                 }
               }
              }
            });
  }

  private void setElements(Eventos eventos) { //Setando os editviews com os dados que vieram do firestore

    evento_titulo.setText(eventos.getTitulo());
    evento_complemento.setText(eventos.getComplemento());
    evento_endereco.setText(eventos.getEndereco());
    evento_descricao.setText(eventos.getDescricao());
    evento_hora.setText(eventos.getHora());
    evento_numero.setText(eventos.getNumero());
  } //Setando dandos do Firestore nos EditText
}