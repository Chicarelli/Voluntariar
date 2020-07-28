package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {   //ACTIVITY RESPONSÁVEL PARA VERIFICAR TENTATIVAS DE LOGIN

    //instanciando firebase
    private FirebaseAuth mAuth;

    //nomeando o conteúdo da activity
    protected EditText email;
    protected EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.loginUsuario);
        senha = findViewById(R.id.loginSenha);
        getSupportActionBar().hide();
    }

    //Pegando instancia Firebase Autentication ao startar
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    //Indo para a tela de cadastro, após clicar no botão para de Cadastrar
    public void criarConta(View view) {
        Intent intent = new Intent (this, CadastroActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    //Usuário tentando logar, Recuperando conteúdo das activitys e fazendo a validação, para então confirmar usuário existente no firebase
    public void validarCampo(View view) {
        //Recuperar o conteúdo das editsviews presentes na activity
        String verEmail = email.getText().toString();
        String verSenha = senha.getText().toString();

        //Validar Campos
        if (verEmail.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha os Campos!", Toast.LENGTH_SHORT).show();
        } else if(verSenha.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha os campos!", Toast.LENGTH_SHORT).show();

        } else {
            fazerSignIn(verEmail, verSenha);
            //Tudo certo, tentando autenticar direto no firebase

        }
    }

    private void fazerSignIn(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            telaPrincipal();
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Metodo que levará o usuário à tela de listagem de eventos após autenticação bem-sucedida.
    public void telaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
