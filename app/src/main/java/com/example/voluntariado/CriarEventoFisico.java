package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CriarEventoFisico extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestoreEvento = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef = storage.getReference().child("images");

    private EditText txtTitulo;
    private EditText txtEndereco;
    private EditText txtComplemento;
    private EditText txtNumero;
    private EditText txtData;
    private EditText txtHora;
    private EditText txtDesc;
    Bitmap bitmap;
    Uri downloadUri;
    private int PICK_IMAGE = 1234;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento_fisico);
        txtTitulo = findViewById(R.id.editTitulo);
        txtEndereco = findViewById(R.id.editEndereco);
        txtComplemento = findViewById(R.id.editComplemento);
        txtNumero = findViewById(R.id.editNumero);
        txtData = findViewById(R.id.editData);
        txtHora = findViewById(R.id.editHora);
        txtDesc = findViewById(R.id.editDesc);
        txtData.addTextChangedListener(MaskEditUtil.mask(txtData, MaskEditUtil.FORMAT_DATE));
        txtHora.addTextChangedListener(MaskEditUtil.mask(txtHora, MaskEditUtil.FORMAT_HOUR));
    }

    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }

    }

    public void criarEvento(View view){
        String titulo = txtTitulo.getText().toString();
        String endereco = txtEndereco.getText().toString();
        String complemento = txtComplemento.getText().toString();
        String numero = txtNumero.getText().toString();
        String data = txtData.getText().toString();
        String descricao = txtDesc.getText().toString();
        String hora = txtHora.getText().toString();

        if (titulo.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        } else if (endereco.trim().isEmpty()) {
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        } else if (numero.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        } else if (data.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        } else if(descricao.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        } else if(complemento.trim().isEmpty()){
            complemento = " ";
        } else if (hora.trim().isEmpty()){
            hora = " ";
        } else {
            registrarEvento(titulo, endereco, numero, data, descricao, complemento, hora);
        }

    }

    public void registrarEvento(String titulo, String endereco, String numero, final String data, String descricao, String complemento, String hora){
        salvarFoto();
        final Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("titulo", titulo);
        dataToSave.put("endereco", endereco);
        dataToSave.put("complemento", complemento);
        dataToSave.put("numero", numero);
        dataToSave.put("data", data);
        dataToSave.put("descricao", descricao);
        dataToSave.put("hora", hora);
        dataToSave.put("proprietario", mAuth.getUid());
        if (bitmap != null){
            dataToSave.put("imagem", bitmap.toString());
        }

        firestoreEvento.collection("eventos")
                .add(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dataToSave.put("id", documentReference.getId());
                        firestoreEvento.collection("eventos").document(documentReference.getId()).set(dataToSave)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        telaLista();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void telaLista(){
        Intent intent = new Intent(CriarEventoFisico.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void uploadFoto(View view){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(gallery, "Selecione"), PICK_IMAGE );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Toast.makeText(CriarEventoFisico.this, imageUri.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    protected void salvarFoto(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dados = baos.toByteArray();

        final UploadTask uploadTask = imagesRef.child(bitmap.toString()).putBytes(dados);
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
    }
}
