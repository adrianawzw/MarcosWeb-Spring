package com.clinicadental.gestioncitas.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "consultorio")
public class Consultorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consultorio")
    private Long idConsultorio;

    private String nombre;
    private String ubicacion;
    private String telefono;

    // Getters y Setters
    public Long getIdConsultorio() { return idConsultorio; }
    public void setIdConsultorio(Long idConsultorio) { this.idConsultorio = idConsultorio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
