package com.prenotazioni.dto;

public class AulaRequest {
    private String nome;
    private int capienza;
    private int piano;

    // Costruttori
    public AulaRequest() {}

    public AulaRequest(String nome, int capienza, int piano) {
        this.nome = nome;
        this.capienza = capienza;
        this.piano = piano;
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public int getPiano() {
        return piano;
    }

    public void setPiano(int piano) {
        this.piano = piano;
    }
}
