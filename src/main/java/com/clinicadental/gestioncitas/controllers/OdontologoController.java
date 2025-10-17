package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.services.OdontologoService;
import com.clinicadental.gestioncitas.services.UsuarioService;

import java.util.List; // Import necesario para List
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/odontologos")
public class OdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    @Autowired
    private UsuarioService usuarioService;

    // --- MÉTODO LISTAR/BUSCAR ACTUALIZADO ---
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Odontologo> odontologos;

        if (buscar != null && !buscar.trim().isEmpty()) {
            // Lógica de búsqueda: Llama a un nuevo método en el servicio.
            // **IMPORTANTE**: Debes implementar 'buscarPorTermino' en OdontologoService.
            odontologos = odontologoService.buscarPorTermino(buscar);
        } else {
            // Si no hay parámetro de búsqueda, lista todos.
            odontologos = odontologoService.listar();
        }

        model.addAttribute("odontologos", odontologos);
        // Agregamos el término de búsqueda al modelo para que se mantenga en el input del HTML
        model.addAttribute("buscar", buscar); 
        return "admin/odontologos";
    }
    // ----------------------------------------

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
        // ➕ MODO CREACIÓN (sin cambios)
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

        Odontologo odontologo = new Odontologo();
        odontologo.setUsuario(nuevoUsuario);
        odontologo.setEspecialidad(especialidad);
        odontologo.setNroColegiatura(nroColegiatura);
        odontologo.setTelefonoConsulta(telefonoConsulta);

        odontologoService.guardar(odontologo);

        return "redirect:/admin/odontologos";
    }

    @PostMapping("/editar")
    public String editar(
            @RequestParam Long idOdontologo,  // ✅ Ahora es requerido para edición
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
        // 🔄 MODO EDICIÓN
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

            odontologo.setEspecialidad(especialidad);
            odontologo.setNroColegiatura(nroColegiatura);
            odontologo.setTelefonoConsulta(telefonoConsulta);

            odontologoService.guardar(odontologo);
        }

        return "redirect:/admin/odontologos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        odontologoService.eliminar(id);
        return "redirect:/admin/odontologos";
    }
    
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
