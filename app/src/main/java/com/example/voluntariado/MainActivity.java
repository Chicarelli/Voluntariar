package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {  //ACITIVTY QUE SERÁ APRESENTADO A LISTA DE EVENTOS DISPONÍVEIS PARA O USUÁRIO

    //fazendo as instâcias do firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //instancia do ListView da activity
    private ListView mEventoListView;

    //Instanciando classe Adapter e uma Lista da classe Eventos.
    private EventoAdapter mEventoAdapter;
    private ArrayList<Eventos> mEventosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Lista de eventos");
        final ListView mEventosListView = (ListView) findViewById(R.id.eventosList);

        //Logo pegando todos os eventos criados na coleção EVENTOS do Database Firestore do Firebase.

        db.collection("eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Eventos> mEventosList = new ArrayList<>();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        //instanciando Eventos, convertendo para objeto e passando para a classe.
                        Eventos evento = document.toObject(Eventos.class);

                        //adicionando a instancia à lista
                        mEventosList.add(evento);

                    }

                    //instanciando o Adapter
                    final EventoAdapter mEventoAdapter = new EventoAdapter(MainActivity.this, mEventosList);
                    //Setando o Adapter com os eventos.
                    mEventosListView.setAdapter(mEventoAdapter);

                    //CONSEGUI CARALHO, AQUI VAI INICIAR A INTENT ENVIANDO OS DADOS DO ITEM CLICADO, É NOI? FLW!
                    mEventosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(MainActivity.this, telaDoEvento.class);
                        intent.putExtra("id", mEventoAdapter.getItem(position).getId());
                        startActivity(intent);

                        }
                    });
                }
                else {
                    Log.d("TAG", "algo deu errado");
                }
            }
        });
    }

    //INFLANDO MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    //Setando comportamento nas opções do MENU
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.criarEvento: telaCriarEvento(); //PRIMEIRA OPÇÃO VAI PARA A TELA DE CRIAÇÃO DE UM NOVO EVENTO
            return true;

            case R.id.info: deslogar(); //SEGUNDA OPÇÃO, DESLOGA O USUÁRIO E VAI PARA A TELA DE LOGIN NOVAMENTE.
            return true;

            default:
            return super.onOptionsItemSelected(item);

        }
    }

    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }

    }

    private void telaCriarEvento(){
        Intent intent = new Intent(MainActivity.this, CriarEventoFisico.class);
        startActivity(intent);
    }

    //METODO RESPONSÁVEL POR DESLOGAR O USUÁRIO, MANDAR PARA A ACITIVITY DE LOGIN E FINALIZAR ESTA.
    private void deslogar(){
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
