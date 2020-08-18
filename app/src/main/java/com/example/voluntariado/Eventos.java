package com.example.voluntariado;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Eventos {

    private String titulo;
    private String endereco;
    private String data;
    private String hora;
    private String descricao;
    private String id;
    private String imagem;
    private String proprietario;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Eventos(){}
    //Classe para definir os eventos

    //construtor
    public Eventos(String titulo, String endereco, String data, String hora, String descricao, String id, String imagem, String proprietario) {
        this.titulo = titulo;
        this.endereco = endereco;
        this.data = data;
        this.hora = hora;
        this.descricao = descricao;
        this.id = id;
        this.imagem = imagem;
        this.proprietario = proprietario;
    }


    //getters and setters :


    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDescricao(){ return descricao;}

    public void setDescricao(String desc) { this.descricao = desc;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
