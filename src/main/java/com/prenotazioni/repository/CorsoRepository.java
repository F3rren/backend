package com.prenotazioni.repository;

import com.prenotazioni.model.Corso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorsoRepository extends JpaRepository<Corso, Long> {}
