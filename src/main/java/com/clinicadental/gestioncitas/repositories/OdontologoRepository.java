package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
}