package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("error", null);
        return "login";
    }

    // POST login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, @RequestParam String password, Model model) {
        return usuarioService.login(correo, password)
        		.map(u -> "redirect:/auth/dashboard")
                .orElseGet(() -> {
                    model.addAttribute("error", "Credenciales inválidas");
                    return "login";
                });
    }

    // GET registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // POST registro
    @PostMapping("/registro")
    public String procesarRegistro(
            @ModelAttribute Usuario usuario,
            @RequestParam("fechaNacimiento") String fechaNacimiento,
            @RequestParam("historialClinico") String historialClinico,
            RedirectAttributes redirectAttributes) {

        usuario.setRol("PACIENTE"); // por defecto
        usuarioService.registrarUsuarioConPaciente(usuario, fechaNacimiento, historialClinico);
        
        redirectAttributes.addFlashAttribute("mensaje", "✅ Cuenta creada con éxito");

        return "redirect:/auth/login";
    }

 // dashboard en /auth/dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

}