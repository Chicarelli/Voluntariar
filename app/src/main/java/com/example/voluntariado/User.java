package com.example.voluntariado;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Console;
import java.util.concurrent.Executor;

public class User {
   //CLASSE QUE SERÁ FUTURAMENTE UTILIZADA PARA SALVAR RESTANTE DOS DADOS DO USUÁRIO NO FIREBASE FIERSTORE.
    private String uuid;
    private String nome;
    private String email;
    private String password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public User() { }

    public String getUuid() {
        return uuid;
    }

    public String getNome() {
        return nome;
    }


}
