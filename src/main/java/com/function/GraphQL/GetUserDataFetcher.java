package com.function.GraphQL;


import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.function.DTO.UsuarioDTO;
import com.function.util.WalletUtil;

public class GetUserDataFetcher implements DataFetcher<List<UsuarioDTO>> {

    @Override
    public List<UsuarioDTO> get(DataFetchingEnvironment environment) throws Exception {
        List<UsuarioDTO> usuarios = new ArrayList<>();

        // Copiar wallet
        WalletUtil.copyWalletToTemp(System.getProperty("java.io.tmpdir"), null);

        // Datos conexión
        String tmpDir = System.getProperty("java.io.tmpdir");
        String walletPath = tmpDir.contains("\\") ? tmpDir.replace("\\", "/") : tmpDir;

        String oracleUrl = "jdbc:oracle:thin:@et2xa97ns8rti1vt_tp?TNS_ADMIN=" + walletPath;
        String oracleUser = "duoc_fullstack";
        String oraclePass = "Eduardocr#2610";

        try (Connection conn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePass)) {
            String sql = "SELECT * FROM USUARIO";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setId(rs.getLong("ID"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setRut(rs.getString("RUT"));
                usuario.setDireccion(rs.getString("DIRECCION"));
                usuario.setComuna(rs.getString("COMUNA"));
                usuario.setRolId(rs.getLong("ROL_ID"));
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
