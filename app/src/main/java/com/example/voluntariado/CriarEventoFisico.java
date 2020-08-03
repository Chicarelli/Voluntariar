package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CriarEventoFisico extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestoreEvento = FirebaseFirestore.getInstance();

    private EditText txtTitulo;
    private EditText txtEndereco;
    private EditText txtComplemento;
    private EditText txtNumero;
    private EditText txtData;
    private EditText txtHora;
    private EditText txtDesc;



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

    public void registrarEvento(String titulo, String endereco, String numero, String data, String descricao, String complemento, String hora){
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("titulo", titulo);
        dataToSave.put("endereco", endereco);
        dataToSave.put("complemento", complemento);
        dataToSave.put("numero", numero);
        dataToSave.put("data", data);
        dataToSave.put("descricao", descricao);
        dataToSave.put("hora", hora);
        dataToSave.put("proprietario", mAuth.getUid());

        firestoreEvento.collection("eventos")
                .add(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    telaLista();
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
}
