package com.example.voluntariado;

public class Eventos {

    private String titulo;
    private String endereco;
    private String data;
    private String hora;
    private String descricao;
    private String id;

    public Eventos(){}
    //Classe para definir os eventos

    //construtor
    public Eventos(String titulo, String endereco, String data, String hora, String descricao, String id) {
        this.titulo = titulo;
        this.endereco = endereco;
        this.data = data;
        this.hora = hora;
        this.descricao = descricao;
        this.id = id;
    }


    //getters and setters :

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
