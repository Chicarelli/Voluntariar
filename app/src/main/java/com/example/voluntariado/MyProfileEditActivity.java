package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyProfileEditActivity extends AppCompatActivity {

  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  StorageReference imagesRef = storage.getReference().child("images");
  EditText nome;
  EditText nascimento;
  EditText email;
  ImageView imageview;

  Bitmap bitmap;
  Uri downloadUri;
  private int PICK_IMAGE = 1234;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_profile_edit);

    imageview = findViewById(R.id.edit_profile_img);
    nome = findViewById(R.id.edt_profile_nome);
    nascimento = findViewById(R.id.edt_profile_data);
    email = findViewById(R.id.edt_profile_email);

    nascimento.addTextChangedListener(MaskEditUtil.mask(nascimento, MaskEditUtil.FORMAT_DATE));


  }

  private void settingData() {
    
    db.collection("users1")
            .document(mAuth.getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                Timestamp timestamp = (Timestamp) documentSnapshot.get("DataNasc");
                Date dataDeNascimento = timestamp.toDate();

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                nome.setText(documentSnapshot.get("nome").toString());
                nascimento.setText(df.format(dataDeNascimento));
                email.setText(documentSnapshot.get("email").toString());
                String image = documentSnapshot.get("image").toString();

                if(!image.equals("")){
                  setImage(image);
                }
              }
            });
  }

  private void setImage(String image) {
    StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(image);
    refStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri uri) {
        Glide.with(MyProfileEditActivity.this)
                .load(uri)
                .into(imageview);
      }
    });

  }

  public void changePhoto(View view) {
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
          Glide.with(MyProfileEditActivity.this)
                  .load(bitmap)
                  .into(imageview);
        }
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  public void save(View view) {
    String profile_name = nome.getText().toString();
    String profile_dataNascimento = nascimento.getText().toString();
    String profile_email = email.getText().toString();
    String imagem = salvarFoto(profile_name);

    Map<String, Object> usuario = new HashMap<>();
    usuario.put("nome", profile_name);
    usuario.put("email", profile_email);
    usuario.put("dataNasc", profile_dataNascimento);
    if(imagem != null){
      usuario.put("image", imagem);
    }

    toEdit(usuario);
  }

  private void toEdit(Map<String, Object> usuario) {
    db.collection("users1").document(mAuth.getUid())
            .set(usuario, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Intent intent = new Intent(MyProfileEditActivity.this, MyProfileActivity.class);
                if (bitmap != null) {
                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                  byte[] imageInByte = stream.toByteArray();
                  intent.putExtra("imagem", imageInByte);
                }
                startActivity(intent);
                finish();
              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Toast.makeText(MyProfileEditActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private String salvarFoto(String profile_name) {
    String namecreated = makeUrl(profile_name);

    if (bitmap != null){
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] dados = baos.toByteArray();

    final UploadTask uploadTask = imagesRef.child(namecreated).putBytes(dados);
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {

      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        downloadUri = taskSnapshot.getUploadSessionUri();
      }
    });
  }
      return namecreated;
    }

  private String makeUrl(String profile_name) {
    if(bitmap != null){
      User user = new User();
      user.setUuid(mAuth.getUid());
      user.setNome(profile_name);
      String namecreated = user.getUuid() + user.getNome();
      return namecreated;
  } else {
      return null;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (bitmap != null) {
      bitmap = null;
    }
    settingData();
  }
}