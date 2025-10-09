package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import com.clinicadental.gestioncitas.services.PacienteService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService, PacienteRepository pacienteRepository) {
        this.pacienteService = pacienteService;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", pacienteService.listarPacientes());
        model.addAttribute("paciente", new Paciente());
        return "admin/pacientes";
    }

    @PostMapping("/guardar")
    public String guardarPaciente(
            @ModelAttribute Paciente paciente,
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String historialClinico
    ) {
        pacienteService.guardarPaciente(paciente, usuario, fechaNacimiento, historialClinico);
        return "redirect:/admin/pacientes";
    }

    @GetMapping("/editar/{id}")
    public String editarPaciente(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.obtenerPorId(id);
        model.addAttribute("paciente", paciente);
        model.addAttribute("pacientes", pacienteService.listarPacientes());
        return "admin/pacientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return "redirect:/admin/pacientes";
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        return pacienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/editar/{id}")
    public String actualizarPaciente(
            @PathVariable Long id,
            @ModelAttribute Paciente paciente,
            @ModelAttribute Usuario usuario,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) String historialClinico
    ) {
        pacienteService.actualizarPaciente(id, paciente, usuario, fechaNacimiento, historialClinico);
        return "redirect:/admin/pacientes";
    }


}
