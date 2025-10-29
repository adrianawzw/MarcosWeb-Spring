package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // 🔹 Buscar citas por fecha exacta
    List<Cita> findByFecha(LocalDate fecha);

    // 🔹 Buscar citas disponibles (sin paciente asignado)
    List<Cita> findByPacienteIsNull();

    // 🔹 Buscar citas por paciente específico
    List<Cita> findByPacienteIdPaciente(Long idPaciente);

    // 🔹 Buscar citas disponibles por fecha específica
    List<Cita> findByFechaAndPacienteIsNull(LocalDate fecha);

    // 🔹 Buscar citas por odontólogo
    List<Cita> findByOdontologoIdOdontologo(Long idOdontologo);

    // 🔹 Buscar citas por consultorio
    List<Cita> findByConsultorioIdConsultorio(Long idConsultorio);

    // 🔹 Buscar citas por servicio
    List<Cita> findByServicioIdServicio(Long idServicio);

    // 🔹 Buscar citas por estado
    List<Cita> findByEstado(String estado);

    // 🔹 Buscar citas por rango de fechas
    List<Cita> findByFechaBetween(LocalDate startDate, LocalDate endDate);

    // 🔹 Buscar citas por paciente y estado
    List<Cita> findByPacienteIdPacienteAndEstado(Long idPaciente, String estado);

    // 🔹 Verificar si existe conflicto de horario para un odontólogo (para nuevas citas)
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
           "c.fecha = :fecha AND " +
           "c.odontologo.idOdontologo = :idOdontologo AND " +
           "((c.horaInicio < :horaFin AND c.horaFin > :horaInicio))")
    boolean existsConflictingCita(
            @Param("fecha") LocalDate fecha,
            @Param("idOdontologo") Long idOdontologo,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    // 🔹 Verificar conflicto excluyendo una cita específica (para edición)
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
           "c.fecha = :fecha AND " +
           "c.odontologo.idOdontologo = :idOdontologo AND " +
           "c.idCita != :idCitaExcluir AND " +
           "((c.horaInicio < :horaFin AND c.horaFin > :horaInicio))")
    boolean existsConflictingCitaExcluding(
            @Param("fecha") LocalDate fecha,
            @Param("idOdontologo") Long idOdontologo,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("idCitaExcluir") Long idCitaExcluir);

    // 🔹 Buscar citas disponibles por fecha y odontólogo
    @Query("SELECT c FROM Cita c WHERE " +
           "c.fecha = :fecha AND " +
           "c.odontologo.idOdontologo = :idOdontologo AND " +
           "c.paciente IS NULL")
    List<Cita> findCitasDisponiblesPorFechaYOdontologo(
            @Param("fecha") LocalDate fecha,
            @Param("idOdontologo") Long idOdontologo);

    // 🔹 Buscar citas por paciente con join para optimización (CORREGIDO)
    @Query("SELECT c FROM Cita c " +
           "JOIN FETCH c.odontologo o " +
           "JOIN FETCH c.consultorio " +
           "JOIN FETCH c.servicio " +
           "WHERE c.paciente.idPaciente = :idPaciente " +
           "ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findCitasCompletasPorPaciente(@Param("idPaciente") Long idPaciente);

    // 🔹 Buscar citas disponibles con toda la información cargada (CORREGIDO)
    @Query("SELECT c FROM Cita c " +
           "JOIN FETCH c.odontologo " +
           "JOIN FETCH c.consultorio " +
           "JOIN FETCH c.servicio " +
           "WHERE c.paciente IS NULL " +
           "ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findCitasDisponiblesCompletas();

    // 🔹 Contar citas por paciente
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.paciente.idPaciente = :idPaciente")
    long countByPacienteId(@Param("idPaciente") Long idPaciente);

    // 🔹 Buscar citas del día actual (CORREGIDO)
    @Query("SELECT c FROM Cita c WHERE c.fecha = CURRENT_DATE")
    List<Cita> findCitasDeHoy();

    // 🔹 Buscar citas próximas (próximos 7 días) - CORREGIDO
    @Query("SELECT c FROM Cita c WHERE c.fecha BETWEEN :hoy AND :enSieteDias")
    List<Cita> findCitasProximas(@Param("hoy") LocalDate hoy, 
                                 @Param("enSieteDias") LocalDate enSieteDias);

    // 🔹 Buscar citas por fecha y hora específica
    @Query("SELECT c FROM Cita c WHERE " +
           "c.fecha = :fecha AND " +
           "c.horaInicio = :horaInicio AND " +
           "c.odontologo.idOdontologo = :idOdontologo")
    Optional<Cita> findByFechaHoraYOdontologo(
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("idOdontologo") Long idOdontologo);

    // 🔹 Buscar citas que están por vencer (mañana) - CORREGIDO
    @Query("SELECT c FROM Cita c WHERE c.fecha = :manana")
    List<Cita> findCitasParaManana(@Param("manana") LocalDate manana);

    // 🔹 Estadísticas de citas por estado
    @Query("SELECT c.estado, COUNT(c) FROM Cita c GROUP BY c.estado")
    List<Object[]> countCitasByEstado();

    // 🔹 Citas por mes y año
    @Query("SELECT COUNT(c) FROM Cita c WHERE FUNCTION('YEAR', c.fecha) = :year AND FUNCTION('MONTH', c.fecha) = :month")
    long countCitasByMonthAndYear(@Param("year") int year, @Param("month") int month);
}