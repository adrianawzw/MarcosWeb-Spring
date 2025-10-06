package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
}
