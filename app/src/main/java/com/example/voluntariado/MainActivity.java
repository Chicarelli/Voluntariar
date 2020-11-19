package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Types;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {  //ACITIVTY QUE SERÁ APRESENTADO A LISTA DE EVENTOS DISPONÍVEIS PARA O USUÁRIO

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    DrawerLayout drawerLayout;
    ImageView optionButton;
    Spinner spinner;
    String citySelected;

    TextView noEvento;
    RecyclerView rv;

    private GroupAdapter adapterRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verificar();

        drawerLayout = findViewById(R.id.drawer_layout);
        optionButton = findViewById(R.id.more_options_imgv);
        registerForContextMenu(optionButton);

        spinner = findViewById(R.id.spinnerMain);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        noEvento = findViewById(R.id.textview_no_evento);
        rv = findViewById(R.id.rv_mainList);
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this ));

        adapterRv = new GroupAdapter();
        rv.setAdapter(adapterRv);
    }

    private void verificar() {
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
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
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void messagesScreen(View view){
        MainActivity.redirectActivity(this, MessageActivity.class);
    }

    public void meuPerfil(View view){
        redirectActivity(this, MyProfileActivity.class);
    }

    public static void redirectActivity(Activity activity, Class aclass){
        Intent intent = new Intent(activity, aclass);
        activity.startActivity(intent);
        activity.finish();
    }

    public void ClickHome(View view){
        recreate();
        closeDrawer(drawerLayout);
    }

    public void myEvents(View view) {
        redirectActivity(this, MeusEventos.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySelected = parent.getItemAtPosition(position).toString();
                adapterRv.clear();
                fetchList(citySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchList(String citySelected) {
        db.collection("eventos")
                .whereEqualTo("cidade", citySelected)
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges != null){
                            for (DocumentChange doc: documentChanges) {

                                //Fazendo comparação da hora para listar apenas eventos que ainda irão acontecer:
                                Date date = new Date(System.currentTimeMillis());
                                Date dataFormatada = dateFormatting(doc.getDocument().get("data").toString());

                                if(date.before(dataFormatada) || date.getDate()==dataFormatada.getDate()) {
                                    Eventos evento = doc.getDocument().toObject(Eventos.class);
                                    adapterRv.add(new EventoItem(evento));
                                    noEvento.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        if(queryDocumentSnapshots.getDocumentChanges().isEmpty()){
                            noEvento.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private Date dateFormatting(String data) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date dataFormatada = null;
        try{
            dataFormatada = formato.parse(data);
        } catch(ParseException err){
            err.printStackTrace();;
        }
        return dataFormatada;
    }

    private class EventoItem extends Item<GroupieViewHolder> {

        private final Eventos eventos;


        private EventoItem (Eventos eventos) {
            this.eventos = eventos;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
                TextView titulo = viewHolder.itemView.findViewById(R.id.evento_titulo);
                TextView rua = viewHolder.itemView.findViewById(R.id.item_nome_proprietario);
                TextView data = viewHolder.itemView.findViewById(R.id.item_tipo_servico);

                titulo.setText(eventos.getTitulo());
                data.setText(eventos.getData());
                rua.setText(eventos.getEndereco());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, telaDoEvento.class);
                        intent.putExtra("id", eventos.getId());
                        startActivity(intent);
                        finish();
                    }
                });
        }

        @Override
        public int getLayout() {
                return R.layout.item_eventos;
        }
    }

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

    private void deslogar(){
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }


}
