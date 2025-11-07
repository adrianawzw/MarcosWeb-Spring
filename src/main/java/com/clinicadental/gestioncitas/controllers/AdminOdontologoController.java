package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.services.OdontologoService;
import com.clinicadental.gestioncitas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/odontologos")
public class AdminOdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    @Autowired
    private UsuarioService usuarioService;

    // --- LISTAR Y BUSCAR ODONTÓLOGOS ---
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Odontologo> odontologos;

        if (buscar != null && !buscar.trim().isEmpty()) {
            odontologos = odontologoService.buscarPorTermino(buscar);
        } else {
            odontologos = odontologoService.listar();
        }

        model.addAttribute("odontologos", odontologos);
        model.addAttribute("buscar", buscar); 
        return "admin/odontologos";
    }

    // --- CREAR NUEVO ODONTÓLOGO ---
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam String password,
            @RequestParam String especialidad,
            @RequestParam String nroColegiatura,
            @RequestParam String telefonoConsulta
    ) {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setPassword(password);
        usuario.setRol("ODONTOLOGO");

        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);

        // Crear odontólogo
        Odontologo odontologo = new Odontologo();
        odontologo.setUsuario(nuevoUsuario);
        odontologo.setEspecialidad(especialidad);
        odontologo.setNroColegiatura(nroColegiatura);
        odontologo.setTelefonoConsulta(telefonoConsulta);

        odontologoService.guardar(odontologo);

        return "redirect:/admin/odontologos";
    }

    // --- EDITAR ODONTÓLOGO ---
    @PostMapping("/editar")
    public String editar(
            @RequestParam Long idOdontologo,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam(required = false) String password,
            @RequestParam String especialidad,
            @RequestParam String nroColegiatura,
            @RequestParam String telefonoConsulta
    ) {
        Optional<Odontologo> optionalOdontologo = odontologoService.buscarPorId(idOdontologo);

        if (optionalOdontologo.isPresent()) {
            Odontologo odontologo = optionalOdontologo.get();
            Usuario usuario = odontologo.getUsuario();

            // Actualizar datos del usuario
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);

            if (password != null && !password.isBlank()) {
                usuario.setPassword(password);
            }

            usuarioService.registrarUsuario(usuario);

            // Actualizar datos del odontólogo
            odontologo.setEspecialidad(especialidad);
            odontologo.setNroColegiatura(nroColegiatura);
            odontologo.setTelefonoConsulta(telefonoConsulta);

            odontologoService.guardar(odontologo);
        }

        return "redirect:/admin/odontologos";
    }

    // --- ELIMINAR ODONTÓLOGO ---
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        odontologoService.eliminar(id);
        return "redirect:/admin/odontologos";
    }
    
    // --- API PARA OBTENER ODONTÓLOGO POR ID (para edición) ---
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Odontologo> obtenerOdontologoPorId(@PathVariable Long id) {
        Optional<Odontologo> odontologo = odontologoService.buscarPorId(id);
        if (odontologo.isPresent()) {
            return ResponseEntity.ok(odontologo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}