package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

public class SolicitacoesDeParticipantes extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String idEvento;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes_de_participantes);

        RecyclerView rv = findViewById(R.id.recyclerView_SolicitacoesDeParticipantes);
        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(SolicitacoesDeParticipantes.this));
        rv.setAdapter(adapter);

        pegarId();
        fetchMembers();
    }

    private void fetchMembers() {
        db.collection("aprovedMembers")
                .document(idEvento)
                .collection("participantes")
                .whereEqualTo("Evento", idEvento);
    }

    private void pegarId() {
        if(getIntent().hasExtra("idEvento")) idEvento = getIntent().getStringExtra("idEvento");
    }
}


class UserItems extends Item<GroupieViewHolder> {

    private final User user;

    private UserItems(User user) {
        this.user = user;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {

    }

    @Override
    public int getLayout() {
        return R.layout.item_contacts_messages;
    }
}