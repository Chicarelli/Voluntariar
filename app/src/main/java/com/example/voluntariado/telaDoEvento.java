package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class telaDoEvento extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView telaEventoTitulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_do_evento);

        if(getIntent().hasExtra("id")){
            String id = getIntent().getStringExtra("id");

            DocumentReference docRef = db.collection("eventos").document(id);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Toast.makeText(telaDoEvento.this, "EIXSTE", Toast.LENGTH_SHORT).show();
                            Eventos evento = new Eventos();
                            evento = document.toObject(Eventos.class);
                            setarElementos(evento);
                        } else {
                            Log.d("EXISTE?", "Não");
                        }
                    } else {
                        Log.d("OPS", "Falhou", task.getException());
                    }
                }

                private void setarElementos(Eventos evento) {
                    TextView titulo = findViewById(R.id.tela_evento_titulo);
                    TextView descricao = findViewById(R.id.tela_evento_descricao);
                    TextView endereco = findViewById(R.id.tela_evento_endereco);
                    TextView data = findViewById(R.id.tela_evento_data);
                    TextView hora = findViewById(R.id.tela_evento_hora);
                    TextView numero = findViewById(R.id.tela_evento_numero);

                    titulo.setText(evento.getTitulo());
                    descricao.setText(evento.getDescricao());
                    endereco.setText(evento.getEndereco());
                    data.setText(evento.getData());
                    hora.setText(evento.getHora());
                    numero.setText("22");
                }
            });
        }

    }
}