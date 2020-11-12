package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//ACTIVITY DE CADASTRO DE USUÁRIO.

@RequiresApi(api = Build.VERSION_CODES.O)
public class CadastroActivity extends AppCompatActivity {

    //Instancias no firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    EditText editEmail;
    EditText editSenha;
    EditText editNome;
    EditText editNasc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editEmail = findViewById(R.id.edtEmail);
        editSenha = findViewById(R.id.edtSenha);
        mAuth = FirebaseAuth.getInstance();
        editNome = findViewById(R.id.editNomeC);
        editNasc = findViewById(R.id.editNasc);
        editNasc.addTextChangedListener(MaskEditUtil.mask(editNasc, MaskEditUtil.FORMAT_DATE));
    }

    public void criarUsuario(View view){
        String email = editEmail.getText().toString();
        String password = editSenha.getText().toString();
        String nome = editNome.getText().toString();
        String dataNascimento = editNasc.getText().toString();

        if(VerificarTextos(email, password, nome, dataNascimento))
        fazerAutenticacao(email, password, nome, dataNascimento);
    }

    private boolean VerificarTextos(String email, String password, String nome, String dataNascimento) {
        if (email.trim().isEmpty())  {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
        } else if(password.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
        } else if(nome.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_LONG).show();
        } else if(dataNascimento.trim().isEmpty()){
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_LONG).show();
        } else if (dataNascimento.length() > 10 || dataNascimento.length() < 10){
            Toast.makeText(this, "Data nascimento inválida!", Toast.LENGTH_SHORT).show();
        } else if (!isValid(dataNascimento)){ //enviando data para o método que irá verificar se é uma data verdadeira, retornando true se sim e false se não
            Toast.makeText(this, "Data nascimento inválida!", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    //salvando e fazendo autenticação
    public void fazerAutenticacao(final String email, String password, final String nome, final String dataNascimento){ //CRiando o Usuario no autentication e depois mandando salvar no firestore.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ //SE OCORROU TUDO CERTO
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            //Salvando no firestore
                            try {
                                salvarfirestore(email, nome, dataNascimento);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CadastroActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    public void salvarfirestore(String email, String nome, String dataNascimento) throws ParseException { //

        Map<String, Object>dataToSave = new HashMap<String, Object>();
        dataToSave.put("email", email);
        dataToSave.put("nome", nome);
        dataToSave.put("dataNasc", dataNascimento);
        dataToSave.put("sexo", "M");
        dataToSave.put("uuid", mAuth.getUid());
        dataToSave.put("image", "");

        savingOnFirestore(dataToSave);
    }

    private void savingOnFirestore(Map<String, Object> dataToSave) {
        firestore.collection("users1").document(mAuth.getUid())
                .set(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "deu certo");
                        telaPrincipal();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "deu ruim");
                    }
                });
    }

    public void telaPrincipal(){ //tela principal para usuários autenticados
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    //METODO PARA VERIFICAR SE A DATA DE NASCIMENTO INSERIDA É UMA DATA VÁLIDA
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isValid(String strDate) {
        String dateFormat = "dd/MM/uuuu";

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern(dateFormat)
                .withResolverStyle(ResolverStyle.STRICT);
        try{
            LocalDate date = LocalDate.parse(strDate, dateTimeFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
