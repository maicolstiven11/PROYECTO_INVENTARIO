package com.inventario.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * CLASE UTILITARIA PARA CONFIGURAR PERMISOS INICIALES.
 * EJECUTA ESTA CLASE (Click derecho -> Run File) UNA SOLA VEZ para llenar la base de datos.
 */
public class SetupPermisos {

    public static void main(String[] args) {
        System.out.println("Iniciando configuración de permisos...");
        
        Connection con = null;
        try {
            con = Conexion.getConexion();
            if(con == null) {
                System.err.println("¡ERROR! No se pudo conectar a la base de datos.");
                return;
            }
            con.setAutoCommit(false); // Transacción para seguridad
            
            // 1. INSERTAR PERMISOS (Lista Completa)
            System.out.println("Insertando permisos en tabla PERMISO...");
            
            // Gestión de Negocios
            insertarPermiso(con, "VER_BARES", "Permite ver la lista de bares asignados");
            insertarPermiso(con, "AGREGAR_NEGOCIO", "Permite registrar nuevos bares");
            insertarPermiso(con, "ELIMINAR_NEGOCIO", "Permite eliminar bares (Admin)");

            // Gestión de Usuarios
            insertarPermiso(con, "GESTIONAR_TRABAJADORES", "Permite crear y editar usuarios");
            
            // Inventario y Productos
            insertarPermiso(con, "VER_PRODUCTOS", "Permite ver el listado de productos");
            insertarPermiso(con, "AGREGAR_PRODUCTO", "Permite crear nuevos productos");
            insertarPermiso(con, "EDITAR_PRODUCTO", "Permite editar productos");
            insertarPermiso(con, "ELIMINAR_PRODUCTO", "Permite eliminar productos");
            insertarPermiso(con, "INICIAR_INVENTARIO", "Permite iniciar ciclos de inventario");
            
            // Ventas
            insertarPermiso(con, "REALIZAR_VENTA", "Permite registrar ventas");
            insertarPermiso(con, "VER_HISTORIAL_VENTAS", "Permite ver el historial completo de ventas");
            
            // Gastos
            insertarPermiso(con, "REGISTRAR_GASTO", "Permite registrar gastos");
            insertarPermiso(con, "VER_GASTOS", "Permite ver historial de gastos");
            
            // Proveedores y Pedidos
            insertarPermiso(con, "GESTIONAR_PROVEEDORES", "Permite agregar y editar proveedores");
            insertarPermiso(con, "HACER_PEDIDOS_PROVEEDOR", "Permite realizar pedidos a proveedores");
            
            // Informes
            insertarPermiso(con, "VER_INFORMES", "Permite ver informes y reportes del sistema");


            // 2. ASIGNAR PERMISOS AL ROL ADMINISTRADOR (ID=1) -> TIENE TODO
            System.out.println("Asignando TODO al Rol Administrador (ID 1)...");
            // Lista maestra de todos los permisos
            String[] listaAdmin = {
                "VER_BARES", "AGREGAR_NEGOCIO", "ELIMINAR_NEGOCIO",
                "GESTIONAR_TRABAJADORES",
                "VER_PRODUCTOS", "AGREGAR_PRODUCTO", "EDITAR_PRODUCTO", "ELIMINAR_PRODUCTO", "INICIAR_INVENTARIO",
                "REALIZAR_VENTA", "VER_HISTORIAL_VENTAS",
                "REGISTRAR_GASTO", "VER_GASTOS",
                "GESTIONAR_PROVEEDORES", "HACER_PEDIDOS_PROVEEDOR",
                "VER_INFORMES"
            };
            for(String p : listaAdmin) {
                asignarPermisoRol(con, 1, p);
            }
            
            // 3. ASIGNAR PERMISOS AL ROL TRABAJADOR (ID=2) -> SOLO OPERATIVO
            System.out.println("Asignando permisos operativos al Rol Trabajador (ID 2)...");
            String[] listaTrabajador = {
                "VER_BARES",              
                "VER_PRODUCTOS",          
                "INICIAR_INVENTARIO",     
                "REALIZAR_VENTA",         
                "REGISTRAR_GASTO",        
                "VER_HISTORIAL_VENTAS",
                "HACER_PEDIDOS_PROVEEDOR" // Trabajador también puede pedir stock? (Asumamos que sí, o lo ajustamos)
            };
            for(String p : listaTrabajador) {
                asignarPermisoRol(con, 2, p);
            } 
            
            con.commit();
            System.out.println("¡ÉXITO! Permisos configurados correctamente.");
            
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            e.printStackTrace();
            try { if(con != null) con.rollback(); } catch(Exception ex){}
        } finally {
            try { if(con != null) { con.setAutoCommit(true); con.close(); } } catch(Exception ex){}
        }
    }
    
    // Método helper para insertar permiso si no existe
    private static void insertarPermiso(Connection con, String nombre, String desc) throws SQLException {
        // Primero verificamos si existe para no duplicar
        String check = "SELECT id_permiso FROM PERMISO WHERE nombre = ?";
        PreparedStatement psCheck = con.prepareStatement(check);
        psCheck.setString(1, nombre);
        if (psCheck.executeQuery().next()) {
            System.out.println(" - El permiso '" + nombre + "' ya existía.");
            psCheck.close();
            return;
        }
        psCheck.close();
        
        String sql = "INSERT INTO PERMISO (nombre, descripcion) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setString(2, desc);
        ps.executeUpdate();
        ps.close();
        System.out.println(" + Permiso '" + nombre + "' creado.");
    }
    
    // Método helper para asignar permiso a rol
    private static void asignarPermisoRol(Connection con, int idRol, String nombrePermiso) throws SQLException {
        // Obtener ID del permiso
        String sqlGetId = "SELECT id_permiso FROM PERMISO WHERE nombre = ?";
        PreparedStatement psGet = con.prepareStatement(sqlGetId);
        psGet.setString(1, nombrePermiso);
        java.sql.ResultSet rs = psGet.executeQuery();
        int idPermiso = -1;
        if(rs.next()) {
            idPermiso = rs.getInt(1);
        }
        rs.close();
        psGet.close();
        
        if (idPermiso == -1) {
            System.err.println(" ! No se encontró el permiso: " + nombrePermiso);
            return;
        }
        
        // Verificar si ya tiene el permiso asignado
        String check = "SELECT * FROM ROL_PERMISOS WHERE id_rol = ? AND id_permiso = ?";
        PreparedStatement psCheck = con.prepareStatement(check);
        psCheck.setInt(1, idRol);
        psCheck.setInt(2, idPermiso);
        if (psCheck.executeQuery().next()) {
            System.out.println(" - El rol " + idRol + " ya tenía el permiso " + nombrePermiso);
            psCheck.close();
            return;
        }
        psCheck.close();
        
        // Asignar
        String sql = "INSERT INTO ROL_PERMISOS (id_rol, id_permiso) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idRol);
        ps.setInt(2, idPermiso);
        ps.executeUpdate();
        ps.close();
        System.out.println(" + Permiso " + nombrePermiso + " asignado al Rol " + idRol);
    }
}
