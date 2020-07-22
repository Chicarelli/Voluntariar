package com.example.voluntariado;

public class User {
   //CLASSE QUE SERÁ FUTURAMENTE UTILIZADA PARA SALVAR RESTANTE DOS DADOS DO USUÁRIO NO FIREBASE FIERSTORE.
    private final String uuid;
    private final String nome;

    public User(String uuid, String nome) {
        this.uuid = uuid;
        this.nome = nome;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNome() {
        return nome;
    }


}
