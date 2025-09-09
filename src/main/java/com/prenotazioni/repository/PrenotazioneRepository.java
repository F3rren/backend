package com.prenotazioni.repository;

import com.prenotazioni.model.Prenotazione;
import com.prenotazioni.model.Prenotazione.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    
    // Trova prenotazioni che si sovrappongono con un periodo dato
    @Query("SELECT p FROM Prenotazione p WHERE p.aula.id = :aulaId " +
           "AND p.stato != 'ANNULLATA' " +
           "AND ((p.inizio <= :inizio AND p.fine > :inizio) " +
           "OR (p.inizio < :fine AND p.fine >= :fine) " +
           "OR (p.inizio >= :inizio AND p.fine <= :fine))")
    List<Prenotazione> findConflittingReservations(@Param("aulaId") Long aulaId, 
                                                   @Param("inizio") LocalDateTime inizio, 
                                                   @Param("fine") LocalDateTime fine);
    
    // Trova prenotazioni di un'aula in un periodo specifico
    @Query("SELECT p FROM Prenotazione p WHERE p.aula.id = :aulaId " +
           "AND p.stato != 'ANNULLATA' " +
           "AND p.inizio <= :fine AND p.fine >= :inizio " +
           "ORDER BY p.inizio ASC")
    List<Prenotazione> findByAulaAndPeriod(@Param("aulaId") Long aulaId,
                                          @Param("inizio") LocalDateTime inizio,
                                          @Param("fine") LocalDateTime fine);
    
    // Trova prenotazioni attive in un momento specifico
    @Query("SELECT p FROM Prenotazione p WHERE p.aula.id = :aulaId " +
           "AND p.stato != 'ANNULLATA' " +
           "AND p.inizio <= :momento AND p.fine > :momento " +
           "ORDER BY p.stato DESC") // MANUTENZIONE, BLOCCATA, PRENOTATA
    List<Prenotazione> findActiveReservations(@Param("aulaId") Long aulaId,
                                             @Param("momento") LocalDateTime momento);
    
    // Trova prenotazioni per utente
    @Query("SELECT p FROM Prenotazione p WHERE p.utente.id = :utenteId " +
           "ORDER BY p.inizio DESC")
    List<Prenotazione> findByUtenteId(@Param("utenteId") Long utenteId);
    
    // Trova aule libere in un periodo
    @Query("SELECT a.id FROM Aula a WHERE a.id NOT IN " +
           "(SELECT DISTINCT p.aula.id FROM Prenotazione p WHERE p.stato != 'ANNULLATA' " +
           "AND ((p.inizio <= :inizio AND p.fine > :inizio) " +
           "OR (p.inizio < :fine AND p.fine >= :fine) " +
           "OR (p.inizio >= :inizio AND p.fine <= :fine)))")
    List<Long> findAuleLibere(@Param("inizio") LocalDateTime inizio, 
                              @Param("fine") LocalDateTime fine);
    
    // Trova prenotazioni per stato
    List<Prenotazione> findByStato(StatoPrenotazione stato);
    
    // Trova prenotazioni future
    @Query("SELECT p FROM Prenotazione p WHERE p.inizio > :ora AND p.stato != 'ANNULLATA' " +
           "ORDER BY p.inizio ASC")
    List<Prenotazione> findPrenotazioniFuture(@Param("ora") LocalDateTime ora);
}
