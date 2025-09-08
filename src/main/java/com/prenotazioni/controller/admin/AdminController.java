package com.prenotazioni.controller.admin;

import com.prenotazioni.service.AuthService;
import com.prenotazioni.service.JwtService;
import com.prenotazioni.dto.RegisterRequest;
import com.prenotazioni.model.Utente;

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

    // Gestione stanze - Creazione stanza
    @PostMapping("/createrooms")
    public ResponseEntity<?> createRoom(@RequestBody Object roomRequest, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        // TODO: Implementare logica di creazione stanza
        return new ResponseEntity<>(
            Collections.singletonMap("message", "Funzionalità creazione stanza da implementare"),
            HttpStatus.NOT_IMPLEMENTED
        );
    }

    // Modifica stanza
    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Object roomRequest, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        // TODO: Implementare logica di modifica stanza
        return new ResponseEntity<>(
            Collections.singletonMap("message", "Funzionalità modifica stanza da implementare"),
            HttpStatus.NOT_IMPLEMENTED
        );
    }

    // Eliminazione stanza
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> accessCheck = checkAdminAccess(authHeader);
        if (accessCheck != null) {
            return accessCheck;
        }

        // TODO: Implementare logica di eliminazione stanza
        return new ResponseEntity<>(
            Collections.singletonMap("message", "Funzionalità eliminazione stanza da implementare"),
            HttpStatus.NOT_IMPLEMENTED
        );
    }
}
