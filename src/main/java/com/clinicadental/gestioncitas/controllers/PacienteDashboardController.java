package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Cita;
import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.services.CitaService;
import com.clinicadental.gestioncitas.services.PacienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/paciente")
public class PacienteDashboardController {

    private final PacienteService pacienteService;
    private final CitaService citaService;

    public PacienteDashboardController(PacienteService pacienteService, CitaService citaService) {
        this.pacienteService = pacienteService;
        this.citaService = citaService;
    }

    // 🔹 Dashboard principal del paciente
    @GetMapping("/dashboard")
    public String dashboardPaciente(Principal principal, Model model) {
    	try {
            String email = principal.getName();
            Optional<Paciente> pacienteOpt = pacienteService.buscarPorCorreo(email);
            
            if (pacienteOpt.isPresent()) {
                Paciente paciente = pacienteOpt.get();
                
                // Obtener información para el dashboard
                int citasActivas = citaService.contarCitasPaciente(paciente.getIdPaciente());
                boolean puedeReservar = citaService.puedeReservarMasCitas(paciente.getIdPaciente());
                
                // 🔹 OBTENER PRÓXIMA CITA
                Optional<Cita> proximaCita = obtenerProximaCita(paciente.getIdPaciente());
                
                model.addAttribute("paciente", paciente);
                model.addAttribute("citasActivas", citasActivas);
                model.addAttribute("puedeReservar", puedeReservar);
                model.addAttribute("citasDisponiblesCount", citaService.obtenerCitasDisponibles().size());
                model.addAttribute("proximaCita", proximaCita.orElse(null));
            }
            
            return "dashboard"; // Tu archivo dashboard.html actual
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "dashboard";
        }
    }

    // 🔹 Perfil del paciente (opcional)
    @GetMapping("/perfil")
    public String perfilPaciente(Principal principal, Model model) {
        try {
            String email = principal.getName();
            Optional<Paciente> pacienteOpt = pacienteService.buscarPorCorreo(email);
            
            if (pacienteOpt.isPresent()) {
                model.addAttribute("paciente", pacienteOpt.get());
            }
            
            return "paciente/perfil";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "paciente/perfil";
        }
    }
    
 // 🔹 Método para obtener la próxima cita más cercana
    private Optional<Cita> obtenerProximaCita(Long idPaciente) {
        List<Cita> citasPaciente = citaService.obtenerCitasPorPaciente(idPaciente);
        
        LocalDate hoy = LocalDate.now();
        
        return citasPaciente.stream()
                .filter(cita -> cita.getFecha().isAfter(hoy.minusDays(1))) // Incluye hoy y futuras
                .min(Comparator.comparing(Cita::getFecha)
                        .thenComparing(Cita::getHoraInicio));
    }
}