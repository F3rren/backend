package com.prenotazioni;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.prenotazioni.model.Utente;
import com.prenotazioni.repository.UtenteRepository;

@SpringBootApplication
public class PrenotazioniAuleBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrenotazioniAuleBackendApplication.class, args);
    }


    @Bean
    public CommandLineRunner demo(UtenteRepository utenteRepository) {
        return args -> {
            if (utenteRepository.count() == 0) {
                Utente u1 = new Utente();
                u1.setNome("Mario Rossi");
                u1.setEmail("mario.rossi@email.com");
                u1.setRuolo("admin");
                u1.setPassword("password123");
                utenteRepository.save(u1);

                com.prenotazioni.model.Utente u2 = new com.prenotazioni.model.Utente();
                u2.setNome("Anna Bianchi");
                u2.setEmail("anna.bianchi@email.com");
                u2.setRuolo("user");
                u2.setPassword("qwerty");
                utenteRepository.save(u2);
            }
        };
    }
}
