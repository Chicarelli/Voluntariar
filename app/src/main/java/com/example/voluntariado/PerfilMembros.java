package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PerfilMembros extends AppCompatActivity {


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_membros);

        puxandoID();
        buscarDadosFirestore(this.id);
    }

    public void puxandoID(){
        if (getIntent().hasExtra("id")){
            String id = getIntent().getStringExtra("id");
            this.id = id;
        }
    }

    public void buscarDadosFirestore(String id){
        db.collection("users1").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            String nome = user.getNome();
                            String email = user.getEmail();
                            //String idade = user.getDataNasc().toString();

                            setandoTextView(nome, email);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void setandoTextView(String nome, String email){
        TextView edtNome = findViewById(R.id.edtNome_perfil_membros);
        TextView edtIdade = findViewById(R.id.edtIdade_perfil_membros);
        TextView edtEmail = findViewById(R.id.edtEmail_perfil_membros);

        edtNome.setText(nome);
        edtEmail.setText(email);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(PerfilMembros.this, ChatActivity.class);
        intent.putExtra("idMembro", id);
        startActivity(intent);
    }

}