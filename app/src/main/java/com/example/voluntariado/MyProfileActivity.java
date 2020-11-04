package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

  String id;
  //Instanciando ferramentas do Firebase
  FirebaseAuth mAuth = FirebaseAuth.getInstance();
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  //Instanciando elementos gráficos
  TextView userName;
  TextView userIdade;
  TextView userEmail;
  Button editProfile;
  ImageView img;


  private GroupAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_profile);
    userName = findViewById(R.id.my_profile_name);
    userIdade = findViewById(R.id.my_profile_idade);
    userEmail = findViewById(R.id.my_profile_email);
    editProfile = findViewById(R.id.bt_edit_my_profile);
    img = findViewById(R.id.my_profile_img);
    img.setBackground(null);

    RecyclerView rv = findViewById(R.id.rv_myProfile);
    rv.setLayoutManager(new LinearLayoutManager(this));


    adapter = new GroupAdapter();
    rv.setAdapter(adapter);
    getUserId();
  }

  private void getUserId() {
    id = mAuth.getUid();
  }

  @Override
  protected void onResume() {
    super.onResume();
    img.setBackground(null);
    fetchListEvents();
    settingData();
  }

  private void fetchListEvents() {
    adapter.clear();
    db.collection("eventos")
            .whereEqualTo("proprietario", id)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                if(documentChanges != null){
                  for (DocumentChange doc: documentChanges) {
                    Eventos evento = doc.getDocument().toObject(Eventos.class);
                    adapter.add(new EventoItem(evento));
                  }
                }
              }
            });

  }

  private void settingData() {
    db.collection("users1")
            .document(mAuth.getUid())
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                User me = documentSnapshot.toObject(User.class);
                userName.setText(me.getNome());
                userIdade.setText("22" + " anos");
                userEmail.setText(me.getEmail());
                String image = me.getImage();
                if(getIntent().hasExtra("imagem")){
                  Bundle bundle = getIntent().getExtras();
                  if(bundle != null){
                    try{
                      byte[] imageInByte = bundle.getByteArray("imagem");
                      Bitmap bmp = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                      img.setImageBitmap(bmp);
                    }catch (Exception e){}
                  }
                } else {
                  setImage(image);
                }
              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {

              }
            });
  }

  private void setImage(String imagem) {
    if(imagem.trim().isEmpty()){

    } else {
      final ImageView userPhoto = findViewById(R.id.my_profile_img);
      StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(imagem);
      refStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
          Glide.with(MyProfileActivity.this)
                  .load(uri)
                  .into(userPhoto);
        }
      });
    }
  }

  public void toEditProfile(View view) {
    Intent intent = new Intent(MyProfileActivity.this, MyProfileEditActivity.class);
    startActivity(intent);
  }

  private class EventoItem extends Item<GroupieViewHolder> {

    private final Eventos evento;

    private EventoItem(Eventos evento){
      this.evento = evento;
    }

    @Override
    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
      TextView txtTitulo = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_titulo);
      TextView txtData = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_data);
      TextView txtHora = viewHolder.itemView.findViewById(R.id.meus_eventos_edt_hora);

      txtTitulo.setText(evento.getTitulo());
      txtData.setText(evento.getData());
      txtHora.setText(evento.getHora());

      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(MyProfileActivity.this, telaDoEvento.class);
          intent.putExtra("id", evento.getId());
          startActivity(intent);
        }
      });
    }

    @Override
    public int getLayout() {
      return R.layout.item_meus_eventos;
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

}