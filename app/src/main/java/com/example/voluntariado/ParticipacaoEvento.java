package com.example.voluntariado;

public class ParticipacaoEvento {

  private String eventoID;
  private String idParticipatingMember;

  public ParticipacaoEvento() { }

  public ParticipacaoEvento(String eventoID, String idParticipatingMember) {
    this.eventoID = eventoID;
    this.idParticipatingMember = idParticipatingMember;
  }

  public String getEventoID() {
    return eventoID;
  }

  public void setEventoID(String eventoID) {
    this.eventoID = eventoID;
  }

  public String getIdParticipatingMember() {
    return idParticipatingMember;
  }

  public void setIdParticipatingMember(String idParticipatingMember) {
    this.idParticipatingMember = idParticipatingMember;
  }
}
