package com.clinicadental.gestioncitas.repositories;

import com.clinicadental.gestioncitas.entities.Cita;
import com.clinicadental.gestioncitas.entities.Odontologo;

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

    List<Cita> findByFecha(LocalDate fecha);

    List<Cita> findByPacienteIsNull();

    List<Cita> findByPacienteIdPaciente(Long idPaciente);

    List<Cita> findByFechaAndPacienteIsNull(LocalDate fecha);

    List<Cita> findByOdontologoIdOdontologo(Long idOdontologo);

    List<Cita> findByConsultorioIdConsultorio(Long idConsultorio);

    List<Cita> findByServicioIdServicio(Long idServicio);

    List<Cita> findByEstado(String estado);

    List<Cita> findByFechaBetween(LocalDate startDate, LocalDate endDate);

    // 🔹 Buscar citas por paciente y estado
    List<Cita> findByPacienteIdPacienteAndEstado(Long idPaciente, String estado);
    
    List<Cita> findByOdontologoOrderByFechaAscHoraInicioAsc(Odontologo odontologo);
    List<Cita> findByOdontologoAndEstadoOrderByFechaAscHoraInicioAsc(Odontologo odontologo, String estado);
    List<Cita> findByOdontologoAndFechaOrderByHoraInicioAsc(Odontologo odontologo, LocalDate fecha);

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
    
 // 🔹 Buscar citas por odontólogo (para el panel del odontólogo)
    List<Cita> findByOdontologo(Odontologo odontologo);

    // 🔹 Buscar citas por odontólogo y estado específico
    List<Cita> findByOdontologoAndEstado(Odontologo odontologo, String estado);

    // 🔹 Buscar citas de hoy para un odontólogo específico
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.fecha = CURRENT_DATE ORDER BY c.horaInicio ASC")
    List<Cita> findCitasHoyPorOdontologo(@Param("odontologo") Odontologo odontologo);

    // 🔹 Buscar citas por odontólogo y fecha específica
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.fecha = :fecha ORDER BY c.horaInicio ASC")
    List<Cita> findCitasPorOdontologoYFecha(
            @Param("odontologo") Odontologo odontologo,
            @Param("fecha") LocalDate fecha);

    // 🔹 Buscar citas reservadas de un odontólogo (para confirmación)
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.estado = 'RESERVADA' ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findCitasReservadasPorOdontologo(@Param("odontologo") Odontologo odontologo);

    // 🔹 Buscar citas confirmadas de un odontólogo
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.estado = 'CONFIRMADA' ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findCitasConfirmadasPorOdontologo(@Param("odontologo") Odontologo odontologo);

    // 🔹 Contar citas por estado para un odontólogo específico
    //@Query("SELECT COUNT(c) FROM Cita c WHERE c.odontologo = :odontologo AND c.estado = :estado")
    //long countByOdontologoAndEstado(@Param("odontologo") Odontologo odontologo, @Param("estado") String estado);

    // 🔹 Buscar próximas citas de un odontólogo (próximos 7 días)
    @Query("SELECT c FROM Cita c WHERE c.odontologo = :odontologo AND c.fecha BETWEEN CURRENT_DATE AND :fechaFin ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findProximasCitasPorOdontologo(
            @Param("odontologo") Odontologo odontologo,
            @Param("fechaFin") LocalDate fechaFin);

    // 🔹 Verificar si un odontólogo tiene citas en un horario específico
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE " +
           "c.odontologo = :odontologo AND " +
           "c.fecha = :fecha AND " +
           "c.estado IN ('RESERVADA', 'CONFIRMADA') AND " +
           "((c.horaInicio < :horaFin AND c.horaFin > :horaInicio))")
    boolean existsCitaActivaEnHorario(
            @Param("odontologo") Odontologo odontologo,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    // 🔹 Buscar citas completas con toda la información para un odontólogo
    @Query("SELECT c FROM Cita c " +
           "JOIN FETCH c.paciente p " +
           "JOIN FETCH p.usuario " +
           "JOIN FETCH c.consultorio " +
           "JOIN FETCH c.servicio " +
           "WHERE c.odontologo = :odontologo " +
           "ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findCitasCompletasPorOdontologo(@Param("odontologo") Odontologo odontologo);

    // 🔹 Estadísticas mensuales para un odontólogo
    @Query("SELECT c.estado, COUNT(c) FROM Cita c WHERE " +
           "c.odontologo = :odontologo AND " +
           "FUNCTION('YEAR', c.fecha) = :year AND " +
           "FUNCTION('MONTH', c.fecha) = :month " +
           "GROUP BY c.estado")
    List<Object[]> findEstadisticasMensualesPorOdontologo(
            @Param("odontologo") Odontologo odontologo,
            @Param("year") int year,
            @Param("month") int month);
    
    Long countByOdontologoAndFecha(Odontologo odontologo, LocalDate fecha);
    Long countByOdontologoAndEstadoIn(Odontologo odontologo, List<String> estados);
    Long countByOdontologoAndEstado(Odontologo odontologo, String estado);

}