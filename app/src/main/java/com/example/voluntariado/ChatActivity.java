package com.example.voluntariado;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import static androidx.recyclerview.widget.RecyclerView.*;

public class ChatActivity extends AppCompatActivity {

  private GroupAdapter adapter;
  private  boolean isLeft;
  EditText edtChat;
  String idMembro;
  FirebaseFirestore db = FirebaseFirestore.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    pegarId();

    RecyclerView rv = findViewById(R.id.recycler_chat);
    edtChat = findViewById(R.id.edt_chat_Conversa);
    Button btnChat = findViewById(R.id.bt_enviar_mensagem);
    btnChat.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });

    adapter = new GroupAdapter();
    rv.setLayoutManager(new LinearLayoutManager(this));
    rv.setAdapter(adapter);

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

    String fromId = FirebaseAuth.getInstance().getUid();
    String toId = this.idMembro;
    long timestamp = System.currentTimeMillis();

    Message message = new Message();
    message.setFromId(fromId);
    message.setToId(toId);
    message.setTimestamp(timestamp);
    message.setText(text);

    if(!message.getText().isEmpty()){
      db.collection("/conversations")
              .document(fromId)
              .collection(toId)
              .add(message)
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                  Log.d("Teste", documentReference.getId());
                }
              }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.e("Teste", e.getMessage(), e);
        }
      });
      db.collection("/conversations")
              .document(toId)
              .collection(fromId)
              .add(message)
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                  Log.d("Teste", documentReference.getId());
                }
              }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.e("Teste", e.getMessage(), e);
        }
      });
    }
    Toast.makeText(ChatActivity.this, idMembro, Toast.LENGTH_SHORT ).show();
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
      return message.getFromId() == FirebaseAuth.getInstance().getUid() ? R.layout.item_from_message : R.layout.item_to_message;
    }
  }
}