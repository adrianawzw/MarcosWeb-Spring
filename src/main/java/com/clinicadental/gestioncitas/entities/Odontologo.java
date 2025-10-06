package com.clinicadental.gestioncitas.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "odontologo")
public class Odontologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_odontologo")
    private Long idOdontologo;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    private String especialidad;

    @Column(name = "nro_colegiatura")
    private String nroColegiatura;

    @Column(name = "telefono_consulta")
    private String telefonoConsulta;

    // Getters y Setters
    public Long getIdOdontologo() { return idOdontologo; }
    public void setIdOdontologo(Long idOdontologo) { this.idOdontologo = idOdontologo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getNroColegiatura() { return nroColegiatura; }
    public void setNroColegiatura(String nroColegiatura) { this.nroColegiatura = nroColegiatura; }

    public String getTelefonoConsulta() { return telefonoConsulta; }
    public void setTelefonoConsulta(String telefonoConsulta) { this.telefonoConsulta = telefonoConsulta; }
}
