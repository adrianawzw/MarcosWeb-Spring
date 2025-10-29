package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Cita;
import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.repositories.CitaRepository;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;

    public CitaService(CitaRepository citaRepository, PacienteRepository pacienteRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // 🔹 Obtener citas disponibles (optimizado)
    public List<Cita> obtenerCitasDisponibles() {
        return citaRepository.findCitasDisponiblesCompletas();
    }

    // 🔹 Obtener citas disponibles por fecha
    public List<Cita> obtenerCitasDisponiblesPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaAndPacienteIsNull(fecha);
    }

    // 🔹 Verificar si paciente tiene menos de 3 citas
    public boolean puedeReservarMasCitas(Long idPaciente) {
        long count = citaRepository.countByPacienteId(idPaciente);
        return count < 3;
    }

    // 🔹 Contar citas del paciente
    public int contarCitasPaciente(Long idPaciente) {
        return (int) citaRepository.countByPacienteId(idPaciente);
    }

    // 🔹 Obtener citas de un paciente (optimizado)
    public List<Cita> obtenerCitasPorPaciente(Long idPaciente) {
        return citaRepository.findCitasCompletasPorPaciente(idPaciente);
    }

    // 🔹 Reservar cita con validación
    @Transactional
    public Cita reservarCita(Long idCita, Long idPaciente) {
        Optional<Cita> citaOpt = citaRepository.findById(idCita);
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(idPaciente);

        if (citaOpt.isPresent() && pacienteOpt.isPresent()) {
            Cita cita = citaOpt.get();
            Paciente paciente = pacienteOpt.get();
            
            // Verificar que la cita esté disponible
            if (cita.getPaciente() != null) {
                throw new RuntimeException("La cita ya está reservada");
            }

            // Verificar que no tenga más de 3 citas
            if (!puedeReservarMasCitas(idPaciente)) {
                throw new RuntimeException("Ya tienes 3 citas reservadas. No puedes reservar más.");
            }

            cita.setPaciente(paciente);
            cita.setEstado("RESERVADA");
            
            return citaRepository.save(cita);
        }
        throw new RuntimeException("Cita o paciente no encontrado");
    }

    // 🔹 Cancelar cita
    @Transactional
    public boolean cancelarCita(Long idCita, Long idPaciente) {
        Optional<Cita> citaOpt = citaRepository.findById(idCita);
        
        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();
            
            // Verificar que la cita pertenezca al paciente
            if (cita.getPaciente() != null && 
                cita.getPaciente().getIdPaciente().equals(idPaciente)) {
                
                cita.setPaciente(null);
                cita.setEstado("DISPONIBLE");
                citaRepository.save(cita);
                return true;
            }
        }
        return false;
    }

    // 🔹 Métodos adicionales útiles (CORREGIDOS)

    public List<Cita> obtenerCitasProximas() {
        LocalDate hoy = LocalDate.now();
        LocalDate enSieteDias = hoy.plusDays(7);
        return citaRepository.findCitasProximas(hoy, enSieteDias);
    }

    public List<Cita> obtenerCitasDeHoy() {
        return citaRepository.findCitasDeHoy();
    }

    public List<Cita> obtenerCitasParaManana() {
        LocalDate manana = LocalDate.now().plusDays(1);
        return citaRepository.findCitasParaManana(manana);
    }

    // 🔹 Validar conflicto de horarios (para administradores)
    public boolean existeConflictoHorario(LocalDate fecha, Long idOdontologo, 
                                         LocalTime horaInicio, LocalTime horaFin) {
        return citaRepository.existsConflictingCita(fecha, idOdontologo, horaInicio, horaFin);
    }

    public boolean existeConflictoHorarioExcluyendo(LocalDate fecha, Long idOdontologo, 
                                                   LocalTime horaInicio, LocalTime horaFin, 
                                                   Long idCitaExcluir) {
        return citaRepository.existsConflictingCitaExcluding(fecha, idOdontologo, horaInicio, horaFin, idCitaExcluir);
    }

    // 🔹 Métodos básicos del repositorio
    public List<Cita> obtenerTodasLasCitas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> obtenerCitaPorId(Long id) {
        return citaRepository.findById(id);
    }

    public Cita guardarCita(Cita cita) {
        return citaRepository.save(cita);
    }

    public void eliminarCita(Long id) {
        citaRepository.deleteById(id);
    }
    
    public Optional<Cita> obtenerProximaCitaPaciente(Long idPaciente) {
        List<Cita> citasPaciente = obtenerCitasPorPaciente(idPaciente);
        
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        
        return citasPaciente.stream()
                .filter(cita -> 
                    cita.getFecha().isAfter(hoy) || 
                    (cita.getFecha().isEqual(hoy) && cita.getHoraInicio().isAfter(ahora))
                )
                .min(Comparator.comparing(Cita::getFecha)
                        .thenComparing(Cita::getHoraInicio));
    }
}