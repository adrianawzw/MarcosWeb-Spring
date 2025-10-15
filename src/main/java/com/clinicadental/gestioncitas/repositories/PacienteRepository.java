package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUsuario(Usuario usuario);

    /**
     * Busca pacientes cuyo nombre, apellido, DNI, teléfono o historial clínico contenga el término.
     */
    @Query("SELECT p FROM Paciente p JOIN p.usuario u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(:termino) OR " +
           "LOWER(u.apellido) LIKE LOWER(:termino) OR " +
           "LOWER(u.dni) LIKE LOWER(:termino) OR " +
           "LOWER(u.telefono) LIKE LOWER(:termino) OR " +
           "LOWER(p.historialClinico) LIKE LOWER(:termino)")
    List<Paciente> buscarPorTermino(@Param("termino") String termino);
}
