package com.clinicadental.gestioncitas.dto;

import java.time.LocalDate;

import com.clinicadental.gestioncitas.entities.Usuario;

public class RegistroPacienteDTO {
    private Usuario usuario;
    private LocalDate fechaNacimiento;
    private String historialClinico;

    // Getters y Setters
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getHistorialClinico() {
        return historialClinico;
    }
    public void setHistorialClinico(String historialClinico) {
        this.historialClinico = historialClinico;
    }
}
