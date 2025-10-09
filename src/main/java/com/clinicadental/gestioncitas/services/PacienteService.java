package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import com.clinicadental.gestioncitas.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public PacienteService(PacienteRepository pacienteRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioService usuarioService) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    @Transactional
    public void guardarPaciente(Paciente paciente, Usuario usuario, String fechaNacimiento, String historialClinico) {
        if (usuario.getIdUsuario() == null) {
            // Crear nuevo usuario con rol PACIENTE
            usuario.setRol("PACIENTE");
            usuarioService.registrarUsuarioConPaciente(usuario, fechaNacimiento, historialClinico);
        } else {
            // Actualizar datos del usuario existente
            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            existente.setNombre(usuario.getNombre());
            existente.setApellido(usuario.getApellido());
            existente.setCorreo(usuario.getCorreo());
            existente.setTelefono(usuario.getTelefono());
            existente.setDireccion(usuario.getDireccion());
            existente.setDni(usuario.getDni());
            usuarioRepository.save(existente);

            // Actualizar datos del paciente
            Paciente existentePaciente = pacienteRepository.findByUsuario(existente)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            existentePaciente.setHistorialClinico(historialClinico);
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                existentePaciente.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimiento));
            }
            pacienteRepository.save(existentePaciente);
        }
    }

    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }
    
    @Transactional
    public void actualizarPaciente(Long id, Paciente datosPaciente, Usuario datosUsuario, 
                                   String fechaNacimiento, String historialClinico) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuarioExistente = pacienteExistente.getUsuario();

        // 🔹 Actualizamos los campos del usuario
        usuarioExistente.setNombre(datosUsuario.getNombre());
        usuarioExistente.setApellido(datosUsuario.getApellido());
        usuarioExistente.setDni(datosUsuario.getDni());
        usuarioExistente.setCorreo(datosUsuario.getCorreo());
        usuarioExistente.setTelefono(datosUsuario.getTelefono());
        usuarioExistente.setDireccion(datosUsuario.getDireccion());

        // 🔹 Convertir la fecha recibida a LocalDate
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaNacimiento, formatter);
            pacienteExistente.setFechaNacimiento(fecha);
        }

        // 🔹 Actualizamos el historial clínico
        pacienteExistente.setHistorialClinico(historialClinico);

        // 🔹 Guardamos los cambios
        pacienteRepository.save(pacienteExistente);
    }

}
