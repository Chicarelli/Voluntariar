package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class MeusEventos extends AppCompatActivity {

    private String id = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private Eventos mi;

    DrawerLayout dl;
    RecyclerView rv;
    TextView noEvent;

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        this.dl = findViewById(R.id.drawer_layout);
         rv = findViewById(R.id.rv_meus_eventos_lista);
         noEvent = findViewById(R.id.noEvent);

        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(MeusEventos.this));
        rv.setAdapter(adapter);
    }

    public void moreOptions(View view){
        PopupMenu popupMenu = new PopupMenu(MeusEventos.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.menu_on_meus_eventos, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pendingSolicitations: pendingSolicitations();
                    return true;

                    default: return true;
                }
            }
        });

        popupMenu.show();
    }

    private void pendingSolicitations() {
       Intent intent = new Intent(MeusEventos.this, SolicitacoesPendentes.class);
       startActivity(intent);
    }

    private void fetchEventos() {
        adapter.clear();
        db.collection("memberAprovados")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("eventosAprovados")
                .whereEqualTo("uidMember", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        if(!documentSnapshots.isEmpty()){
                            for (DocumentSnapshot doc: documentSnapshots) {
                                fetchEventos(doc.get("eventoID").toString());
                            }
                        }
                    }
                });

    }

    private void fetchEventos(String eventoID) {
        //Toast.makeText(this, eventoID, Toast.LENGTH_SHORT).show();
        db.collection("eventos")
                .whereEqualTo("id", eventoID)
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        if(!documentSnapshots.isEmpty()){
                            for (DocumentSnapshot doc: documentSnapshots) {
                                Eventos evento = doc.toObject(Eventos.class);
                                adapter.add(new EventoItem(evento));
                            }
                        }
                    }
                });
    }


    private class EventoItem extends Item<GroupieViewHolder> {

        private final Eventos eventos;

        private EventoItem (Eventos eventos) {
            this.eventos = eventos;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView titulo = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_titulo);
            TextView data = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_data);
            TextView hora = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_hora);

            titulo.setText(eventos.getTitulo());
            data.setText(eventos.getData());
            hora.setText(eventos.getHora());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeusEventos.this, telaDoEvento.class);
                    intent.putExtra("id", eventos.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getLayout() {
            return R.layout.item_meus_eventos;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(dl);
    }

    public void messagesScreen(View view){
        MainActivity.redirectActivity(this, MessageActivity.class);
    }

    public void ClickMenu(View view){
        MainActivity.openDrawer(dl);
    }

    public void ClickLogo(View view){
        //Close
        MainActivity.closeDrawer(dl);
    }

    public void ClickHome(View view){
        //Redirecting to MainActivity
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void meuPerfil(View view){
        MainActivity.redirectActivity(this, MyProfileActivity.class);
    }

    public void myEvents(View view) {
        recreate();
        MainActivity.closeDrawer(dl);
    }

    public void Logout(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MeusEventos.this);

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
    protected void onDestroy() {
        super.onDestroy();;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MeusEventos.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        fetchEventos();
    }
}