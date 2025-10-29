package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Paciente;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioService usuarioService;

    public PacienteService(PacienteRepository pacienteRepository, UsuarioService usuarioService) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public void guardarPaciente(Paciente paciente, Usuario usuario, String fechaNacimiento, String historialClinico) {
        // ✅ VERIFICACIÓN MÁS ROBUSTA: Usar idPaciente Y usuario.idUsuario
        boolean esCreacion = (paciente.getIdPaciente() == null);
        
        if (esCreacion) {
        	guardarNuevoPaciente(paciente, usuario, fechaNacimiento, historialClinico);
        } else {
            // 🔄 MODO EDICIÓN
            //actualizarPaciente(paciente.getIdPaciente(), usuario, fechaNacimiento, historialClinico);
        	actualizarPacienteExistente(paciente.getIdPaciente(), usuario, fechaNacimiento, historialClinico);
        }
    }

    @Transactional
    private void guardarNuevoPaciente(Paciente paciente, Usuario usuario, String fechaNacimiento, String historialClinico) {
        // ✅ Verificar si ya existe un paciente con el mismo DNI
        if (pacienteRepository.existsByDni(usuario.getDni())) {
            throw new RuntimeException("Ya existe un paciente con el DNI: " + usuario.getDni());
        }
        
        // ✅ Verificar si ya existe un paciente con el mismo correo
        if (pacienteRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un paciente con el correo: " + usuario.getCorreo());
        }
        
        // ✅ Crear NUEVO usuario
        usuario.setRol("PACIENTE");
        Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);
        
        // Configurar paciente
        paciente.setUsuario(usuarioGuardado);
        
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            paciente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        }
        
        if (historialClinico != null && !historialClinico.isEmpty()) {
            paciente.setHistorialClinico(historialClinico);
        }
        
        pacienteRepository.save(paciente);
    }
    
    @Transactional
    public void actualizarPacienteExistente(Long idPaciente, Usuario datosUsuario, 
                                           String fechaNacimiento, String historialClinico) {
        Paciente pacienteExistente = obtenerPorId(idPaciente);
        Usuario usuarioExistente = pacienteExistente.getUsuario();

        // ✅ Verificar DNI único (solo si cambió)
        if (!usuarioExistente.getDni().equals(datosUsuario.getDni())) {
            Optional<Paciente> pacienteConMismoDni = pacienteRepository.findByUsuarioDni(datosUsuario.getDni());
            if (pacienteConMismoDni.isPresent() && 
                !pacienteConMismoDni.get().getIdPaciente().equals(idPaciente)) {
                throw new RuntimeException("Ya existe otro paciente con el DNI: " + datosUsuario.getDni());
            }
        }

        // ✅ Verificar correo único (solo si cambió)
        if (!usuarioExistente.getCorreo().equals(datosUsuario.getCorreo())) {
            Optional<Paciente> pacienteConMismoCorreo = pacienteRepository.findByUsuarioCorreo(datosUsuario.getCorreo());
            if (pacienteConMismoCorreo.isPresent() && 
                !pacienteConMismoCorreo.get().getIdPaciente().equals(idPaciente)) {
                throw new RuntimeException("Ya existe otro paciente con el correo: " + datosUsuario.getCorreo());
            }
        }

        // ✅ Actualizar datos del usuario
        usuarioExistente.setNombre(datosUsuario.getNombre());
        usuarioExistente.setApellido(datosUsuario.getApellido());
        usuarioExistente.setDni(datosUsuario.getDni());
        usuarioExistente.setCorreo(datosUsuario.getCorreo());
        usuarioExistente.setTelefono(datosUsuario.getTelefono());
        usuarioExistente.setDireccion(datosUsuario.getDireccion());

        // ✅ Actualizar paciente
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            pacienteExistente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        }
        
        if (historialClinico != null) {
            pacienteExistente.setHistorialClinico(historialClinico);
        }

        // ✅ Guardar cambios
        usuarioService.registrarUsuario(usuarioExistente);
        pacienteRepository.save(pacienteExistente);
    }
    
    // ✅ Método para buscar por término (mejorado)
    public List<Paciente> buscarPorTermino(String termino) {
        return pacienteRepository.buscarPorTermino(termino);
    }

    // ✅ Método para buscar por DNI
    public Optional<Paciente> buscarPorDni(String dni) {
        return pacienteRepository.findByUsuarioDni(dni);
    }

    // ✅ Método para buscar por email
    public Optional<Paciente> buscarPorCorreo(String correo) {
        return pacienteRepository.findByUsuarioCorreo(correo);
    }

    // ✅ Método para verificar existencia por DNI
    public boolean existePorDni(String dni) {
        return pacienteRepository.existsByDni(dni);
    }

    // ✅ Método para verificar existencia por correo
    public boolean existePorCorreo(String correo) {
        return pacienteRepository.existsByCorreo(correo);
    }

    // ... otros métodos básicos
    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }
}