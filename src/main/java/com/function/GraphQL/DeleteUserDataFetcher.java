package com.function.GraphQL;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.function.util.WalletUtil;

public class DeleteUserDataFetcher implements DataFetcher<String> {

    @Override
    public String get(DataFetchingEnvironment environment) throws Exception {
        
        // Obtén el ID como String (GraphQL lo pasa como String por defecto)
        String idStr = environment.getArgument("id");

        // Convierte el String a Long
        Long id = Long.parseLong(idStr);

        // Datos de conexión a Oracle
        WalletUtil.copyWalletToTemp(System.getProperty("java.io.tmpdir"), null);

        String tmpDir = System.getProperty("java.io.tmpdir");
        String walletPath = tmpDir.contains("\\") ? tmpDir.replace("\\", "/") : tmpDir;

        String oracleUrl = "jdbc:oracle:thin:@et2xa97ns8rti1vt_tp?TNS_ADMIN=" + walletPath;
        String oracleUser = "duoc_fullstack";
        String oraclePass = "Eduardocr#2610";

        try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePass)) {
            // SQL para eliminar el usuario
            String sql = "DELETE FROM USUARIO WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Usamos setLong para pasar el ID como Long
                stmt.setLong(1, id);

                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    return "Usuario eliminado exitosamente.";
                } else {
                    return "No se encontró el usuario para eliminar.";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar usuario: " + e.getMessage();
        }
    }
}
