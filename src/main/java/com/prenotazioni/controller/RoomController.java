package com.prenotazioni.controller;

import com.prenotazioni.service.JwtService;
import com.prenotazioni.service.AulaService;
import com.prenotazioni.service.PrenotazioneService;
import com.prenotazioni.model.Aula;
import com.prenotazioni.dto.RoomDetailsResponse;

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
    @GetMapping("/details")
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

    // Endpoint per ottenere tutte le aule con dettagli completi (formato mock-like)
    @GetMapping("/detailed")
    public ResponseEntity<?> getAllRoomsDetailed(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<RoomDetailsResponse> roomDetails = aulaService.getAllRoomsWithDetails();
        
        return new ResponseEntity<>(
            Collections.singletonMap("rooms", roomDetails),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere una singola aula con dettagli completi
    @GetMapping("/{id}/detailed")
    public ResponseEntity<?> getRoomDetailed(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        RoomDetailsResponse roomDetails = aulaService.getRoomWithDetails(id);
        if (roomDetails == null) {
            return new ResponseEntity<>(
                Collections.singletonMap("error", "Aula non trovata"),
                HttpStatus.NOT_FOUND
            );
        }
        
        return new ResponseEntity<>(
            Collections.singletonMap("room", roomDetails),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere solo le aule fisiche
    @GetMapping("/physical")
    public ResponseEntity<?> getPhysicalRooms(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Aula> aule = aulaService.getPhysicalRoomsOrdered();
        return new ResponseEntity<>(
            Map.of(
                "rooms", aule,
                "totalRooms", aule.size(),
                "type", "physical"
            ),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere solo le aule virtuali
    @GetMapping("/virtual")
    public ResponseEntity<?> getVirtualRooms(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<Aula> aule = aulaService.getVirtualRoomsOrdered();
        return new ResponseEntity<>(
            Map.of(
                "rooms", aule,
                "totalRooms", aule.size(),
                "type", "virtual"
            ),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere aule fisiche con dettagli completi
    @GetMapping("/physical/detailed")
    public ResponseEntity<?> getPhysicalRoomsDetailed(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<RoomDetailsResponse> roomDetails = aulaService.getPhysicalRoomsWithDetails();
        return new ResponseEntity<>(
            Map.of(
                "rooms", roomDetails,
                "totalRooms", roomDetails.size(),
                "type", "physical"
            ),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere aule virtuali con dettagli completi
    @GetMapping("/virtual/detailed")
    public ResponseEntity<?> getVirtualRoomsDetailed(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        List<RoomDetailsResponse> roomDetails = aulaService.getVirtualRoomsWithDetails();
        return new ResponseEntity<>(
            Map.of(
                "rooms", roomDetails,
                "totalRooms", roomDetails.size(),
                "type", "virtual"
            ),
            HttpStatus.OK
        );
    }

    // Endpoint per ottenere statistiche aule fisiche vs virtuali
    @GetMapping("/stats")
    public ResponseEntity<?> getRoomsStats(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> authCheck = checkAuth(authHeader);
        if (authCheck != null) {
            return authCheck;
        }

        long physicalCount = aulaService.countPhysicalRooms();
        long virtualCount = aulaService.countVirtualRooms();
        long totalCount = physicalCount + virtualCount;

        return new ResponseEntity<>(
            Map.of(
                "totalRooms", totalCount,
                "physicalRooms", physicalCount,
                "virtualRooms", virtualCount,
                "physicalPercentage", totalCount > 0 ? Math.round((physicalCount * 100.0) / totalCount) : 0,
                "virtualPercentage", totalCount > 0 ? Math.round((virtualCount * 100.0) / totalCount) : 0
            ),
            HttpStatus.OK
        );
    }
}
