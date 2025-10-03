package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import com.clinicadental.gestioncitas.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder; // 👈 inyectamos el encoder

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PacienteRepository pacienteRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // 🔑 antes de guardar, encriptamos la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        if ("PACIENTE".equalsIgnoreCase(nuevoUsuario.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setUsuario(nuevoUsuario);
            pacienteRepository.save(paciente);
        }

        return nuevoUsuario;
    }

    public Optional<Usuario> login(String correo, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // usamos el encoder para comparar la contraseña encriptada
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Usuario registrarUsuarioConPaciente(Usuario usuario, String fechaNacimiento, String historialClinico) {
        // encriptamos aquí también
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        if ("PACIENTE".equalsIgnoreCase(nuevoUsuario.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setUsuario(nuevoUsuario);

            if (historialClinico == null || historialClinico.isBlank()) {
                paciente.setHistorialClinico(null);
            } else {
                paciente.setHistorialClinico(historialClinico);
            }

            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                paciente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }

            pacienteRepository.save(paciente);
        }

        return nuevoUsuario;
    }
}
