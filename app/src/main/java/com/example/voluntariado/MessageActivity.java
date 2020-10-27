package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

public class MessageActivity extends AppCompatActivity {


  private GroupAdapter adapter;
  TextView txt_contacts;
  ImageView botaoVoltar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);

    txt_contacts = findViewById(R.id.txtView_contacts);
    botaoVoltar = findViewById(R.id.backToMain);

    RecyclerView rv = findViewById(R.id.rv_contacts);
    rv.setLayoutManager(new LinearLayoutManager(this));

    adapter = new GroupAdapter<>();
    rv.setAdapter(adapter);

    fetchLastMessage();
  }

  public void backToMain(View view){
    Intent intent = new Intent(MessageActivity.this, MainActivity.class);
    startActivity(intent);
    finishAffinity();
  }

  public void screenContacts(View view){

    Intent intent = new Intent(MessageActivity.this, MainActivity.class);
    startActivity(intent);
  }

  private void fetchLastMessage() {

    String uid = FirebaseAuth.getInstance().getUid();

    FirebaseFirestore.getInstance().collection("last-messages")
            .document(uid)
            .collection("contacts")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                if(documentChanges != null) {
                  for (DocumentChange doc: documentChanges) {

                    if (doc.getType() == DocumentChange.Type.ADDED){

                        Contact contact = doc.getDocument().toObject(Contact.class);

                        adapter.add(new ContactItem(contact));

                    }

                  }
                }
              }
            });
  }

  private class ContactItem extends Item<GroupieViewHolder> {

    private final Contact contact;

    private ContactItem(Contact contact) {
      this.contact = contact;
    }


    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
      TextView txtuser = viewHolder.itemView.findViewById(R.id.username);
      TextView lastMessage = viewHolder.itemView.findViewById(R.id.last_message);

      txtuser.setText(contact.getNome());
      lastMessage.setText(contact.getLastMessage());

    }

    @Override
    public int getLayout() {
      return R.layout.item_contacts_messages;
    }
  }
}