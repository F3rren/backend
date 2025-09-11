package com.prenotazioni.service;

import com.prenotazioni.model.Aula;
import com.prenotazioni.model.Corso;
import com.prenotazioni.model.Prenotazione;
import com.prenotazioni.model.Prenotazione.StatoPrenotazione;
import com.prenotazioni.model.Utente;
import com.prenotazioni.repository.PrenotazioneRepository;
import com.prenotazioni.repository.AulaRepository;
import com.prenotazioni.repository.CorsoRepository;
import com.prenotazioni.repository.UtenteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PrenotazioneService {
    
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    
    @Autowired
    private AulaRepository aulaRepository;
    
    @Autowired
    private CorsoRepository corsoRepository;
    
    @Autowired
    private UtenteRepository utenteRepository;

    // Prenota un'aula per una lezione
    public Prenotazione prenotaAula(Long aulaId, Long corsoId, Long utenteId, 
                                   LocalDateTime inizio, LocalDateTime fine, String descrizione) {
        
        // Verifica disponibilità
        if (!isAulaDisponibile(aulaId, inizio, fine)) {
            return null; // Aula non disponibile
        }
        
        Optional<Aula> aula = aulaRepository.findById(aulaId);
        Optional<Corso> corso = corsoRepository.findById(corsoId);
        Optional<Utente> utente = utenteRepository.findById(utenteId);
        
        if (aula.isEmpty() || corso.isEmpty() || utente.isEmpty()) {
            return null;
        }
        
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setAula(aula.get());
        prenotazione.setCorso(corso.get());
        prenotazione.setUtente(utente.get());
        prenotazione.setInizio(inizio);
        prenotazione.setFine(fine);
        prenotazione.setStato(StatoPrenotazione.PRENOTATA);
        prenotazione.setDescrizione(descrizione);
        prenotazione.setDataCreazione(LocalDateTime.now());
        
        return prenotazioneRepository.save(prenotazione);
    }
    
    // Blocca un'aula (solo admin)
    public Prenotazione bloccaAula(Long aulaId, Long utenteAdminId, 
                                  LocalDateTime inizio, LocalDateTime fine, String motivo) {
        
        // Verifica disponibilità
        if (!isAulaDisponibile(aulaId, inizio, fine)) {
            return null; // Aula non disponibile
        }
        
        Optional<Aula> aula = aulaRepository.findById(aulaId);
        Optional<Utente> admin = utenteRepository.findById(utenteAdminId);
        
        if (aula.isEmpty() || admin.isEmpty() || !"admin".equals(admin.get().getRuolo())) {
            return null;
        }
        
        Prenotazione blocco = new Prenotazione();
        blocco.setAula(aula.get());
        blocco.setCorso(null); // Nessun corso per i blocchi
        blocco.setUtente(admin.get());
        blocco.setInizio(inizio);
        blocco.setFine(fine);
        blocco.setStato(StatoPrenotazione.BLOCCATA);
        blocco.setDescrizione(motivo);
        blocco.setDataCreazione(LocalDateTime.now());
        
        return prenotazioneRepository.save(blocco);
    }
    
    // Mette un'aula in manutenzione
    public Prenotazione aulaInManutenzione(Long aulaId, Long utenteAdminId, 
                                          LocalDateTime inizio, LocalDateTime fine, String dettagli) {
        
        Optional<Aula> aula = aulaRepository.findById(aulaId);
        Optional<Utente> admin = utenteRepository.findById(utenteAdminId);
        
        if (aula.isEmpty() || admin.isEmpty() || !"admin".equals(admin.get().getRuolo())) {
            return null;
        }
        
        Prenotazione manutenzione = new Prenotazione();
        manutenzione.setAula(aula.get());
        manutenzione.setCorso(null);
        manutenzione.setUtente(admin.get());
        manutenzione.setInizio(inizio);
        manutenzione.setFine(fine);
        manutenzione.setStato(StatoPrenotazione.MANUTENZIONE);
        manutenzione.setDescrizione(dettagli);
        manutenzione.setDataCreazione(LocalDateTime.now());
        
        return prenotazioneRepository.save(manutenzione);
    }
    
    // Verifica se un'aula è disponibile in un determinato periodo
    public boolean isAulaDisponibile(Long aulaId, LocalDateTime inizio, LocalDateTime fine) {
        List<Prenotazione> conflitti = prenotazioneRepository
            .findConflittingReservations(aulaId, inizio, fine);
        return conflitti.isEmpty();
    }
    
    // Ottiene tutte le prenotazioni di un'aula in una data specifica
    public List<Prenotazione> getPrenotazioniAula(Long aulaId, LocalDateTime data) {
        LocalDateTime inizioGiornata = data.toLocalDate().atStartOfDay();
        LocalDateTime fineGiornata = inizioGiornata.plusDays(1).minusSeconds(1);
        
        return prenotazioneRepository.findByAulaAndPeriod(aulaId, inizioGiornata, fineGiornata);
    }
    
    // Ottiene lo stato attuale di un'aula
    public String getStatoAula(Long aulaId, LocalDateTime momento) {
        List<Prenotazione> prenotazioniAttive = prenotazioneRepository
            .findActiveReservations(aulaId, momento);
            
        if (prenotazioniAttive.isEmpty()) {
            return "LIBERA";
        }
        
        // Priorità: MANUTENZIONE > BLOCCATA > PRENOTATA
        for (Prenotazione p : prenotazioniAttive) {
            if (p.getStato() == StatoPrenotazione.MANUTENZIONE) {
                return "MANUTENZIONE";
            }
        }
        
        for (Prenotazione p : prenotazioniAttive) {
            if (p.getStato() == StatoPrenotazione.BLOCCATA) {
                return "BLOCCATA";
            }
        }
        
        return "PRENOTATA";
    }
    
    // Annulla una prenotazione
    public boolean annullaPrenotazione(Long prenotazioneId, Long utenteId) {
        Optional<Prenotazione> prenotazione = prenotazioneRepository.findById(prenotazioneId);
        
        if (prenotazione.isEmpty()) {
            return false;
        }
        
        Prenotazione p = prenotazione.get();
        
        // Solo il creatore o un admin può annullare
        Optional<Utente> utente = utenteRepository.findById(utenteId);
        if (utente.isEmpty()) {
            return false;
        }
        
        boolean isCreatore = p.getUtente().getId().equals(utenteId);
        boolean isAdmin = "admin".equals(utente.get().getRuolo());
        
        if (!isCreatore && !isAdmin) {
            return false;
        }
        
        p.setStato(StatoPrenotazione.ANNULLATA);
        prenotazioneRepository.save(p);
        return true;
    }
    
    // Lista tutte le prenotazioni per gestione admin
    public List<Prenotazione> getAllPrenotazioni() {
        return prenotazioneRepository.findAll();
    }
    
    // Lista prenotazioni per utente
    public List<Prenotazione> getPrenotazioniUtente(Long utenteId) {
        return prenotazioneRepository.findByUtenteId(utenteId);
    }
    
    // Ottieni dettagli completi per una specifica aula
    public List<Map<String, Object>> getRoomCompleteDetails(Long aulaId) {
        return prenotazioneRepository.findCompleteDetailsByAulaId(aulaId);
    }
    
    // Ottieni dettagli completi di tutte le prenotazioni
    public List<Map<String, Object>> getAllCompleteDetails() {
        return prenotazioneRepository.findAllCompleteDetails();
    }
    
    // Ottieni una singola prenotazione per ID
    public Prenotazione getPrenotazioneById(Long id) {
        Optional<Prenotazione> prenotazione = prenotazioneRepository.findById(id);
        return prenotazione.orElse(null);
    }
    
    // Ottieni dettagli completi per una singola prenotazione
    public List<Map<String, Object>> getPrenotazioneCompleteDetails(Long prenotazioneId) {
        return prenotazioneRepository.findCompleteDetailsByPrenotazioneId(prenotazioneId);
    }
    
    // Lista prenotazioni per stato
    public List<Prenotazione> getPrenotazioniByStato(StatoPrenotazione stato) {
        return prenotazioneRepository.findByStato(stato);
    }
    
    // Lista prenotazioni future
    public List<Prenotazione> getPrenotazioniFuture() {
        return prenotazioneRepository.findPrenotazioniFuture(LocalDateTime.now());
    }
    
    // Metodo admin per annullare qualsiasi prenotazione
    public boolean annullaPrenotazioneAsAdmin(Long prenotazioneId, Long adminId, String motivo) {
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(prenotazioneId);
        if (prenotazioneOpt.isEmpty()) {
            return false;
        }
        
        Prenotazione prenotazione = prenotazioneOpt.get();
        
        // Verifica che l'admin esista
        Optional<Utente> admin = utenteRepository.findById(adminId);
        if (admin.isEmpty() || !"admin".equals(admin.get().getRuolo())) {
            return false;
        }
        
        // Gli admin possono eliminare qualsiasi prenotazione, indipendentemente dallo stato
        prenotazione.setStato(StatoPrenotazione.ANNULLATA);
        
        // Aggiorna la descrizione per indicare l'azione admin
        String descrizioneOriginale = prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "";
        String nuovaDescrizione = descrizioneOriginale + 
            (descrizioneOriginale.isEmpty() ? "" : " | ") +
            "ANNULLATA DALL'AMMINISTRATORE: " + motivo;
        prenotazione.setDescrizione(nuovaDescrizione);
        
        prenotazioneRepository.save(prenotazione);
        return true;
    }
}
