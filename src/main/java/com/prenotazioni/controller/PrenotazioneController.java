package com.prenotazioni.controller;

import com.prenotazioni.model.Prenotazione;
import com.prenotazioni.service.PrenotazioneService;
import com.prenotazioni.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {
    
    @Autowired
    private PrenotazioneService prenotazioneService;
    
    @Autowired
    private JwtService jwtService;
    
    // DTO per le richieste di prenotazione
    public static class PrenotazioneRequest {
        private Long aulaId;
        private Long corsoId;
        private String inizio; // formato: "2024-12-25T14:30:00"
        private String fine;
        private String descrizione;
        
        // Getters e Setters
        public Long getAulaId() { return aulaId; }
        public void setAulaId(Long aulaId) { this.aulaId = aulaId; }
        public Long getCorsoId() { return corsoId; }
        public void setCorsoId(Long corsoId) { this.corsoId = corsoId; }
        public String getInizio() { return inizio; }
        public void setInizio(String inizio) { this.inizio = inizio; }
        public String getFine() { return fine; }
        public void setFine(String fine) { this.fine = fine; }
        public String getDescrizione() { return descrizione; }
        public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    }
    
    // Metodo privato per verificare autenticazione
    private ResponseEntity<?> checkAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Token di autorizzazione mancante"),
                HttpStatus.UNAUTHORIZED
            );
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Token non valido"),
                HttpStatus.UNAUTHORIZED
            );
        }
        return null; // Auth OK
    }
    
    // Prenota un'aula
    @PostMapping("/prenota")
    public ResponseEntity<?> prenotaAula(@RequestBody PrenotazioneRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) return authCheck;
        
        try {
            String token = authHeader.substring(7);
            Long utenteId = jwtService.getUserIdFromToken(token);
            
            LocalDateTime inizio = LocalDateTime.parse(request.getInizio());
            LocalDateTime fine = LocalDateTime.parse(request.getFine());
            
            Prenotazione prenotazione = prenotazioneService.prenotaAula(
                request.getAulaId(), request.getCorsoId(), utenteId, 
                inizio, fine, request.getDescrizione()
            );
            
            if (prenotazione == null) {
                return new ResponseEntity<>(
                    Collections.singletonMap("error", "Impossibile prenotare: aula non disponibile o dati non validi"),
                    HttpStatus.CONFLICT
                );
            }
            
            return new ResponseEntity<>(
                Map.of("message", "Prenotazione effettuata con successo", "prenotazione", prenotazione),
                HttpStatus.CREATED
            );
            
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Formato data non valido. Usa: YYYY-MM-DDTHH:MM:SS"),
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Errore interno del server"),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    // Blocca un'aula (solo admin)
    @PostMapping("/blocca")
    public ResponseEntity<?> bloccaAula(@RequestBody PrenotazioneRequest request,
                                       @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) return authCheck;
        
        String token = authHeader.substring(7);
        String ruolo = jwtService.getRuoloFromToken(token);
        if (!"admin".equals(ruolo)) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Solo gli amministratori possono bloccare le aule"),
                HttpStatus.FORBIDDEN
            );
        }
        
        try {
            Long utenteId = jwtService.getUserIdFromToken(token);
            LocalDateTime inizio = LocalDateTime.parse(request.getInizio());
            LocalDateTime fine = LocalDateTime.parse(request.getFine());
            
            Prenotazione blocco = prenotazioneService.bloccaAula(
                request.getAulaId(), utenteId, inizio, fine, request.getDescrizione()
            );
            
            if (blocco == null) {
                return new ResponseEntity<>(
                    Collections.singletonMap("error", "Impossibile bloccare l'aula"),
                    HttpStatus.CONFLICT
                );
            }
            
            return new ResponseEntity<>(
                Map.of("message", "Aula bloccata con successo", "blocco", blocco),
                HttpStatus.CREATED
            );
            
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Formato data non valido"),
                HttpStatus.BAD_REQUEST
            );
        }
    }
    
    // Verifica disponibilità aula
    @GetMapping("/disponibilita")
    public ResponseEntity<?> verificaDisponibilita(@RequestParam Long aulaId,
                                                   @RequestParam String inizio,
                                                   @RequestParam String fine) {
        try {
            LocalDateTime inizioDateTime = LocalDateTime.parse(inizio);
            LocalDateTime fineDateTime = LocalDateTime.parse(fine);
            
            boolean disponibile = prenotazioneService.isAulaDisponibile(aulaId, inizioDateTime, fineDateTime);
            
            return new ResponseEntity<>(
                Map.of("aulaId", aulaId, "disponibile", disponibile, 
                       "periodo", inizio + " - " + fine),
                HttpStatus.OK
            );
            
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Formato data non valido"),
                HttpStatus.BAD_REQUEST
            );
        }
    }
    
    // Stato attuale di un'aula
    @GetMapping("/stato/{aulaId}")
    public ResponseEntity<?> getStatoAula(@PathVariable Long aulaId) {
        String stato = prenotazioneService.getStatoAula(aulaId, LocalDateTime.now());
        
        return new ResponseEntity<>(
            Map.of("aulaId", aulaId, "stato", stato, "timestamp", LocalDateTime.now()),
            HttpStatus.OK
        );
    }
    
    // Lista prenotazioni utente
    @GetMapping("/mie")
    public ResponseEntity<?> getMiePrenotazioni(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) return authCheck;
        
        String token = authHeader.substring(7);
        Long utenteId = jwtService.getUserIdFromToken(token);
        
        List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniUtente(utenteId);
        
        return new ResponseEntity<>(
            Collections.singletonMap("prenotazioni", prenotazioni),
            HttpStatus.OK
        );
    }
    
    // Annulla prenotazione
    @DeleteMapping("/{prenotazioneId}")
    public ResponseEntity<?> annullaPrenotazione(@PathVariable Long prenotazioneId,
                                                @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) return authCheck;
        
        String token = authHeader.substring(7);
        Long utenteId = jwtService.getUserIdFromToken(token);
        
        boolean annullata = prenotazioneService.annullaPrenotazione(prenotazioneId, utenteId);
        
        if (!annullata) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Impossibile annullare la prenotazione"),
                HttpStatus.FORBIDDEN
            );
        }
        
        return new ResponseEntity<>(
            Collections.singletonMap("message", "Prenotazione annullata con successo"),
            HttpStatus.OK
        );
    }

    // ========== NUOVI ENDPOINT PER GESTIONE PRENOTAZIONI ==========

    // Lista tutte le prenotazioni (semplice) - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping
    public ResponseEntity<?> getAllPrenotazioni(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Prenotazione> prenotazioni = prenotazioneService.getAllPrenotazioni();
        if (prenotazioni == null || prenotazioni.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Nessuna prenotazione trovata"),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("prenotazioni", prenotazioni),
            HttpStatus.OK
        );
    }

    // Singola prenotazione per ID (semplice) - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrenotazioneById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id);
        if (prenotazione == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Prenotazione non trovata"),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("prenotazione", prenotazione),
            HttpStatus.OK
        );
    }

    // Dettagli completi di una prenotazione specifica - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getPrenotazioneDetailsById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        // Prima verifica se la prenotazione esiste
        Prenotazione prenotazione = prenotazioneService.getPrenotazioneById(id);
        if (prenotazione == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Prenotazione non trovata"),
                HttpStatus.NOT_FOUND
            );
        }

        // Ottieni i dettagli completi
        List<Map<String, Object>> dettagliCompleti = prenotazioneService.getPrenotazioneCompleteDetails(id);
        
        return new ResponseEntity<>(
            Map.of(
                "prenotazione", prenotazione,
                "dettagliCompleti", dettagliCompleti,
                "totalDettagli", dettagliCompleti.size()
            ),
            HttpStatus.OK
        );
    }

    // Vista completa di tutte le prenotazioni con dettagli - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/all-details")
    public ResponseEntity<?> getAllPrenotazioniWithDetails(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Map<String, Object>> dettagliCompleti = prenotazioneService.getAllCompleteDetails();
        
        return new ResponseEntity<>(
            Map.of(
                "prenotazioni", dettagliCompleti,
                "totalPrenotazioni", dettagliCompleti.size()
            ),
            HttpStatus.OK
        );
    }

    // Prenotazioni per stato - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/stato/{stato}")
    public ResponseEntity<?> getPrenotazioniByStato(@PathVariable String stato, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        try {
            Prenotazione.StatoPrenotazione statoEnum = Prenotazione.StatoPrenotazione.valueOf(stato.toUpperCase());
            List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniByStato(statoEnum);
            
            return new ResponseEntity<>(
                Map.of(
                    "stato", stato,
                    "prenotazioni", prenotazioni,
                    "totalPrenotazioni", prenotazioni.size()
                ),
                HttpStatus.OK
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Stato non valido. Stati disponibili: PRENOTATA, BLOCCATA, MANUTENZIONE, ANNULLATA"),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    // Prenotazioni future - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/future")
    public ResponseEntity<?> getPrenotazioniFuture(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Prenotazione> prenotazioni = prenotazioneService.getPrenotazioniFuture();
        
        return new ResponseEntity<>(
            Map.of(
                "prenotazioni", prenotazioni,
                "totalPrenotazioni", prenotazioni.size()
            ),
            HttpStatus.OK
        );
    }
}
