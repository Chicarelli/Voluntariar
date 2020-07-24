package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//ACTIVITY DE CADASTRO DE USUÁRIO.

@RequiresApi(api = Build.VERSION_CODES.O)
public class CadastroActivity extends AppCompatActivity {

    //Instancias no firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //pegando views do layout
    EditText editEmail;
    EditText editSenha;
    EditText editNome;
    EditText editNasc;


    @Override
    //nomeando as views
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

    //METODO RESPONSÁVEL POR FAZER A VERIFICAÇÃO DOS DADOS ANTES DE SALVAR / FAZER A AUTENTICAÇÃO DO USUÁRIO NO FIREBASE
    public void criarUsuario(View view){

        //Recuperar TEXTOS dos EditViews e transformando em String
        String email = editEmail.getText().toString();
        String password = editSenha.getText().toString();
        String nome = editNome.getText().toString();
        String dataNascimento = editNasc.getText().toString();


        //VERIFICANDO SE O CONTEÚDO DAS VIEWS NÃO ESTÃO VAZIOS
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
        }

        //Tudo certo, enviado pro método que vai finalmente salvar/autenticar o usuário
        fazerAutenticacao(email, password, nome, dataNascimento);
    }

    //METODO RESPONSÁVEL POR SALVAR O USUÁRIO NO FIREBASE E FAZER A AUTENTICAÇÃO
    public void fazerAutenticacao(final String email, String password, final String nome, final String dataNascimento){ //CRiando o Usuario no autentication e depois mandando salvar no firestore.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ //SE OCORROU TUDO CERTO
                            Log.d("FIREBASE", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();  //VERIFICANDO USUÁRIO ATUAL (NAO SEI SE TEM FINALIDADE AINDA)

                            //Chamando Metodo firestore para salvar os outros dados  //MANDANDO OS DADOS DO USUÁRIO PARA MÉTODO DE SALVAR O RESTANTE DOS DADOS DELE NO FIRESTORE
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

    //METODO RESPONSÁVEL POR SALVAR O RESTANTE DOS DADOS DO USUÁRIO CRIADO NO FIRESTORE
    public void salvarfirestore(String email, String nome, String dataNascimento) throws ParseException { //
        String uuid = mAuth.getUid(); //PEGANDO O ID GERADO PELO AUTENTICATION PARA O USUÁRIO

        Date dataDeNascimento = mudandoData(dataNascimento); //Alterando o Date para formato Date!

        //Adicionando a um MAP para organizar pra por no collection
        Map<String, Object>dataToSave = new HashMap<String, Object>();
        dataToSave.put("email", email);
        dataToSave.put("nome", nome);
        dataToSave.put("DataNasc", dataDeNascimento);
        dataToSave.put("sexo", "M");

        //Colocando no Collection Firestor. Coleção: Users1, com o mesmo nome do uuid dele criado pelo firebase autentication
        firestore.collection("users1").document(uuid)
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

    //MÉTODO QUE LEVARÁ O USUÁRIO PARA A TELA DE LISTA DE EVENTOS, APÓS FEITO O CADASTRO/AUTENTICAÇÃO DO MESMO
    public void telaPrincipal(){
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

    //METODO PARA RETORNAR O STRING DATA EM UM DATE DATA.
    public Date mudandoData(String dataNasc) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date dataFormatada = formato.parse(dataNasc);
        return dataFormatada;
    }
}
