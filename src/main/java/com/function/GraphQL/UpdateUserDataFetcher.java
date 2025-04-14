package com.function.GraphQL;

import com.function.util.WalletUtil;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class UpdateUserDataFetcher implements DataFetcher<String> {

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        // Obtener el input (que es un mapa de los campos dentro de UserInput)
        Map<String, Object> input = environment.getArgument("input");

        // Acceder a los valores dentro de 'input'
        String nuevoNombre = (String) input.get("nombre");
        String nuevoRut = (String) input.get("rut");
        String nuevaDireccion = (String) input.get("direccion");
        String nuevaComuna = (String) input.get("comuna");
        Integer rolIdInt = (Integer) input.get("rolId");
        Long nuevoRolId = Long.valueOf(rolIdInt);

        // Long nuevoRolId = (Long) input.get("rolId");

        // Obtener el id del argumento fuera de input
        String idStr = environment.getArgument("id");
        Long id = Long.parseLong(idStr); // Convertir el ID a Long

        WalletUtil.copyWalletToTemp(System.getProperty("java.io.tmpdir"), null);

        String tmpDir = System.getProperty("java.io.tmpdir");
        String walletPath = tmpDir.contains("\\") ? tmpDir.replace("\\", "/") : tmpDir;

        String oracleUrl = "jdbc:oracle:thin:@et2xa97ns8rti1vt_tp?TNS_ADMIN=" + walletPath;
        String oracleUser = "duoc_fullstack";
        String oraclePass = "Eduardocr#2610";

        try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePass)) {
            // SQL de actualización con todos los campos que deseas actualizar
            String sql = "UPDATE USUARIO SET NOMBRE = ?, RUT = ?, DIRECCION = ?, COMUNA = ?, ROL_ID = ? WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Establecer los valores de los parámetros en el PreparedStatement
                stmt.setString(1, nuevoNombre);  // Establecer el nuevo nombre
                stmt.setString(2, nuevoRut);     // Establecer el nuevo rut
                stmt.setString(3, nuevaDireccion); // Establecer la nueva dirección
                stmt.setString(4, nuevaComuna);   // Establecer la nueva comuna
                stmt.setLong(5, nuevoRolId);      // Establecer el nuevo rol
                stmt.setLong(6, id);              // Establecer el ID del usuario a actualizar

                // Ejecutar la actualización
                int rowsUpdated = stmt.executeUpdate();

                // Comprobar si se actualizó algún registro
                if (rowsUpdated > 0) {
                    return "Usuario actualizado exitosamente.";
                } else {
                    return "No se encontró el usuario para actualizar.";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al actualizar usuario: " + e.getMessage();
        }
    }
}
