package com.example.voluntariado;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CriarEventoFisicoUtil {
  private String uuid;
  private String titulo;
  private String createdName;

  public CriarEventoFisicoUtil(String uuid, String titulo) {
    this.uuid = uuid;
    this.titulo = titulo;
    this.createdName = uuid + titulo;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getCreatedName() {
    return createdName;
  }

  public void setCreatedName(String createdName) {
    this.createdName = createdName;
  }
}
