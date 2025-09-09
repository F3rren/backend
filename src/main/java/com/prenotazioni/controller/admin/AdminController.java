package com.prenotazioni.controller.admin;

import com.prenotazioni.service.AuthService;
import com.prenotazioni.service.JwtService;
import com.prenotazioni.service.AulaService;
import com.prenotazioni.dto.RegisterRequest;
import com.prenotazioni.dto.AulaRequest;
import com.prenotazioni.model.Utente;
import com.prenotazioni.model.Aula;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AulaService aulaService;

    // Metodo privato per verificare se l'utente è admin
    private ResponseEntity<?> checkAdminAccess(String authHeader) {
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

        String ruolo = jwtService.getRuoloFromToken(token);
        if (!"admin".equals(ruolo)) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Accesso negato: solo gli amministratori possono accedere"),
                HttpStatus.FORBIDDEN
            );
        }

        return null; // Access granted
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        Utente utente = authService.register(request);
        if (utente == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Email o username già esistenti"),
                HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
            Collections.singletonMap("success", "Utente registrato con successo dall'amministratore"),
            HttpStatus.CREATED
        );
    }

    // Lista tutti gli utenti
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        List<Utente> users = authService.getAllUsers();
        if (users == null || users.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Nessun utente trovato"),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("users", users),
            HttpStatus.OK
        );
    }

    // Modifica utente
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUtente(@PathVariable Long id, @RequestBody RegisterRequest request, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        Utente updated = authService.updateUtente(id, request);
        if (updated == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Utente non trovato o non modificabile"),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("message", "Utente selezionato modificato con successo"),
            HttpStatus.OK
        );
    }

    // Eliminazione utente
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUtente(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        boolean deleted = authService.deleteUtente(id);
        if (!deleted) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Utente non trovato o non eliminabile"),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("message", "Utente eliminato con successo"),
            HttpStatus.OK
        );
    }

    // Lista tutte le aule
    @GetMapping("/rooms")
    public ResponseEntity<?> getAllRooms(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
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

    // Ottieni singola aula per ID
    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
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

    // Gestione stanze - Creazione stanza
    @PostMapping("/createrooms")
    public ResponseEntity<?> createRoom(@RequestBody AulaRequest roomRequest, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        Aula nuovaAula = aulaService.createAula(roomRequest);
        if (nuovaAula == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Impossibile creare l'aula. Verifica che il nome non sia già esistente e che i dati siano validi."),
                HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("message", "Aula creata con successo"),
            HttpStatus.CREATED
        );
    }

    // Modifica stanza
    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody AulaRequest roomRequest, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        Aula aulaAggiornata = aulaService.updateAula(id, roomRequest);
        if (aulaAggiornata == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Impossibile aggiornare l'aula. Verifica che l'ID sia corretto e che i dati siano validi."),
                HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("message", "Aula aggiornata con successo"),
            HttpStatus.OK
        );
    }

    // Eliminazione stanza
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        boolean eliminata = aulaService.deleteAula(id);
        if (!eliminata) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Impossibile eliminare l'aula. Verifica che l'ID sia corretto."),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            Collections.singletonMap("message", "Aula eliminata con successo"),
            HttpStatus.OK
        );
    }
}
