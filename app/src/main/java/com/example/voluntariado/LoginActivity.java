package com.example.voluntariado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {   //ACTIVITY RESPONSÁVEL PARA VERIFICAR TENTATIVAS DE LOGIN

    //instanciando firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected EditText email;
    protected EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.loginUsuario);
        senha = findViewById(R.id.loginSenha);
    }

    public void criarConta(View view) {//indo para cadastro
        Intent intent = new Intent (this, CadastroActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void validarCampo(View view) {
        String verEmail = email.getText().toString();
        String verSenha = senha.getText().toString();

        if (verEmail.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha os Campos!", Toast.LENGTH_SHORT).show();
        } else if(verSenha.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha os campos!", Toast.LENGTH_SHORT).show();
        } else {
            fazerSignIn(verEmail, verSenha); //Tudo certo, tentando autenticar direto no firebase
        }
    }

    private void fazerSignIn(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            telaPrincipal();
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void telaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
