package org.avsytem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Busca o hash da senha de um usu�rio pelo seu username.
     * Retorna o hash se o usu�rio for encontrado e estiver ativo, caso contr�rio, null.
     */
    public String getPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM usuarios WHERE username = ? AND ativo = true";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }
        return null; // Usu�rio n�o encontrado ou inativo
    }
}