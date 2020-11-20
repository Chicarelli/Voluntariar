package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class AprovedMembers extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String idEvento;
    private GroupAdapter adapter;
    TextView noSolicitation;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes_de_participantes);

        rv = findViewById(R.id.recyclerView_SolicitacoesDeParticipantes);
        noSolicitation = findViewById(R.id.noSolicitation);

        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(AprovedMembers.this));
        rv.setAdapter(adapter);

        pegarId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMembers();
    }

    private void fetchMembers() {
        adapter.clear();
        db.collection("aprovedMembers")
                .document(idEvento)
                .collection("participantes")
                .whereEqualTo("Evento", idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                User user = doc.toObject(User.class);
                                searchingUserWithUuid(user.getUuid());
                            }
                        }
                        if(queryDocumentSnapshots.isEmpty()){
                            rv.setVisibility(View.INVISIBLE);
                            noSolicitation.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

  private void searchingUserWithUuid(String uuid) {
      db.collection("users1")
              .document(uuid)
              .get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  User user = task.getResult().toObject(User.class);
                  adapter.add(new UserItems(user));
                }
              });
  }

  private void pegarId() {
        if(getIntent().hasExtra("idevento")) {
            idEvento = getIntent().getStringExtra("idevento");
        }
    }

 private class UserItems extends Item<GroupieViewHolder> {

    private final User user;

    UserItems(User user) {
        this.user = user;
    }

    @Override
    public void bind(@NonNull final GroupieViewHolder viewHolder, final int position) {
        TextView nomeParticipante = viewHolder.itemView.findViewById(R.id.username);

        nomeParticipante.setText(user.getNome());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(AprovedMembers.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.options_on_participantes, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
              @Override
              public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                  case R.id.verificarMembro: verifyMember(user.getUuid());
                  return true;

                  case R.id.excluirMembro: removeMember(user.getUuid(), idEvento, adapter, getItem(position));
                  return true;

                  default: return true;
                }
              }
            });
            popupMenu.show();
          }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.item_contacts_messages;
    }
}

  private void removeMember(final String uuid, String idEvento, final GroupAdapter adapter, final Item position) {
      db.collection("aprovedMembers")
              .document(idEvento)
              .collection("participantes")
              .document(uuid)
              .delete()
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(AprovedMembers.this, "Participante Deletado", Toast.LENGTH_SHORT).show();
                  adapter.remove(position);
                  adapter.notifyDataSetChanged();
                }
              });

      db.collection("memberAprovados")
              .document(uuid)
              .collection("eventosAprovados")
              .whereEqualTo("eventoID", idEvento)
              .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              List<DocumentSnapshot> documents = task.getResult().getDocuments();
              if (!documents.isEmpty()){
                  String uidEvento = documents.get(0).getId();;
                  db.collection("memberAprovados")
                          .document(uuid)
                          .collection("eventosAprovados")
                          .document(uidEvento)
                          .delete();
              }
          }
      });
  }

  private void verifyMember(String uuid) {
      Intent intent = new Intent(AprovedMembers.this, PerfilMembros.class);
      intent.putExtra("id", uuid);
      startActivity(intent);
  }

    public void backToMain(View view){
        finish();
    }
}