package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return User.withUsername(usuario.getCorreo())
                   .password(usuario.getPassword()) // debe estar encriptada con BCrypt
                   .roles(usuario.getRol()) // ADMIN, PACIENTE, ODONTOLOGO
                   .build();
    }
}
