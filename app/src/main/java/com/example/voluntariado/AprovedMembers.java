package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

public class AprovedMembers extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String idEvento;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes_de_participantes);

        RecyclerView rv = findViewById(R.id.recyclerView_SolicitacoesDeParticipantes);
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
                                adapter.add(new UserItems(user));
                            }
                        }
                    }
                });
        Toast.makeText(this, idEvento, Toast.LENGTH_SHORT).show();
    }

    private void pegarId() {
        if(getIntent().hasExtra("idevento")) {
            idEvento = getIntent().getStringExtra("idevento");
        }
    }
}


 class UserItems extends Item<GroupieViewHolder> {

    private final User user;

    UserItems(User user) {
        this.user = user;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        TextView nomeParticipante = viewHolder.itemView.findViewById(R.id.username);

        nomeParticipante.setText(user.getUuid());

    }

    @Override
    public int getLayout() {
        return R.layout.item_contacts_messages;
    }
}