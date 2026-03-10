package com.inventario.model;

import java.sql.Date;

public class Inventario {
    private int idInventario;
    private int idNegocio;
    private Date fechaInicio; // RESTAURADO
    private String tipoControl;
    private String estado;

    public Inventario() {
    }

    public Inventario(int idInventario, int idNegocio, Date fechaInicio, String tipoControl, String estado) {
        this.idInventario = idInventario;
        this.idNegocio = idNegocio;
        this.fechaInicio = fechaInicio;
        this.tipoControl = tipoControl;
        this.estado = estado;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getTipoControl() {
        return tipoControl;
    }

    public void setTipoControl(String tipoControl) {
        this.tipoControl = tipoControl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
