package com.clinicadental.gestioncitas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    Optional<Odontologo> findByUsuario(Usuario usuario);
}
