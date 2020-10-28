package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileActivity extends AppCompatActivity {

  String id;
  //Instanciando ferramentas do Firebase
  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  //Instanciando elementos gráficos
  TextView userName;
  TextView userIdade;
  TextView userEmail;
  ImageView UserPhotoProfile;
  Button editProfile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_profile);
    userName = findViewById(R.id.my_profile_name);
    userIdade = findViewById(R.id.my_profile_idade);
    userEmail = findViewById(R.id.my_profile_email);

    getUserId();
  }

  private void getUserId() {
    id = mAuth.getUid();
  }

  @Override
  protected void onResume() {
    super.onResume();
    settingData();
  }

  private void settingData() {
    db.collection("users1")
            .document(id)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                User me = documentSnapshot.toObject(User.class);
                userName.setText(me.getNome());
                userIdade.setText("22" + " anos");
                userEmail.setText(me.getEmail());
              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {

              }
            });
  }
}