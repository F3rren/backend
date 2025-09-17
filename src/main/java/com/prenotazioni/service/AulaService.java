package com.prenotazioni.service;

import com.prenotazioni.model.Aula;
import com.prenotazioni.model.Prenotazione;
import com.prenotazioni.repository.AulaRepository;
import com.prenotazioni.repository.PrenotazioneRepository;
import com.prenotazioni.dto.AulaRequest;
import com.prenotazioni.dto.RoomDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

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
        System.out.println("🏠 AulaService.createAula - Dati ricevuti:");
        System.out.println("   Nome: " + request.getNome());
        System.out.println("   Capienza: " + request.getCapienza());
        System.out.println("   Piano: " + request.getPiano());
        System.out.println("   isVirtual: " + request.isVirtual());
        
        // Verifica che il nome non sia già esistente
        if (aulaRepository.existsByNomeIgnoreCase(request.getNome())) {
            System.out.println("❌ Nome già esistente: " + request.getNome());
            return null; // Nome già esistente
        }

        // Validazioni
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            System.out.println("❌ Nome non valido");
            return null; // Nome non valido
        }
        if (request.getCapienza() <= 0) {
            System.out.println("❌ Capienza non valida: " + request.getCapienza());
            return null; // Capienza non valida
        }
        if (request.getPiano() < 0) {
            System.out.println("❌ Piano non valido: " + request.getPiano());
            return null; // Piano non valido
        }

        Aula aula = new Aula();
        aula.setNome(request.getNome().trim());
        aula.setCapienza(request.getCapienza());
        aula.setPiano(request.getPiano());
        aula.setVirtual(request.isVirtual());

        System.out.println("✅ Creazione aula - Dati finali:");
        System.out.println("   Nome: " + aula.getNome());
        System.out.println("   Capienza: " + aula.getCapienza());
        System.out.println("   Piano: " + aula.getPiano());
        System.out.println("   isVirtual: " + aula.isVirtual());

        Aula savedAula = aulaRepository.save(aula);
        System.out.println("💾 Aula salvata con ID: " + savedAula.getId());
        
        return savedAula;
    }

    // Aggiorna un'aula esistente
    public Aula updateAula(Long id, AulaRequest request) {
        System.out.println("🔄 AulaService.updateAula - ID: " + id);
        System.out.println("   Dati ricevuti:");
        System.out.println("   Nome: " + request.getNome());
        System.out.println("   Capienza: " + request.getCapienza());
        System.out.println("   Piano: " + request.getPiano());
        System.out.println("   isVirtual: " + request.isVirtual());
        
        Optional<Aula> aulaOptional = aulaRepository.findById(id);
        if (aulaOptional.isEmpty()) {
            System.out.println("❌ Aula non trovata con ID: " + id);
            return null; // Aula non trovata
        }

        Aula aula = aulaOptional.get();
        System.out.println("📖 Dati aula esistente:");
        System.out.println("   Nome: " + aula.getNome());
        System.out.println("   Capienza: " + aula.getCapienza());
        System.out.println("   Piano: " + aula.getPiano());
        System.out.println("   isVirtual: " + aula.isVirtual());

        // Verifica che il nome non sia già esistente (escludendo l'aula corrente)
        if (aulaRepository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            System.out.println("❌ Nome già esistente: " + request.getNome());
            return null; // Nome già esistente
        }

        // Validazioni
        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            System.out.println("❌ Nome non valido");
            return null; // Nome non valido
        }
        if (request.getCapienza() <= 0) {
            System.out.println("❌ Capienza non valida: " + request.getCapienza());
            return null; // Capienza non valida
        }
        if (request.getPiano() < 0) {
            System.out.println("❌ Piano non valido: " + request.getPiano());
            return null; // Piano non valido
        }

        aula.setNome(request.getNome().trim());
        aula.setCapienza(request.getCapienza());
        aula.setPiano(request.getPiano());
        aula.setVirtual(request.isVirtual());

        System.out.println("✅ Aggiornamento aula - Dati finali:");
        System.out.println("   Nome: " + aula.getNome());
        System.out.println("   Capienza: " + aula.getCapienza());
        System.out.println("   Piano: " + aula.getPiano());
        System.out.println("   isVirtual: " + aula.isVirtual());

        Aula savedAula = aulaRepository.save(aula);
        System.out.println("💾 Aula aggiornata con ID: " + savedAula.getId());
        
        return savedAula;
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

    // Ottieni i dettagli completi di tutte le aule con informazioni di stato e prenotazioni
    public List<RoomDetailsResponse> getAllRoomsWithDetails() {
        List<Aula> aule = aulaRepository.findAll();
        List<RoomDetailsResponse> response = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Aula aula : aule) {
            RoomDetailsResponse roomDetails = new RoomDetailsResponse(aula.getId(), aula.getNome(), aula.getPiano(), aula.getCapienza(), aula.isVirtual());
            
            // Ottieni tutte le prenotazioni per questa aula
            List<Prenotazione> prenotazioni = prenotazioneRepository.findByAulaId(aula.getId());
            
            // Determina lo stato dell'aula
            String status = "libera";
            RoomDetailsResponse.CurrentBooking currentBooking = null;
            RoomDetailsResponse.BlockInfo blockInfo = null;
            
            LocalDateTime now = LocalDateTime.now();
            
            // Controllo se l'aula è attualmente occupata o bloccata
            for (Prenotazione prenotazione : prenotazioni) {
                if (prenotazione.getInizio().isBefore(now) && prenotazione.getFine().isAfter(now)) {
                    if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                        status = "prenotata";
                        currentBooking = new RoomDetailsResponse.CurrentBooking(
                            prenotazione.getUtente().getNome(),
                            prenotazione.getInizio().toLocalDate().format(dateFormatter),
                            prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                        );
                    } else if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.BLOCCATA || 
                              prenotazione.getStato() == Prenotazione.StatoPrenotazione.MANUTENZIONE) {
                        status = "bloccata";
                        blockInfo = new RoomDetailsResponse.BlockInfo(
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Aula bloccata",
                            "admin",
                            prenotazione.getDataCreazione().toLocalDate().format(dateFormatter)
                        );
                    }
                    break;
                }
            }
            
            // Se non è attualmente occupata, controlla se ci sono prenotazioni future nelle prossime 2 ore
            if (status.equals("libera")) {
                LocalDateTime twoHoursLater = now.plusHours(2);
                for (Prenotazione prenotazione : prenotazioni) {
                    if (prenotazione.getInizio().isAfter(now) && prenotazione.getInizio().isBefore(twoHoursLater) &&
                        prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                        status = "prenotata";
                        currentBooking = new RoomDetailsResponse.CurrentBooking(
                            prenotazione.getUtente().getNome(),
                            prenotazione.getInizio().toLocalDate().format(dateFormatter),
                            prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                        );
                        break;
                    }
                }
            }
            
            // Crea la lista delle prenotazioni
            List<RoomDetailsResponse.BookingInfo> bookingInfos = new ArrayList<>();
            for (Prenotazione prenotazione : prenotazioni) {
                if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                    bookingInfos.add(new RoomDetailsResponse.BookingInfo(
                        prenotazione.getInizio().toLocalDate().format(dateFormatter),
                        prenotazione.getInizio().format(timeFormatter),
                        prenotazione.getFine().format(timeFormatter),
                        prenotazione.getUtente().getNome(),
                        prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                    ));
                }
            }
            
            roomDetails.setStatus(status);
            roomDetails.setBooking(currentBooking);
            roomDetails.setBlocked(blockInfo);
            roomDetails.setBookings(bookingInfos);
            
            response.add(roomDetails);
        }
        
        return response;
    }

    // Ottieni i dettagli completi di una singola aula
    public RoomDetailsResponse getRoomWithDetails(Long aulaId) {
        Optional<Aula> aulaOpt = aulaRepository.findById(aulaId);
        if (aulaOpt.isEmpty()) {
            return null;
        }
        
        Aula aula = aulaOpt.get();
        RoomDetailsResponse roomDetails = new RoomDetailsResponse(aula.getId(), aula.getNome(), aula.getPiano(), aula.getCapienza(), aula.isVirtual());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // Ottieni tutte le prenotazioni per questa aula
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByAulaId(aula.getId());
        
        // Determina lo stato dell'aula
        String status = "libera";
        RoomDetailsResponse.CurrentBooking currentBooking = null;
        RoomDetailsResponse.BlockInfo blockInfo = null;
        
        LocalDateTime now = LocalDateTime.now();
        
        // Controllo se l'aula è attualmente occupata o bloccata
        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getInizio().isBefore(now) && prenotazione.getFine().isAfter(now)) {
                if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                    status = "prenotata";
                    currentBooking = new RoomDetailsResponse.CurrentBooking(
                        prenotazione.getUtente().getNome(),
                        prenotazione.getInizio().toLocalDate().format(dateFormatter),
                        prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                        prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                    );
                } else if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.BLOCCATA || 
                          prenotazione.getStato() == Prenotazione.StatoPrenotazione.MANUTENZIONE) {
                    status = "bloccata";
                    blockInfo = new RoomDetailsResponse.BlockInfo(
                        prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Aula bloccata",
                        "admin",
                        prenotazione.getDataCreazione().toLocalDate().format(dateFormatter)
                    );
                }
                break;
            }
        }
        
        // Se non è attualmente occupata, controlla se ci sono prenotazioni future nelle prossime 2 ore
        if (status.equals("libera")) {
            LocalDateTime twoHoursLater = now.plusHours(2);
            for (Prenotazione prenotazione : prenotazioni) {
                if (prenotazione.getInizio().isAfter(now) && prenotazione.getInizio().isBefore(twoHoursLater) &&
                    prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                    status = "prenotata";
                    currentBooking = new RoomDetailsResponse.CurrentBooking(
                        prenotazione.getUtente().getNome(),
                        prenotazione.getInizio().toLocalDate().format(dateFormatter),
                        prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                        prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                    );
                    break;
                }
            }
        }
        
        // Crea la lista delle prenotazioni
        List<RoomDetailsResponse.BookingInfo> bookingInfos = new ArrayList<>();
        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                bookingInfos.add(new RoomDetailsResponse.BookingInfo(
                    prenotazione.getInizio().toLocalDate().format(dateFormatter),
                    prenotazione.getInizio().format(timeFormatter),
                    prenotazione.getFine().format(timeFormatter),
                    prenotazione.getUtente().getNome(),
                    prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                ));
            }
        }
        
        roomDetails.setStatus(status);
        roomDetails.setBooking(currentBooking);
        roomDetails.setBlocked(blockInfo);
        roomDetails.setBookings(bookingInfos);
        
        return roomDetails;
    }
    
    // Metodi per gestire aule fisiche e virtuali
    
    // Ottieni tutte le aule fisiche
    public List<Aula> getPhysicalRooms() {
        return aulaRepository.findByIsVirtual(false);
    }
    
    // Ottieni tutte le aule virtuali
    public List<Aula> getVirtualRooms() {
        return aulaRepository.findByIsVirtual(true);
    }
    
    // Ottieni aule fisiche ordinate per piano e nome
    public List<Aula> getPhysicalRoomsOrdered() {
        return aulaRepository.findPhysicalRoomsOrderByPianoAndNome();
    }
    
    // Ottieni aule virtuali ordinate per nome
    public List<Aula> getVirtualRoomsOrdered() {
        return aulaRepository.findVirtualRoomsOrderByNome();
    }
    
    // Ottieni i dettagli delle aule fisiche
    public List<RoomDetailsResponse> getPhysicalRoomsWithDetails() {
        List<Aula> aule = aulaRepository.findByIsVirtual(false);
        return getRoomsDetailsFromList(aule);
    }
    
    // Ottieni i dettagli delle aule virtuali
    public List<RoomDetailsResponse> getVirtualRoomsWithDetails() {
        List<Aula> aule = aulaRepository.findByIsVirtual(true);
        return getRoomsDetailsFromList(aule);
    }
    
    // Conta aule fisiche
    public long countPhysicalRooms() {
        return aulaRepository.countByIsVirtual(false);
    }
    
    // Conta aule virtuali  
    public long countVirtualRooms() {
        return aulaRepository.countByIsVirtual(true);
    }
    
    // Metodo di utilità privato per evitare duplicazione del codice
    private List<RoomDetailsResponse> getRoomsDetailsFromList(List<Aula> aule) {
        List<RoomDetailsResponse> response = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Aula aula : aule) {
            RoomDetailsResponse roomDetails = new RoomDetailsResponse(aula.getId(), aula.getNome(), aula.getPiano(), aula.getCapienza(), aula.isVirtual());
            
            // Ottieni tutte le prenotazioni per questa aula
            List<Prenotazione> prenotazioni = prenotazioneRepository.findByAulaId(aula.getId());
            
            // Determina lo stato dell'aula
            String status = "libera";
            RoomDetailsResponse.CurrentBooking currentBooking = null;
            RoomDetailsResponse.BlockInfo blockInfo = null;
            
            LocalDateTime now = LocalDateTime.now();
            
            // Controllo se l'aula è attualmente occupata o bloccata
            for (Prenotazione prenotazione : prenotazioni) {
                if (prenotazione.getInizio().isBefore(now) && prenotazione.getFine().isAfter(now)) {
                    if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                        status = "prenotata";
                        currentBooking = new RoomDetailsResponse.CurrentBooking(
                            prenotazione.getUtente().getNome(),
                            prenotazione.getInizio().toLocalDate().format(dateFormatter),
                            prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                        );
                    } else if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.BLOCCATA || 
                              prenotazione.getStato() == Prenotazione.StatoPrenotazione.MANUTENZIONE) {
                        status = "bloccata";
                        blockInfo = new RoomDetailsResponse.BlockInfo(
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Aula bloccata",
                            "admin",
                            prenotazione.getDataCreazione().toLocalDate().format(dateFormatter)
                        );
                    }
                    break;
                }
            }
            
            // Se non è attualmente occupata, controlla se ci sono prenotazioni future nelle prossime 2 ore
            if (status.equals("libera")) {
                LocalDateTime twoHoursLater = now.plusHours(2);
                for (Prenotazione prenotazione : prenotazioni) {
                    if (prenotazione.getInizio().isAfter(now) && prenotazione.getInizio().isBefore(twoHoursLater) &&
                        prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                        status = "prenotata";
                        currentBooking = new RoomDetailsResponse.CurrentBooking(
                            prenotazione.getUtente().getNome(),
                            prenotazione.getInizio().toLocalDate().format(dateFormatter),
                            prenotazione.getInizio().format(timeFormatter) + "-" + prenotazione.getFine().format(timeFormatter),
                            prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                        );
                        break;
                    }
                }
            }
            
            // Crea la lista delle prenotazioni
            List<RoomDetailsResponse.BookingInfo> bookingInfos = new ArrayList<>();
            for (Prenotazione prenotazione : prenotazioni) {
                if (prenotazione.getStato() == Prenotazione.StatoPrenotazione.PRENOTATA) {
                    bookingInfos.add(new RoomDetailsResponse.BookingInfo(
                        prenotazione.getInizio().toLocalDate().format(dateFormatter),
                        prenotazione.getInizio().format(timeFormatter),
                        prenotazione.getFine().format(timeFormatter),
                        prenotazione.getUtente().getNome(),
                        prenotazione.getDescrizione() != null ? prenotazione.getDescrizione() : "Lezione"
                    ));
                }
            }
            
            roomDetails.setStatus(status);
            roomDetails.setBooking(currentBooking);
            roomDetails.setBlocked(blockInfo);
            roomDetails.setBookings(bookingInfos);
            
            response.add(roomDetails);
        }
        
        return response;
    }
}
