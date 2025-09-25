package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import com.clinicadental.gestioncitas.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // primero guardamos el usuario
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        // si es paciente, creamos el registro en la tabla paciente
        if ("paciente".equalsIgnoreCase(nuevoUsuario.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setUsuario(nuevoUsuario);
            pacienteRepository.save(paciente);
        }

        return nuevoUsuario;
    }

    public Optional<Usuario> login(String correo, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);

        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario; // credenciales correctas
        }

        return Optional.empty(); // credenciales inválidas
    }
    
    @Transactional
    public Usuario registrarUsuarioConPaciente(Usuario usuario, String fechaNacimiento, String historialClinico) {
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        if ("paciente".equalsIgnoreCase(nuevoUsuario.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setUsuario(nuevoUsuario);
            paciente.setHistorialClinico(historialClinico);

            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                paciente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }

            pacienteRepository.save(paciente);
        }

        return nuevoUsuario;
    }

}