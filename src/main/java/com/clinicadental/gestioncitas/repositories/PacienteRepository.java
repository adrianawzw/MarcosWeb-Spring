package com.clinicadental.gestioncitas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicadental.gestioncitas.entities.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
