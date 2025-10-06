package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {
}