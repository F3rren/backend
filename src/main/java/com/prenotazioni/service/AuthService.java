package com.prenotazioni.service;

import com.prenotazioni.model.Utente;
import com.prenotazioni.repository.UtenteRepository;
import com.prenotazioni.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UtenteRepository utenteRepository;

    public Utente login(String email, String password) {
        return utenteRepository.findByEmailAndPassword(email, password);
    }

    public Utente register(RegisterRequest request) {
        if (utenteRepository.findByEmail(request.getEmail()) != null) return null;
        if (utenteRepository.findByUsername(request.getUsername()) != null) return null;
        
        Utente utente = new Utente();
        utente.setEmail(request.getEmail());
        utente.setNome(request.getNome()); 
        utente.setPassword(request.getPassword());
        utente.setRuolo(request.getRuolo());
        utente.setUsername(request.getUsername());

        return utenteRepository.save(utente);
    }
}
