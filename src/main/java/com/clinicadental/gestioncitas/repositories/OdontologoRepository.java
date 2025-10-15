package com.clinicadental.gestioncitas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    Optional<Odontologo> findByUsuario(Usuario usuario);
 // Método usando HQL/JPQL para buscar en múltiples campos relacionados (Usuario)
    @Query("SELECT o FROM Odontologo o WHERE " +
           "LOWER(o.usuario.nombre) LIKE LOWER(:termino) OR " +
           "LOWER(o.usuario.apellido) LIKE LOWER(:termino) OR " +
           "LOWER(o.usuario.dni) LIKE LOWER(:termino) OR " +
           "LOWER(o.nroColegiatura) LIKE LOWER(:termino) OR " +
           "LOWER(o.especialidad) LIKE LOWER(:termino)")
    List<Odontologo> buscarPorTermino(@Param("termino") String termino);
}
