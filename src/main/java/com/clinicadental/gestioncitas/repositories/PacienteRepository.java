package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUsuario(Usuario usuario);
}
