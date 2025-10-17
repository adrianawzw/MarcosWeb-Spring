package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import com.clinicadental.gestioncitas.services.PacienteService;
import com.clinicadental.gestioncitas.services.UsuarioService;

import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listarPacientes(@RequestParam(required = false) String buscar, Model model) {
        List<Paciente> pacientes;
        
        if (buscar != null && !buscar.trim().isEmpty()) {
            pacientes = pacienteService.buscarPorTermino(buscar);
        } else {
            pacientes = pacienteService.listarPacientes(); // ✅ Usando el método del service
        }
        
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("buscar", buscar);
        return "admin/pacientes";
    }

    @PostMapping("/guardar")
    public String guardarPaciente(
            @RequestParam(required = false) Long idPaciente,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String password,
            @RequestParam String fechaNacimiento,
            @RequestParam String historialClinico) {
        
        try {
            // Crear usuario con los datos del formulario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuario.setPassword(password);
            usuario.setRol("PACIENTE");

            // Crear paciente
            Paciente paciente = new Paciente();
            paciente.setIdPaciente(idPaciente); // ✅ Será null para creación, tendrá valor para edición
            
            pacienteService.guardarPaciente(paciente, usuario, fechaNacimiento, historialClinico);
            
            return "redirect:/admin/pacientes?success=true";
        } catch (Exception e) {
            // ✅ Manejar el error sin caracteres especiales en la URL
            return "redirect:/admin/pacientes?error=operation_failed";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        try {
            pacienteService.eliminarPaciente(id); // ✅ Usando el método del service
            return "redirect:/admin/pacientes?success=deleted";
        } catch (Exception e) {
            return "redirect:/admin/pacientes?error=delete_failed";
        }
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        try {
            // ✅ CORRECTO: Usando el método del service
            Paciente paciente = pacienteService.obtenerPorId(id);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ❌ ELIMINA este método si lo tienes (está duplicado)
    // @GetMapping("/editar/{id}")
    // public String editarPaciente(@PathVariable Long id, Model model) {
    //     Paciente paciente = pacienteService.obtenerPorId(id); // Esto está bien
    //     model.addAttribute("paciente", paciente);
    //     model.addAttribute("pacientes", pacienteService.listarPacientes());
    //     return "admin/pacientes";
    // }

    // ❌ ELIMINA este método también (ya no es necesario)
    // @PostMapping("/editar/{id}")
    // public String actualizarPaciente(...) {
    //     // Este método ya no es necesario, usamos /guardar para ambos
    // }
}