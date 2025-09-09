package com.prenotazioni.service;

import com.prenotazioni.model.Aula;
import com.prenotazioni.repository.AulaRepository;
import com.prenotazioni.dto.AulaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    // Ottieni tutte le aule
    public List<Aula> getAllAule() {
        return aulaRepository.findAll();
    }

    // Ottieni una singola aula per ID
    public Optional<Aula> getAulaById(Long id) {
        return aulaRepository.findById(id);
    }

    // Crea una nuova aula
    public Aula createAula(AulaRequest request) {
        // Verifica che il nome non sia già esistente
        if (aulaRepository.existsByNomeIgnoreCase(request.getNome())) {
            return null; // Nome già esistente
        }

        // Validazioni
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            return null; // Nome non valido
        }
        if (request.getCapienza() <= 0) {
            return null; // Capienza non valida
        }
        if (request.getPiano() < 0) {
            return null; // Piano non valido
        }

        Aula aula = new Aula();
        aula.setNome(request.getNome().trim());
        aula.setCapienza(request.getCapienza());
        aula.setPiano(request.getPiano());

        return aulaRepository.save(aula);
    }

    // Aggiorna un'aula esistente
    public Aula updateAula(Long id, AulaRequest request) {
        Optional<Aula> aulaOptional = aulaRepository.findById(id);
        if (aulaOptional.isEmpty()) {
            return null; // Aula non trovata
        }

        Aula aula = aulaOptional.get();

        // Verifica che il nome non sia già esistente (escludendo l'aula corrente)
        if (aulaRepository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            return null; // Nome già esistente
        }

        // Validazioni
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            return null; // Nome non valido
        }
        if (request.getCapienza() <= 0) {
            return null; // Capienza non valida
        }
        if (request.getPiano() < 0) {
            return null; // Piano non valido
        }

        aula.setNome(request.getNome().trim());
        aula.setCapienza(request.getCapienza());
        aula.setPiano(request.getPiano());

        return aulaRepository.save(aula);
    }

    // Elimina un'aula
    public boolean deleteAula(Long id) {
        Optional<Aula> aulaOptional = aulaRepository.findById(id);
        if (aulaOptional.isEmpty()) {
            return false; // Aula non trovata
        }

        try {
            aulaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false; // Errore durante l'eliminazione
        }
    }

    // Verifica se un'aula esiste per nome
    public boolean aulaExistsByName(String nome) {
        return aulaRepository.existsByNomeIgnoreCase(nome);
    }
    
    // Filtra aule per piano
    public List<Aula> getAuleByPiano(int piano) {
        return aulaRepository.findByPiano(piano);
    }
    
    // Filtra aule per capienza minima
    public List<Aula> getAuleByCapienzaMinima(int minCapienza) {
        return aulaRepository.findByCapienzaGreaterThanEqual(minCapienza);
    }
}
