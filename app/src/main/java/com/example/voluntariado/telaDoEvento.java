package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.voluntariado.R.drawable.background;

public class telaDoEvento extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button botaoPouE;
    EditText titulo;
    EditText descricao;
    EditText endereco;
    EditText data;
    EditText hora;
    EditText numero;
    String id;

    String imgPadrao;
    //StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(imgPadrao);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_do_evento);

        /*if(getIntent().hasExtra("imagem")){
            this.imgPadrao = getIntent().getStringExtra("imagem");
        } else {
            this.imgPadrao = "teste.jpeg";
        }*/

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            compararUsuario(id);
            settingElementsOnScreen(id);

        }

    }

    private void settingElementsOnScreen(String id){
        DocumentReference docRef = db.collection("eventos").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Eventos evento = new Eventos();
                        evento = document.toObject(Eventos.class);
                        setarElementos(evento);
                    } else {
                        Log.d("EXISTE?", "Não");
                    }
                } else {
                    Log.d("OPS", "Falhou", task.getException());
                }
            }


            private void setarElementos(Eventos evento) {
                titulo = findViewById(R.id.tela_evento_titulo);
                descricao = findViewById(R.id.tela_evento_descricao);
                endereco = findViewById(R.id.tela_evento_endereco);
                data = findViewById(R.id.tela_evento_data);
                hora = findViewById(R.id.tela_evento_hora);
                numero = findViewById(R.id.tela_evento_numero);

                titulo.setText(evento.getTitulo());
                descricao.setText(evento.getDescricao());
                endereco.setText(evento.getEndereco());
                data.setText(evento.getData());
                hora.setText(evento.getHora());


                StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(evento.getImagem());
                final ImageView imgView = findViewById(R.id.imagemEvento);
                refStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(telaDoEvento.this)
                                .load(uri)
                                .into(imgView);
                    }
                }). addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }
    private void compararUsuario(String id) {
        botaoPouE = findViewById(R.id.botaoParticiparEditar);
        db.collection("eventos")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           for(QueryDocumentSnapshot document : task.getResult()) {
                               Eventos eventos = document.toObject(Eventos.class);
                               String user = mAuth.getUid().toUpperCase().trim();
                               String proprietario = eventos.getProprietario().toUpperCase().trim();

                               if (user.equals(proprietario)){
                                   Toast.makeText(telaDoEvento.this, "Iguais", LENGTH_SHORT).show();
                                   botaoPouE.setText("Editar");
                               } else {
                                   Toast.makeText(telaDoEvento.this, "Diferentes", Toast.LENGTH_LONG).show();
                                   testUserParticipation();
                               }

                           }
                       }else {
                           Toast.makeText(telaDoEvento.this, "erro", Toast.LENGTH_LONG).show();
                       }
                    }
                });
    }

    private void testUserParticipation() {
        String currentUser = mAuth.getUid();
        botaoPouE = findViewById(R.id.botaoParticiparEditar);

        db.collection("participating").document(currentUser+id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            botaoPouE.setText("Deixar de Participar");
                        } else {
                            botaoPouE.setText("Participar");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        botaoPouE.setText("Participar");
                    }
                });
   }

    public void botaoTelaEvento(View view){
        titulo = findViewById(R.id.tela_evento_titulo);
        descricao = findViewById(R.id.tela_evento_descricao);
        endereco = findViewById(R.id.tela_evento_endereco);
        data = findViewById(R.id.tela_evento_data);
        hora = findViewById(R.id.tela_evento_hora);
        numero = findViewById(R.id.tela_evento_numero);
        botaoPouE = findViewById(R.id.botaoParticiparEditar);
        if (botaoPouE.getText().equals("Participar")){

            Toast.makeText(telaDoEvento.this, "Participar", LENGTH_SHORT).show();
            participatingMembers();


        } else if(botaoPouE.getText().equals("Editar")){

            Toast.makeText(telaDoEvento.this, "Editar", LENGTH_SHORT).show();

            titulo.setEnabled(true);
            descricao.setEnabled(true);
            endereco.setEnabled(true);
            data.setEnabled(true);
            hora.setEnabled(true);
            numero.setEnabled(true);
            titulo.setBackgroundResource(R.drawable.caixa_de_texto);
            descricao.setBackgroundResource(R.drawable.caixa_de_texto_descricao_tela_do_evento);
            endereco.setBackgroundResource(R.drawable.caixa_de_texto);
            data.setBackgroundResource(R.drawable.caixa_de_texto);
            hora.setBackgroundResource(R.drawable.caixa_de_texto);
            numero.setBackgroundResource(R.drawable.caixa_de_texto);


            data.addTextChangedListener(MaskEditUtil.mask(data, MaskEditUtil.FORMAT_DATE));
            hora.addTextChangedListener(MaskEditUtil.mask(hora, MaskEditUtil.FORMAT_HOUR));

            botaoPouE.setText("Salvar");
        }
        else if (botaoPouE.getText().equals("Salvar")){

            savingDataToFirestore(titulo, descricao, endereco, data, hora, numero);
            titulo.setEnabled(false);
            descricao.setEnabled(false);
            endereco.setEnabled(false);
            data.setEnabled(false);
            hora.setEnabled(false);
            numero.setEnabled(false);
            titulo.setBackgroundResource(0);
            descricao.setBackgroundResource(0);
            endereco.setBackgroundResource(0);
            data.setBackgroundResource(0);
            hora.setBackgroundResource(0);
            numero.setBackgroundResource(0);

            botaoPouE.setText("Editar");

        } else if (botaoPouE.getText().equals("Deixar de Participar")) {
            deletingParticipation();
        }

        }

    public boolean onPrepareOptionsMenu(Menu menu){
        invalidateOptionsMenu();
        botaoPouE = findViewById(R.id.botaoParticiparEditar);
        if(botaoPouE.getText().equals("Editar")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.excluirmenu, menu);
            return true;
        } else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.mainmenu, menu);
            return true;
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.excluirEvento: excluirEvento(); //PRIMEIRA OPÇÃO VAI PARA A TELA DE CRIAÇÃO DE UM NOVO EVENTO
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletingParticipation() {
        String currentUser = mAuth.getUid();
        botaoPouE = findViewById(R.id.botaoParticiparEditar);

        db.collection("participating").document(currentUser+id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(telaDoEvento.this, "Deixou de participar", LENGTH_SHORT).show();
                        botaoPouE.setText("Participar");
                    }
                });

    }
    private void excluirEvento() {
        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            db.collection("eventos").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(telaDoEvento.this, "Evento Deletado", LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(telaDoEvento.this, "Falhou", LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void participatingMembers() {
        String currentUser = mAuth.getUid();
        botaoPouE = findViewById(R.id.botaoParticiparEditar);

        Map<String, Object> participate = new HashMap<>();
        participate.put("eventoID", id);
        participate.put("idParticipatingMember", currentUser);
        db.collection("participating").document(currentUser + id)
                .set(participate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(telaDoEvento.this, "Participando", LENGTH_SHORT).show();
                        botaoPouE.setText("Deixar de Participar");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(telaDoEvento.this, "Erro!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void savingDataToFirestore(EditText titulo, EditText descricao, EditText endereco, EditText data, EditText hora, EditText numero) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("titulo", titulo.getText().toString());
        evento.put("descricao", descricao.getText().toString());
        evento.put("endereco", endereco.getText().toString());
        evento.put("data", data.getText().toString());
        evento.put("hora", hora.getText().toString());
        evento.put("numero", numero.getText().toString());


        db.collection("eventos").document(id)
                .set(evento, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(telaDoEvento.this, "Evento editado", LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(telaDoEvento.this, "Deu erro!", LENGTH_SHORT).show();
                    }
                });

    }
}



