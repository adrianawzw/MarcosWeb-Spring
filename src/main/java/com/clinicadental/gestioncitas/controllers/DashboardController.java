package com.clinicadental.gestioncitas.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private OdontologoService odontologoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Verificar el rol del usuario
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            boolean isOdontologo = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ODONTOLOGO"));
            boolean isPaciente = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PACIENTE"));
            
            // Si es odontólogo, cargar estadísticas específicas
            if (isOdontologo) {
                Usuario usuario = usuarioService.findByCorreo(username);
                Odontologo odontologo = odontologoService.findByUsuario(usuario)
                        .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
                
                // Obtener estadísticas
                Long citasHoy = citaService.contarCitasHoyPorOdontologo(odontologo);
                Long citasPendientes = citaService.contarCitasPendientesPorOdontologo(odontologo);
                Long citasConfirmadas = citaService.contarCitasPorEstadoYOdontologo(odontologo, "CONFIRMADA");
                
                model.addAttribute("citasHoy", citasHoy);
                model.addAttribute("citasPendientes", citasPendientes);
                model.addAttribute("citasConfirmadas", citasConfirmadas);
            }
            
            return "dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "error";
        }
    }
}