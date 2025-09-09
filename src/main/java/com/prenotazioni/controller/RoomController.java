package com.prenotazioni.controller;

import com.prenotazioni.service.JwtService;
import com.prenotazioni.service.AulaService;
import com.prenotazioni.service.PrenotazioneService;
import com.prenotazioni.model.Aula;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private AulaService aulaService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PrenotazioneService prenotazioneService;

    // Metodo privato per verificare autenticazione (senza controllo ruolo)
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

        return null; // Access granted per tutti gli utenti autenticati
    }

    // Lista tutte le aule - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping
    public ResponseEntity<?> getAllRooms(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Aula> aule = aulaService.getAllAule();
        if (aule == null || aule.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Nessuna aula trovata"),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("rooms", aule),
            HttpStatus.OK
        );
    }

    // Vista completa di tutte le prenotazioni - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/all-details")
    public ResponseEntity<?> getAllRoomsWithDetails(@RequestHeader("Authorization") String authHeader) {
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

    // Ottieni singola aula per ID - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI  
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        java.util.Optional<Aula> aula = aulaService.getAulaById(id);
        if (aula.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Aula non trovata"),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("room", aula.get()),
            HttpStatus.OK
        );
    }

    // Ottieni dettagli completi aula con prenotazioni - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getRoomDetailsById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        // Prima verifica se l'aula esiste
        java.util.Optional<Aula> aula = aulaService.getAulaById(id);
        if (aula.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Aula non trovata"),
                HttpStatus.NOT_FOUND
            );
        }

        // Ottieni i dettagli completi
        List<Map<String, Object>> dettagliCompleti = prenotazioneService.getRoomCompleteDetails(id);
        
        return new ResponseEntity<>(
            Map.of(
                "aula", aula.get(),
                "prenotazioni", dettagliCompleti,
                "totalPrenotazioni", dettagliCompleti.size()
            ),
            HttpStatus.OK
        );
    }

    // Filtra aule per piano - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/piano/{piano}")
    public ResponseEntity<?> getRoomsByFloor(@PathVariable int piano, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Aula> aule = aulaService.getAuleByPiano(piano);
        if (aule == null || aule.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Nessuna aula trovata per il piano " + piano),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("rooms", aule),
            HttpStatus.OK
        );
    }

    // Filtra aule per capienza minima - ACCESSIBILE A TUTTI GLI UTENTI AUTENTICATI
    @GetMapping("/capienza")
    public ResponseEntity<?> getRoomsByCapacity(@RequestParam int minCapienza, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Aula> aule = aulaService.getAuleByCapienzaMinima(minCapienza);
        if (aule == null || aule.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Nessuna aula trovata con capienza >= " + minCapienza),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("rooms", aule),
            HttpStatus.OK
        );
    }
}
