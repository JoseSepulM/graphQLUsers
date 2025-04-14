package com.function.GraphQL;

import com.function.util.WalletUtil;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class AddUserDataFetcher implements DataFetcher<String> {

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        // Obtener el input como un mapa
        Map<String, Object> input = environment.getArgument("input");

        // Acceder a los valores dentro de 'input'
        String nombre = (String) input.get("nombre");
        String rut = (String) input.get("rut");
        String direccion = (String) input.get("direccion");
        String comuna = (String) input.get("comuna");
        Integer rolIdInt = (Integer) input.get("rolId");
        Long rolId = Long.valueOf(rolIdInt);
        // Long rolId = (Long) input.get("rolId");

        // Copiar wallet de manera segura (sin contexto)
        WalletUtil.copyWalletToTemp(System.getProperty("java.io.tmpdir"), null);

        // Datos de conexión a Oracle
        String tmpDir = System.getProperty("java.io.tmpdir");
        String walletPath = tmpDir.contains("\\") ? tmpDir.replace("\\", "/") : tmpDir;

        String oracleUrl = "jdbc:oracle:thin:@et2xa97ns8rti1vt_tp?TNS_ADMIN=" + walletPath;
        String oracleUser = "duoc_fullstack";
        String oraclePass = "Eduardocr#2610";

        try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePass)) {
            // SQL para insertar todos los campos necesarios en la tabla
            String sql = "INSERT INTO USUARIO (NOMBRE, RUT, DIRECCION, COMUNA, ROL_ID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Establecer los valores de los parámetros en el PreparedStatement
                stmt.setString(1, nombre);        // Nombre del usuario
                stmt.setString(2, rut);           // Rut del usuario
                stmt.setString(3, direccion);     // Dirección del usuario
                stmt.setString(4, comuna);        // Comuna del usuario
                stmt.setLong(5, rolId);           // ID del rol

                // Ejecutar la inserción
                int rowsInserted = stmt.executeUpdate();

                // Comprobar si se insertó algún registro
                if (rowsInserted > 0) {
                    return "Usuario creado exitosamente.";
                } else {
                    return "No se pudo crear el usuario.";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al crear usuario: " + e.getMessage();
        }
    }
}
