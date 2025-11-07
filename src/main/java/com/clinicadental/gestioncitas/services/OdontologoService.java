package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Odontologo;
import com.clinicadental.gestioncitas.entities.Usuario;
import com.clinicadental.gestioncitas.repositories.OdontologoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<Odontologo> listar() {
        return odontologoRepository.findAll();
    }

    public void guardar(Odontologo odontologo) {
        odontologoRepository.save(odontologo);
    }

    public void eliminar(Long id) {
        odontologoRepository.deleteById(id);
    }
    
    // ✅ MANTENER SOLO UNO - eliminar el otro
    public Optional<Odontologo> buscarPorId(Long id) {
        return odontologoRepository.findById(id);
    }

    // ❌ ELIMINAR ESTE MÉTODO DUPLICADO
    // public Optional<Odontologo> obtenerPorId(Long id) {
    //     return odontologoRepository.findById(id);
    // }

    public List<Odontologo> buscarPorTermino(String termino) {
        String likeTermino = "%" + termino + "%";
        return odontologoRepository.buscarPorTermino(likeTermino);
    }
    
    public Optional<Odontologo> findByUsuario(Usuario usuario) {
        return odontologoRepository.findByUsuario(usuario);
    }
}