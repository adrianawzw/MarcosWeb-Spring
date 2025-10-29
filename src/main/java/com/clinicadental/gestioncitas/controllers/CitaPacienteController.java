package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Cita;
import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.services.CitaService;
import com.clinicadental.gestioncitas.services.PacienteService;
import com.clinicadental.gestioncitas.services.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/paciente")
public class CitaPacienteController {

    private final CitaService citaService;
    private final PacienteService pacienteService;
    private final PdfService pdfService;

    public CitaPacienteController(CitaService citaService, 
                                 PacienteService pacienteService,
                                 PdfService pdfService) {
        this.citaService = citaService;
        this.pacienteService = pacienteService;
        this.pdfService = pdfService;
    }

    // 🔹 Obtener paciente autenticado
    private Paciente obtenerPacienteAutenticado(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        String email = principal.getName();
        Optional<Paciente> pacienteOpt = pacienteService.buscarPorCorreo(email);
        
        return pacienteOpt.orElseThrow(() -> 
            new RuntimeException("Paciente no encontrado para el usuario: " + email));
    }

    // 🔹 Página 1: CITAS DISPONIBLES para reservar
    @GetMapping("/citas-disponibles")
    public String citasDisponibles(@RequestParam(required = false) String fecha,
                                   Principal principal,
                                   Model model) {
        try {
            Paciente paciente = obtenerPacienteAutenticado(principal);
            List<Cita> citasDisponibles;
            
            if (fecha != null && !fecha.isEmpty()) {
                LocalDate fechaBusqueda = LocalDate.parse(fecha);
                citasDisponibles = citaService.obtenerCitasDisponiblesPorFecha(fechaBusqueda);
            } else {
                citasDisponibles = citaService.obtenerCitasDisponibles();
            }
            
            // Información del límite de citas
            int citasActuales = citaService.contarCitasPaciente(paciente.getIdPaciente());
            boolean puedeReservar = citaService.puedeReservarMasCitas(paciente.getIdPaciente());
            
            model.addAttribute("citasDisponibles", citasDisponibles);
            model.addAttribute("paciente", paciente);
            model.addAttribute("fechaBusqueda", fecha);
            model.addAttribute("citasActuales", citasActuales);
            model.addAttribute("puedeReservar", puedeReservar);
            
            return "paciente/citas-disponibles";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "paciente/citas-disponibles";
        }
    }

    // 🔹 Página 2: MIS CITAS (las que ya reservó)
    @GetMapping("/mis-citas")
    public String misCitas(Principal principal, Model model) {
        try {
            Paciente paciente = obtenerPacienteAutenticado(principal);
            List<Cita> misCitas = citaService.obtenerCitasPorPaciente(paciente.getIdPaciente());
            
            model.addAttribute("misCitas", misCitas);
            model.addAttribute("paciente", paciente);
            
            return "paciente/mis-citas";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar mis citas: " + e.getMessage());
            return "paciente/mis-citas";
        }
    }

    // 🔹 Confirmar y reservar cita (con generación de PDF)
    @PostMapping("/reservar-cita")
    public String reservarCita(@RequestParam Long idCita, 
                              Principal principal,
                              Model model) {
        try {
            Paciente paciente = obtenerPacienteAutenticado(principal);
            
            // Reservar la cita
            Cita citaReservada = citaService.reservarCita(idCita, paciente.getIdPaciente());
            
            // Generar PDF automáticamente después de reservar
            byte[] pdfBytes = pdfService.generarComprobanteCita(citaReservada);
            
            // Preparar el PDF para descarga
            model.addAttribute("pdfBytes", pdfBytes);
            model.addAttribute("cita", citaReservada);
            model.addAttribute("paciente", paciente);
            
            return "paciente/reserva-exitosa";
            
        } catch (Exception e) {
            return "redirect:/paciente/citas-disponibles?error=" + e.getMessage();
        }
    }

    // 🔹 Endpoint para descargar PDF
    @GetMapping("/descargar-pdf/{idCita}")
    public ResponseEntity<byte[]> descargarPdfCita(@PathVariable Long idCita, Principal principal) {
        try {
            Paciente paciente = obtenerPacienteAutenticado(principal);
            
            // Verificar que la cita pertenezca al paciente
            List<Cita> misCitas = citaService.obtenerCitasPorPaciente(paciente.getIdPaciente());
            Cita cita = misCitas.stream()
                    .filter(c -> c.getIdCita().equals(idCita))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            byte[] pdfBytes = pdfService.generarComprobanteCita(cita);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=comprobante-cita-" + cita.getIdCita() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }
    }

    // 🔹 Cancelar cita
    @PostMapping("/cancelar-cita/{idCita}")
    public String cancelarCita(@PathVariable Long idCita, Principal principal) {
        try {
            Paciente paciente = obtenerPacienteAutenticado(principal);
            citaService.cancelarCita(idCita, paciente.getIdPaciente());
            
            return "redirect:/paciente/mis-citas?success=cancelacion_exitosa";
            
        } catch (Exception e) {
            return "redirect:/paciente/mis-citas?error=" + e.getMessage();
        }
    }
}