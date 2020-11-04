package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class ChatActivity extends AppCompatActivity {

  private GroupAdapter adapter;
  private  boolean isLeft;
  EditText edtChat;
  String idMembro;
  TextView chatTitle;
  RecyclerView rv;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  private User me;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    chatTitle = findViewById(R.id.chat_username_title);
    rv = findViewById(R.id.recycler_chat);
    edtChat = findViewById(R.id.edt_chat_Conversa);
    Button btnChat = findViewById(R.id.bt_enviar_mensagem);

    adapter = new GroupAdapter();
    rv.setLayoutManager(new LinearLayoutManager(this));
    rv.setAdapter(adapter);

    pegarId();
    setarTitulo();
    btnChat.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
    definindoUsuario();
  }

  private void definindoUsuario() {
    db.collection("users1")
            .document(FirebaseAuth.getInstance().getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                me = documentSnapshot.toObject(User.class);
                fetchMessages();
              }
            });
  }

  private void setarTitulo() { //Setando toolbar com nome do usuário com o qual se está falando
    db.collection("users1")
            .document(idMembro)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                chatTitle.setText(documentSnapshot.get("nome").toString());
              }
            });
  }

  private void fetchMessages() {
    adapter.clear();
    if (me!= null) {
      String fromId = me.getUuid();
      String toId = idMembro;

      db.collection("conversations")
              .document(fromId)
              .collection(toId)
              .orderBy("timestamp", Query.Direction.ASCENDING)
              .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                  List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                  if (documentChanges != null) {
                    for (DocumentChange doc: documentChanges) {
                      if (doc.getType() == DocumentChange.Type.ADDED) {
                        Message message = doc.getDocument().toObject(Message.class);
                        adapter.add(new MessageItem(message));
                        rv.scrollToPosition(rv.getAdapter().getItemCount()-1);
                      }
                    }
                  }
                }
              });
    }

  }

  private void pegarId() {
      if (getIntent().hasExtra("id")){
        String id = getIntent().getStringExtra("id");
        this.idMembro = id;
     }
  }

  private void sendMessage() {
    String text = edtChat.getText().toString();
    edtChat.setText(null);

    final String fromId = FirebaseAuth.getInstance().getUid();
    final String toId = this.idMembro;
    long timestamp = System.currentTimeMillis();

    final Message message = new Message();
    message.setFromId(fromId);
    message.setToId(toId);
    message.setTimestamp(timestamp);
    message.setText(text);

    if(!message.getText().isEmpty()){
      savingMessageFromId(fromId, toId, message);
      savingMessageToId(toId, fromId, message);
    }
    int qtd = rv.getAdapter().getItemCount();
    rv.scrollToPosition(qtd);
  }

  private void savingMessageToId(final String toId, final String fromId, final Message message) {
    db.collection("/conversations")
            .document(toId)
            .collection(fromId)
            .add(message)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
              @Override
              public void onSuccess(DocumentReference documentReference) {
                Log.d("Teste", documentReference.getId());

                Contact contact = new Contact();
                contact.setUuid(fromId);
                contact.setTimestamp(message.getTimestamp());
                contact.setNome(me.getNome());
                contact.setLastMessage(message.getText());

                FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(toId)
                        .collection("contacts")
                        .document(fromId)
                        .set(contact);

              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.e("Teste", e.getMessage(), e);
      }
    });
  }

  private void savingMessageFromId(final String fromId, final String toId, final Message message) {
    db.collection("/conversations")
            .document(fromId)
            .collection(toId)
            .add(message)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
              @Override
              public void onSuccess(DocumentReference documentReference) {
                Log.d("Teste", documentReference.getId());

                Contact contact = new Contact();
                contact.setUuid(toId);
                contact.setTimestamp(message.getTimestamp());
                contact.setNome(me.getNome());
                contact.setLastMessage(message.getText());

                FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(fromId)
                        .collection("contacts")
                        .document(toId)
                        .set(contact);
              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.e("Teste", e.getMessage(), e);
      }
    });
  }

  private class MessageItem extends Item<GroupieViewHolder> {

    private final Message message;

    private MessageItem (Message message) {
      this.message = message;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
      TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_message);

      txtMsg.setText(message.getText());
    }

    @Override
    public int getLayout() {
      return message.getFromId().equals(FirebaseAuth.getInstance().getUid()) ? R.layout.item_from_message : R.layout.item_to_message;
    }
  }
}