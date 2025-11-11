package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Cita;
import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.services.CitaService;
import com.clinicadental.gestioncitas.services.OdontologoService;
import com.clinicadental.gestioncitas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/odontologo")
public class OdontologoController {

    private final CitaService citaService;
    private final OdontologoService odontologoService;
    private final UsuarioService usuarioService;

    @Autowired
    public OdontologoController(CitaService citaService, OdontologoService odontologoService, UsuarioService usuarioService) {
        this.citaService = citaService;
        this.odontologoService = odontologoService;
        this.usuarioService = usuarioService;
    }

    // --- MÉTODO AUXILIAR PARA OBTENER EL ODONTÓLOGO LOGUEADO ---
    private Odontologo obtenerOdontologoLogueado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new RuntimeException("Usuario no autenticado");
            }
            
            String username = auth.getName();
            System.out.println("🔍 Usuario autenticado: " + username);
            
            Usuario usuario = usuarioService.findByCorreo(username);
            System.out.println("🔍 Usuario encontrado: " + usuario.getNombre());
            
            return odontologoService.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado para el usuario: " + username));
                    
        } catch (Exception e) {
            System.err.println("❌ Error en obtenerOdontologoLogueado: " + e.getMessage());
            throw new RuntimeException("Error al obtener información del odontólogo: " + e.getMessage());
        }
    }

    // --- VER TODAS LAS CITAS DEL ODONTÓLOGO ---
    @GetMapping("/mis-citas")
    public String verMisCitas(Model model) {
        try {
            System.out.println("🚀 Accediendo a /odontologo/mis-citas");
            Odontologo odontologo = obtenerOdontologoLogueado();
            List<Cita> citas = citaService.obtenerCitasPorOdontologo(odontologo);
            
            System.out.println("📊 Citas encontradas: " + citas.size());
            
            // Contar estadísticas con los NUEVOS ESTADOS
            long citasHoy = citas.stream()
                    .filter(c -> c.getFecha().equals(LocalDate.now()))
                    .count();
            long citasReservadas = citas.stream()
                    .filter(c -> "RESERVADA".equals(c.getEstado()))
                    .count();
            long citasCompletadas = citas.stream()
                    .filter(c -> "COMPLETADA".equals(c.getEstado()))
                    .count();
            
            model.addAttribute("citas", citas);
            model.addAttribute("odontologo", odontologo);
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("citasReservadas", citasReservadas);
            model.addAttribute("citasCompletadas", citasCompletadas);
            
            return "odontologo/mis-citas";
            
        } catch (Exception e) {
            System.err.println("❌ Error en verMisCitas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "error";
        }
    }

    // --- NUEVO: VER CITAS PENDIENTES (RESERVADAS + CONFIRMADAS) ---
    @GetMapping("/citas-pendientes")
    public String verCitasPendientes(Model model) {
        try {
            System.out.println("🚀 Accediendo a /odontologo/citas-pendientes");
            Odontologo odontologo = obtenerOdontologoLogueado();
            
            // Obtener citas reservadas y confirmadas
            List<Cita> citasReservadas = citaService.obtenerCitasPorEstadoYOdontologo(odontologo, "RESERVADA");
            List<Cita> citasConfirmadas = citaService.obtenerCitasPorEstadoYOdontologo(odontologo, "CONFIRMADA");
            
            // Combinar ambas listas
            List<Cita> citasPendientes = citasReservadas;
            citasPendientes.addAll(citasConfirmadas);
            
            // Estadísticas
            long totalReservadas = citasReservadas.size();
            long totalConfirmadas = citasConfirmadas.size();
            long citasProximaSemana = citasPendientes.stream()
                    .filter(c -> c.getFecha().isBefore(LocalDate.now().plusDays(7)))
                    .count();
            
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("totalReservadas", totalReservadas);
            model.addAttribute("totalConfirmadas", totalConfirmadas);
            model.addAttribute("citasProximaSemana", citasProximaSemana);
            
            return "odontologo/citas-pendientes";
            
        } catch (Exception e) {
            System.err.println("❌ Error en verCitasPendientes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar citas pendientes: " + e.getMessage());
            return "error";
        }
    }

    // --- NUEVO: VER CITAS DE HOY (VISTA ESPECÍFICA) ---
    @GetMapping("/citas-hoy")
    public String verCitasHoy(Model model) {
        try {
            System.out.println("🚀 Accediendo a /odontologo/citas-hoy");
            Odontologo odontologo = obtenerOdontologoLogueado();
            List<Cita> citasHoy = citaService.obtenerCitasHoyPorOdontologo(odontologo);
            
            // Estadísticas para hoy
            long totalCitasHoy = citasHoy.size();
            long citasConfirmadasHoy = citasHoy.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();
            long citasReservadasHoy = citasHoy.stream()
                    .filter(c -> "RESERVADA".equals(c.getEstado()))
                    .count();
            
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("totalCitasHoy", totalCitasHoy);
            model.addAttribute("citasConfirmadasHoy", citasConfirmadasHoy);
            model.addAttribute("citasReservadasHoy", citasReservadasHoy);
            
            return "odontologo/citas-hoy";
            
        } catch (Exception e) {
            System.err.println("❌ Error en verCitasHoy: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar citas de hoy: " + e.getMessage());
            return "error";
        }
    }

    // --- NUEVO: VER CITAS CONFIRMADAS ---
    @GetMapping("/citas-confirmadas")
    public String verCitasConfirmadas(Model model) {
        try {
            System.out.println("🚀 Accediendo a /odontologo/citas-confirmadas");
            Odontologo odontologo = obtenerOdontologoLogueado();
            List<Cita> citasConfirmadas = citaService.obtenerCitasPorEstadoYOdontologo(odontologo, "CONFIRMADA");
            
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            
            return "odontologo/citas-confirmadas";
            
        } catch (Exception e) {
            System.err.println("❌ Error en verCitasConfirmadas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar citas confirmadas: " + e.getMessage());
            return "error";
        }
    }

    // --- VER CITAS RESERVADAS ---
    @GetMapping("/citas-reservadas")
    public String verCitasReservadas(Model model) {
        try {
            Odontologo odontologo = obtenerOdontologoLogueado();
            List<Cita> citasReservadas = citaService.obtenerCitasPorEstadoYOdontologo(odontologo, "RESERVADA");
            
            model.addAttribute("citas", citasReservadas);
            model.addAttribute("odontologo", odontologo);
            model.addAttribute("titulo", "Citas Reservadas");
            return "odontologo/mis-citas"; // Reutilizar la misma vista
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar citas reservadas: " + e.getMessage());
            return "error";
        }
    }

    // --- CONFIRMAR CITA (Cambiar de RESERVADA a CONFIRMADA) ---
    @PostMapping("/cita/{id}/confirmar")
    public String confirmarCita(@PathVariable Long id) {
        try {
            citaService.confirmarCita(id);
            return "redirect:/odontologo/mis-citas?success=confirmada";
        } catch (Exception e) {
            return "redirect:/odontologo/mis-citas?error=confirmacion_fallida";
        }
    }

    // --- COMPLETAR CITA ---
    @PostMapping("/cita/{id}/completar")
    public String completarCita(@PathVariable Long id) {
        try {
            citaService.completarCita(id);
            return "redirect:/odontologo/mis-citas?success=completada";
        } catch (Exception e) {
            return "redirect:/odontologo/mis-citas?error=completacion_fallida";
        }
    }

    // --- CANCELAR CITA ---
    @PostMapping("/cita/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, @RequestParam String motivo) {
        try {
            citaService.cancelarCitaOdontologo(id, motivo);
            return "redirect:/odontologo/mis-citas?success=cancelada";
        } catch (Exception e) {
            return "redirect:/odontologo/mis-citas?error=cancelacion_fallida";
        }
    }
}