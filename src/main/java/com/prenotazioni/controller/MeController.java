package com.prenotazioni.controller;

import com.prenotazioni.model.Utente;
import com.prenotazioni.repository.UtenteRepository;
import com.prenotazioni.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getMe(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        Utente utente = utenteRepository.findByEmail(email);
        if (utente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(utente);
    }
}
