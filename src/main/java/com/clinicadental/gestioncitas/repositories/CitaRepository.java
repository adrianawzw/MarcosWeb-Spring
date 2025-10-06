package com.clinicadental.gestioncitas.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicadental.gestioncitas.entities.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {
   
	// 🔹 Buscar citas por fecha exacta
    List<Cita> findByFecha(LocalDate fecha);


}
