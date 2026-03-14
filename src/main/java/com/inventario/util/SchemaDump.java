package com.inventario.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaDump {
    public static void main(String[] args) {
        try {
            Connection con = Conexion.getConexion();
            if (con == null) {
                System.out.println("No DB Connection");
                return;
            }
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tables = metaData.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("TABLE: " + tableName);
                ResultSet columns = metaData.getColumns(con.getCatalog(), null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String datatype = columns.getString("TYPE_NAME");
                    System.out.println("  - " + columnName + " (" + datatype + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
