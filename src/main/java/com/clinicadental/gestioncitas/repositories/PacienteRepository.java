package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // ✅ Buscar paciente por usuario
    Optional<Paciente> findByUsuario(Usuario usuario);

    // ✅ Buscar paciente por ID de usuario
    @Query("SELECT p FROM Paciente p WHERE p.usuario.idUsuario = :idUsuario")
    Optional<Paciente> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    // ✅ Buscar paciente por DNI del usuario
    @Query("SELECT p FROM Paciente p WHERE p.usuario.dni = :dni")
    Optional<Paciente> findByUsuarioDni(@Param("dni") String dni);

    // ✅ Buscar paciente por email del usuario
    @Query("SELECT p FROM Paciente p WHERE p.usuario.correo = :correo")
    Optional<Paciente> findByUsuarioCorreo(@Param("correo") String correo);

    // ✅ Buscar pacientes por término en múltiples campos
    @Query("SELECT p FROM Paciente p JOIN p.usuario u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(u.dni) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(u.telefono) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.historialClinico) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Paciente> buscarPorTermino(@Param("termino") String termino);

    // ✅ Buscar pacientes por nombre (opcional)
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Paciente> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    // ✅ Buscar pacientes por apellido (opcional)
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.usuario.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    List<Paciente> findByApellidoContainingIgnoreCase(@Param("apellido") String apellido);

    // ✅ Verificar si existe un paciente con un DNI específico
    @Query("SELECT COUNT(p) > 0 FROM Paciente p WHERE p.usuario.dni = :dni")
    boolean existsByDni(@Param("dni") String dni);

    // ✅ Verificar si existe un paciente con un email específico
    @Query("SELECT COUNT(p) > 0 FROM Paciente p WHERE p.usuario.correo = :correo")
    boolean existsByCorreo(@Param("correo") String correo);
}