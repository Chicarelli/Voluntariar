package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
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

    DrawerLayout drawerLayout;
    ImageView optionButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        optionButton = findViewById(R.id.more_options_imgv);

        registerForContextMenu(optionButton);




        final ListView mEventosListView = (ListView) findViewById(R.id.eventosList);


        db.collection("eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Eventos> mEventosList = new ArrayList<>();
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //instanciando Eventos, convertendo para objeto e passando para a classe.
                        Eventos evento = document.toObject(Eventos.class);


                        //adicionando a instancia à lista
                        mEventosList.add(evento);

                    }

                    //instanciando o Adapter
                    final EventoAdapter mEventoAdapter = new EventoAdapter(MainActivity.this, mEventosList);
                    //Setando o Adapter com os eventos.
                    mEventosListView.setAdapter(mEventoAdapter);

                    //INICIAR A INTENT ENVIANDO OS DADOS DO ITEM CLICADO,
                    mEventosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent = new Intent(MainActivity.this, telaDoEvento.class);
                            intent.putExtra("id", mEventoAdapter.getItem(position).getId());
                            startActivity(intent);

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    Log.d("TAG", task.toString());
                }
            }
        });
    }

    public void moreOptions(View view){
        PopupMenu popup = new PopupMenu(MainActivity.this, view);

        popup.getMenuInflater().inflate(R.menu.mainmenu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.criarEvento: telaCriarEvento(); //PRIMEIRA OPÇÃO VAI PARA A TELA DE CRIAÇÃO DE UM NOVO EVENTO
                        return true;

                    case R.id.info: deslogar(); //SEGUNDA OPÇÃO, DESLOGA O USUÁRIO E VAI PARA A TELA DE LOGIN NOVAMENTE.
                        return true;

                    default:
                        return true;

                }
            }
        });

        popup.show();
    }

    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open Drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        //Close
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //Closing drawerLayout

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    public void ClickHome(View view){
        //Recreate

        recreate();
        closeDrawer(drawerLayout);
    }

    public void myEvents(View view) {
        Intent intent = new Intent(MainActivity.this, MeusEventos.class);
        startActivity(intent);
    }

    public void Logout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Logout");

        builder.setMessage("Tem certeza que deseja sair?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();

                System.exit(0);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout);
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
