package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.*;
import com.clinicadental.gestioncitas.repositories.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final OdontologoRepository odontologoRepository;
    private final ConsultorioRepository consultorioRepository;
    private final ServicioRepository servicioRepository;

    public CitaController(CitaRepository citaRepository,
                          OdontologoRepository odontologoRepository,
                          ConsultorioRepository consultorioRepository,
                          ServicioRepository servicioRepository) {
        this.citaRepository = citaRepository;
        this.odontologoRepository = odontologoRepository;
        this.consultorioRepository = consultorioRepository;
        this.servicioRepository = servicioRepository;
    }

    @GetMapping
    public String listarCitas(@RequestParam(value = "buscar", required = false) String buscar, Model model) {
        List<Cita> citas;

        if (buscar != null && !buscar.isEmpty()) {
            try {
                LocalDate fecha = LocalDate.parse(buscar);
                citas = citaRepository.findByFecha(fecha);
            } catch (Exception e) {
                citas = List.of(); // formato no válido → lista vacía
            }
        } else {
            citas = citaRepository.findAll();
        }

        model.addAttribute("citas", citas);
        model.addAttribute("cita", new Cita());
        model.addAttribute("buscar", buscar);

        // combos
        model.addAttribute("odontologos", odontologoRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        model.addAttribute("servicios", servicioRepository.findAll());

        return "admin/citas";
    }

    @PostMapping("/guardar")
    public String guardarCita(@ModelAttribute Cita cita) {

        // ✅ reconstruimos las relaciones desde sus IDs
        if (cita.getOdontologo() != null && cita.getOdontologo().getIdOdontologo() != null) {
            Odontologo o = odontologoRepository.findById(cita.getOdontologo().getIdOdontologo()).orElse(null);
            cita.setOdontologo(o);
        }

        if (cita.getConsultorio() != null && cita.getConsultorio().getIdConsultorio() != null) {
            Consultorio c = consultorioRepository.findById(cita.getConsultorio().getIdConsultorio()).orElse(null);
            cita.setConsultorio(c);
        }

        if (cita.getServicio() != null && cita.getServicio().getIdServicio() != null) {
            Servicio s = servicioRepository.findById(cita.getServicio().getIdServicio()).orElse(null);
            cita.setServicio(s);
        }

        // ✅ paciente se asigna después (cuando el paciente reserva)
        cita.setPaciente(null);

        citaRepository.save(cita);
        return "redirect:/admin/citas";
    }

    @GetMapping("/editar/{id}")
    public String editarCita(@PathVariable Long id, Model model) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        model.addAttribute("citas", citaRepository.findAll());
        model.addAttribute("cita", cita);
        model.addAttribute("odontologos", odontologoRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        model.addAttribute("servicios", servicioRepository.findAll());
        model.addAttribute("editar", true);

        return "admin/citas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id) {
        citaRepository.deleteById(id);
        return "redirect:/admin/citas";
    }
}
