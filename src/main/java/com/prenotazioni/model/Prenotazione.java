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
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;
    
    @ManyToOne
    @JoinColumn(name = "corso_id", nullable = true) // Può essere null per blocchi/manutenzioni
    private Corso corso;
    
    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;
    
    @Column(nullable = false)
    private LocalDateTime inizio;
    
    @Column(nullable = false)
    private LocalDateTime fine;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPrenotazione stato;
    
    @Column(length = 500)
    private String descrizione; // Motivo del blocco, note, etc.
    
    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    // Enum per gli stati
    public enum StatoPrenotazione {
        PRENOTATA,      // Aula prenotata per una lezione
        BLOCCATA,       // Aula bloccata dall'admin
        MANUTENZIONE,   // Aula in manutenzione
        ANNULLATA       // Prenotazione annullata
    }
}