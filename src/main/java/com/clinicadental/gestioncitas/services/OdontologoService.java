package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Odontologo;
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
    
    public Optional<Odontologo> obtenerPorId(Long id) {
        return odontologoRepository.findById(id);
    }
    
    public Optional<Odontologo> buscarPorId(Long id) {
        return odontologoRepository.findById(id);
    }


}
