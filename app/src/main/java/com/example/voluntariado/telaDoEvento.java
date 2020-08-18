package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class telaDoEvento extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
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
            String id = getIntent().getStringExtra("id");

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
                    TextView titulo = findViewById(R.id.tela_evento_titulo);
                    TextView descricao = findViewById(R.id.tela_evento_descricao);
                    TextView endereco = findViewById(R.id.tela_evento_endereco);
                    TextView data = findViewById(R.id.tela_evento_data);
                    TextView hora = findViewById(R.id.tela_evento_hora);
                    TextView numero = findViewById(R.id.tela_evento_numero);

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

    }

}