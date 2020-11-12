package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

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
    String imagem;
    ImageView imgView;
    Boolean userIsParticipante;

    DrawerLayout drawerLayout;
    ImageView optionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_do_evento);

        drawerLayout = findViewById(R.id.drawer_layout);
        optionButton = findViewById(R.id.more_options_imgv);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            compararUsuario(id);
        }
        registerForContextMenu(optionButton);

    }

    public void moreOptions(View view){
      PopupMenu popup = new PopupMenu(telaDoEvento.this, view);

      if(userIsParticipante){
        popup.getMenuInflater().inflate(R.menu.participantemenu, popup.getMenu());
      } else {
      popup.getMenuInflater().inflate(R.menu.excluirmenu, popup.getMenu());
      }

      popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
          switch (item.getItemId()){
            case R.id.participantes: participantes();
            return true;

            case R.id.excluirEvento: excluirEvento();
            return true;
            
            case R.id.solicitacoes: solicitacoesDeParticipantes();
            return true;

            case R.id.verProprietario: pegarIdProprietario();
            return true;

            default: return true;
          }
        }
      });

      popup.show();
    }

  private void contactarProprietario(String uidProprietario) {
      Intent intent = new Intent(telaDoEvento.this, PerfilMembros.class);
      intent.putExtra("id", uidProprietario);
      startActivity(intent);
  }

  private void pegarIdProprietario() {
      db.collection("eventos")
              .document(id)
              .get()
              .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                  contactarProprietario(documentSnapshot.get("proprietario").toString());
                }
              });
  }

  private void solicitacoesDeParticipantes() {
        Intent intent = new Intent(this, RequestParticipation.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void ClickMenu(View view){
      openDrawer(drawerLayout);
    }

    public void messagesScreen(View view){
    MainActivity.redirectActivity(this, MessageActivity.class);
  }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void meuPerfil(View view){
        MainActivity.redirectActivity(this, MyProfileActivity.class);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
      MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void myEvents(View view){
        MainActivity.redirectActivity(this, MeusEventos.class);
    }

    public void Logout(View view){
      AlertDialog.Builder builder = new AlertDialog.Builder(telaDoEvento.this);
      builder.setTitle("Logout");

      builder.setMessage("Tem certeza que deseja sair?");

      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          finishAffinity();
          System.exit(0);
        }
      });

      builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });

      builder.show();
  }

  @Override
    protected void onPause() {
      super.onPause();

      closeDrawer(drawerLayout);
    }

    private void settingElementsOnScreen(String id){
          DocumentReference docRef = db.collection("eventos").document(id);
          docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                      DocumentSnapshot document = task.getResult();
                      if (document.exists()) {
                          Eventos evento;
                          evento = document.toObject(Eventos.class);
                          setarElementos(evento);
                          if(getIntent().hasExtra("imagem")){
                            Bundle bundle = getIntent().getExtras();
                            if(bundle != null){
                              try{
                                byte[] imageInByte = bundle.getByteArray("imagem");
                                Bitmap bmp = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                                imgView.setImageBitmap(bmp);
                              }catch(Exception e){}
                            }
                          }
                      } else {
                          Log.d("EXISTE?", "Não");
                      }
                  } else {
                      Log.d("OPS", "Falhou", task.getException());
                  }
              }
          });
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
        imagem = evento.getImagem();
        setImage(imagem);

      }

    private void setImage(String imagem) {
      StorageReference refStorage = storage.getReferenceFromUrl("gs://voluntariar-50f20.appspot.com/images").child(imagem);
      imgView = findViewById(R.id.imagemEvento);
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
                                   botaoPouE.setText("Editar");
                                   userIsParticipante = false;
                               } else {
                                   testUserParticipation();
                                   userIsParticipante = true;
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
                            botaoPouE.setText("Solicitar Participação");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        botaoPouE.setText("Solicitar Participação");
                    }
                });
   }

    public void botaoTelaEvento(View view){
        botaoPouE = findViewById(R.id.botaoParticiparEditar);
        if (botaoPouE.getText().equals("Solicitar Participação")){
            participatingMembers();

        } else if(botaoPouE.getText().equals("Editar")){
            Intent intent = new Intent(telaDoEvento.this, TelaEventoParticipante.class);
            intent.putExtra("id", this.id);
            startActivity(intent);
        }
        else if (botaoPouE.getText().equals("Deixar de Participar")) {
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

            case R.id.participantes: participantes();
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void participantes(){
        Intent intent = new Intent(telaDoEvento.this, AprovedMembers.class);
        intent.putExtra("idevento", id);
        startActivity(intent);
    }

    private void deletingParticipation() {
        String currentUser = mAuth.getUid();
        botaoPouE = findViewById(R.id.botaoParticiparEditar);

        db.collection("participating").document(currentUser+id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(telaDoEvento.this, "Deixou de participar", LENGTH_SHORT).show();
                        botaoPouE.setText("Solicitar Participação");
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
                        //Toast.makeText(telaDoEvento.this, "Participando", LENGTH_SHORT).show();
                        botaoPouE.setText("Deixar de Participar");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(telaDoEvento.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

   @Override
    protected void onResume() {
    super.onResume();
    settingElementsOnScreen(id);
    }

}



