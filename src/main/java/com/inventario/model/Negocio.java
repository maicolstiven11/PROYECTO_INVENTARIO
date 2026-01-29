package com.inventario.model;

public class Negocio {
    private int idNegocio;
    private String nombre;
    private String direccion;
    private String estado; // 'activo', 'inactivo'
    private boolean tieneInventarioActivo; // Flag para UI

    public Negocio() {
    }

    public Negocio(int idNegocio, String nombre, String direccion, String estado) {
        this.idNegocio = idNegocio;
        this.nombre = nombre;
        this.direccion = direccion;
        this.estado = estado;
    }

    public int getIdNegocio() {
        return idNegocio;
    }

    public void setIdNegocio(int idNegocio) {
        this.idNegocio = idNegocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isTieneInventarioActivo() {
        return tieneInventarioActivo;
    }

    public void setTieneInventarioActivo(boolean tieneInventarioActivo) {
        this.tieneInventarioActivo = tieneInventarioActivo;
    }
}
