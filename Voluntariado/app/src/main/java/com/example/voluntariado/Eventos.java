package com.example.voluntariado;

public class Eventos {

    private String titulo;
    private String endereco;
    private String data;
    private String hora;

    public Eventos(){}
    //Classe para definir os eventos

    //construtor
    public Eventos(String titulo, String descricao, String data, String hora) {
        this.titulo = titulo;
        this.endereco = descricao;
        this.data = data;
        this.hora = hora;
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

    public void setDescricao(String descricao) {
        this.endereco = descricao;
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
}
