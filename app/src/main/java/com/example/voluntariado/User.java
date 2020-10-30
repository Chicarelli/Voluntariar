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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.util.Date;
import java.util.concurrent.Executor;

public class User {
   //CLASSE QUE SERÁ FUTURAMENTE UTILIZADA PARA SALVAR RESTANTE DOS DADOS DO USUÁRIO NO FIREBASE FIERSTORE.
    private String uuid;
    private String nome;
    private String email;
    private String sexo;
    //private Date DataNasc;
    //private String idade;
  private String image;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public User() {    }

    public User (String uuid, String nome, String email, String sexo, String image, Date DataNasc){
      this.uuid = uuid;
      this.nome = nome;
      this.email = email;
      this.sexo = sexo;
      this.image = image;
      //this.DataNasc = DataNasc;
    }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSexo() {
    return sexo;
  }

  public void setSexo(String sexo) {
    this.sexo = sexo;
  }


}
