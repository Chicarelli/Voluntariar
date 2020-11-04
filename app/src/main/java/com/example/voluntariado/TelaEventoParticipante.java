package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.voluntariado.R.drawable.abc_edit_text_material;
import static com.example.voluntariado.R.drawable.background_caixa_editing_notfocusable;
import static com.example.voluntariado.R.drawable.background_caixa_editing_telaeventoparticipating;

public class TelaEventoParticipante extends AppCompatActivity {

  private String id;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  EditText evento_titulo;
  EditText evento_complemento;
  EditText evento_descricao;
  EditText evento_hora;
  EditText evento_endereco;
  EditText evento_numero;
  FloatingActionButton fab_button;
  ImageView bt_change_photo;
  ImageView imgView;
  Bitmap bitmap;
  Uri downloadUri;
  private int PICK_IMAGE = 1234;
  StorageReference imagesRef = storage.getReference().child("images");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tela_evento_participante);
    evento_titulo = findViewById(R.id.editing_title);
    evento_complemento = findViewById(R.id.editing_complemento);
    evento_descricao = findViewById(R.id.editing_descricao);
    evento_hora = findViewById(R.id.editing_hora);
    evento_endereco = findViewById(R.id.editing_endereco);
    evento_numero = findViewById(R.id.editing_numero);
    fab_button = findViewById(R.id.fab_button_save_edit);
    bt_change_photo = findViewById(R.id.img_upload_foto);
    imgView = findViewById(R.id.img_evento_participating);
    //evento_data.addTextChangedListener(MaskEditUtil.mask(evento_titulo, MaskEditUtil.FORMAT_DATE));
    evento_hora.addTextChangedListener(MaskEditUtil.mask(evento_hora, MaskEditUtil.FORMAT_HOUR));

    if(getIntent().hasExtra("id")){
      this.id = getIntent().getStringExtra("id");
    }

    recuperarEvento(); //Recuperando o evento pelo ID passado entre intents
    onFocus();

    fab_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveModifications();
      }
    });
    
    bt_change_photo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        changePhoto();
      }
    });

  }

  private void changePhoto() {
    Intent gallery = new Intent();
    gallery.setType("image/*");
    gallery.setAction(Intent.ACTION_GET_CONTENT);

    startActivityForResult(Intent.createChooser(gallery, "Selecione"), PICK_IMAGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
      Uri imageUri = data.getData();
      try{
        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        if(bitmap!=null){
          Glide.with(TelaEventoParticipante.this)
                  .load(bitmap)
                  .into(imgView);
        }
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  private void saveModifications() {
    String imagem = salvarFoto(evento_titulo.getText().toString());
    Map<String, Object> evento = new HashMap<>();
    evento.put("titulo",  evento_titulo.getText().toString());
    evento.put("complemento", evento_complemento.getText().toString());
    evento.put("endereco", evento_endereco.getText().toString());
    evento.put("descricao", evento_descricao.getText().toString());
    evento.put("hora", evento_hora.getText().toString());
    evento.put("numero", evento_numero.getText().toString());
    if(imagem != null) {
      evento.put("imagem", imagem);
    }

    db.collection("eventos").document(id)
            .set(evento, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Intent intent = new Intent(TelaEventoParticipante.this, telaDoEvento.class);
                if(bitmap != null){
                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                  byte[] imageInByte = stream.toByteArray();
                  intent.putExtra("imagem", imageInByte);
                }
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(TelaEventoParticipante.this, e.getMessage(), LENGTH_SHORT).show();
              }
            });
  }

  protected String salvarFoto(String titulo){

    if(bitmap != null) {
      CriarEventoFisicoUtil criarEventoFisicoUtil = new CriarEventoFisicoUtil(mAuth.getUid(), titulo);
      String nameCreated = criarEventoFisicoUtil.getCreatedName();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      byte[] dados = baos.toByteArray();

      final UploadTask uploadTask = imagesRef.child(nameCreated).putBytes(dados);
      uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          // Handle unsuccessful uploads
        }
      }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
          downloadUri = taskSnapshot.getUploadSessionUri();
        }
      });


      return nameCreated;
    }else {
      return null;
    }
  }

  private void onFocus() {
    evento_titulo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });
    evento_complemento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });
    evento_descricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });
    evento_hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });
    evento_endereco.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });
    evento_numero.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        trocarDrawable(v, hasFocus);
      }
    });

  } //Pegando Evento dos EditText, Focus

  private void trocarDrawable(View v, boolean hasFocus) {
    if(hasFocus){
      //v.setBackgroundResource(background_caixa_editing_telaeventoparticipating);
      v.setBackgroundResource(abc_edit_text_material);
    } else {
      v.setBackgroundResource(background_caixa_editing_notfocusable);
    }
  } //Trocando o drawable dos EditText

  private void recuperarEvento() { //Recuperando eventos do Firestore
    db.collection("eventos").document(id)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                 DocumentSnapshot document = task.getResult();
                 if(document.exists()){
                   Eventos eventos = new Eventos();
                   eventos = document.toObject(Eventos.class);
                   setElements(eventos);//Passando objeto eventos para selecionar na tela
                 }
               }
              }
            });
  } //recuperar o evento no firestore

  private void setElements(Eventos eventos) { //Setando os editviews com os dados que vieram do firestore

    evento_titulo.setText(eventos.getTitulo());
    evento_complemento.setText(eventos.getComplemento());
    evento_endereco.setText(eventos.getEndereco());
    evento_descricao.setText(eventos.getDescricao());
    evento_hora.setText(eventos.getHora());
    evento_numero.setText(eventos.getNumero());
    setImage(eventos);
  } //Setando dandos do Firestore nos EditText

  private void setImage(Eventos eventos) {
    StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(eventos.getImagem());
    refStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri uri) {
        Glide.with(TelaEventoParticipante.this)
                .load(uri)
                .into(imgView);
      }
    }). addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {

      }
    });
  }

  

}