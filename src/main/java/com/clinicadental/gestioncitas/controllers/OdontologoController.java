package com.clinicadental.gestioncitas.controllers;

import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.services.OdontologoService;
import com.clinicadental.gestioncitas.services.UsuarioService;

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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("odontologos", odontologoService.listar());
        return "admin/odontologos";
    }

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
        // 1️⃣ Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setPassword(password);
        usuario.setRol("ODONTOLOGO");

        // 2️⃣ Registrar el usuario (usa tu método que ya encripta y guarda)
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);

        // 3️⃣ Crear odontólogo
        Odontologo odontologo = new Odontologo();
        odontologo.setUsuario(nuevoUsuario);
        odontologo.setEspecialidad(especialidad);
        odontologo.setNroColegiatura(nroColegiatura);
        odontologo.setTelefonoConsulta(telefonoConsulta);

        odontologoService.guardar(odontologo);

        return "redirect:/admin/odontologos";
    }

    @PostMapping("/editar/{id}")
    public String editar(
    		@PathVariable Long id,
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
        // ✅ Corregido: manejar Optional<Odontologo>
        Optional<Odontologo> optionalOdontologo = odontologoService.buscarPorId(id);

        if (optionalOdontologo.isPresent()) {
            Odontologo odontologo = optionalOdontologo.get();
            Usuario usuario = odontologo.getUsuario();

            // 🔄 Actualizamos datos del usuario
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);

            if (password != null && !password.isBlank()) {
                usuario.setPassword(password);
            }

            // Reutilizamos el método de usuarioService (encripta y guarda)
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
