package com.prenotazioni.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Aula aula;
    @ManyToOne
    private Corso corso;
    @ManyToOne
    private Utente utente;
    private LocalDateTime inizio;
    private LocalDateTime fine;

}