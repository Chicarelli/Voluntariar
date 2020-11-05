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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import java.util.List;
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

  String id;
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
    getUuid();
    searchingFirestoreForUser();
  }

  private void getUuid() {
    id = mAuth.getUid();
  }

  private void searchingFirestoreForUser() {
    db.collection("users1")
            .document(id)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
              @Override
              public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               User user = documentSnapshot.toObject(User.class);
               settingDataOnScreen(user);
              }
            });
  }

  private void settingDataOnScreen(User user) {
    nome.setText(user.getNome());
    nascimento.setText(user.getDataNasc());
    email.setText(user.getEmail());
    StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(id);
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
        imageview.setImageBitmap(bitmap);
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  public void buttonToSavePressed(View view) {
    Map<String, Object> dataToEdit = new HashMap<>();
    if (bitmap != null){
      savingImageAndCreatingUri();
      dataToEdit.put("image", id);
    }
    dataToEdit.put("nome", nome.getText().toString());
    dataToEdit.put("email", email.getText().toString());
    dataToEdit.put("dataNasc", nascimento.getText().toString());
    editOnFirestore(dataToEdit);
  }

  private void editOnFirestore(Map<String, Object> dataToEdit) {
    db.collection("users1")
            .document(id)
            .update(dataToEdit)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                Intent intent = new Intent(MyProfileEditActivity.this, MyProfileActivity.class);
                if(bitmap != null){
                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                  byte[] imageInByte = stream.toByteArray();
                  intent.putExtra("imagem", imageInByte);
                }
                startActivity(intent);
                finish();
              }
            });
  }

  private void savingImageAndCreatingUri() {
    if(bitmap != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      byte[] dados = baos.toByteArray();

      final UploadTask uploadTask = imagesRef.child(id).putBytes(dados);
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
  }
}