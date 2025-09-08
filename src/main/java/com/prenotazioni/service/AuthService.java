package com.prenotazioni.service;

import com.prenotazioni.model.Utente;
import com.prenotazioni.repository.UtenteRepository;
import com.prenotazioni.dto.RegisterRequest;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UtenteRepository utenteRepository;

    public Utente login(String email, String password) {
        Utente utente = utenteRepository.findByEmailAndPassword(email, password);
        if (utente != null) {
            // Aggiorna l'ultimo accesso
            utente.setUltimoAccesso(LocalDateTime.now());
            utenteRepository.save(utente);
        }
        return utente;
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
        
        // Imposta la data di registrazione (non modificabile)
        utente.setDataRegistrazione(LocalDateTime.now());

        return utenteRepository.save(utente);
    }

    public List<Utente> getAllUsers() {
        return utenteRepository.findAll();
    }

    public boolean deleteUtente(Long id) {
        if (!utenteRepository.existsById(id)) {
            return false;
        }
        utenteRepository.deleteById(id);
        return true;
    }

    public Utente updateUtente(Long id, RegisterRequest request) {
        Utente utente = utenteRepository.findById(id).orElse(null);
        if (utente == null) {
            return null;
        }

        utente.setEmail(request.getEmail());
        utente.setNome(request.getNome());
        utente.setPassword(request.getPassword());
        utente.setRuolo(request.getRuolo());
        utente.setUsername(request.getUsername());
        
        // NON modifichiamo dataRegistrazione - rimane quella originale
        // ultimoAccesso viene aggiornato solo al login

        return utenteRepository.save(utente);
    }
}
