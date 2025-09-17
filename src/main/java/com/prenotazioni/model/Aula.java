package com.prenotazioni.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private int capienza;
    private int piano;
    
    @Column(name = "is_virtual", nullable = false)
    @JsonProperty("isVirtual")
    private boolean isVirtual = false;
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCapienza() { return capienza; }
    public void setCapienza(int capienza) { this.capienza = capienza; }
    public int getPiano() { return piano; }
    public void setPiano(int piano) { this.piano = piano; }
    public boolean isVirtual() { return isVirtual; }
    public void setVirtual(boolean isVirtual) { this.isVirtual = isVirtual; }
}
