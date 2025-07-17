package org.avsytem.dao;

import org.mindrot.jbcrypt.BCrypt;

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
     * Adiciona um novo usu�rio ao banco de dados, com a senha criptografada.
     * @param nomeCompleto O nome completo do usu�rio.
     * @param email O email do usu�rio.
     * @param username O nome de usu�rio para login.
     * @param plainTextPassword A senha em texto plano, que ser� criptografada.
     * @throws SQLException se ocorrer um erro no banco, como username duplicado.
     */
    public void adicionar(String nomeCompleto, String email, String username, String plainTextPassword) throws SQLException {
        // Gera o "sal" e o hash da senha usando BCrypt
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());

        String sql = "INSERT INTO usuarios (nome_completo, email, username, password_hash) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomeCompleto);
            pstmt.setString(2, email);
            pstmt.setString(3, username);
            pstmt.setString(4, hashedPassword); // Salva a senha criptografada
            pstmt.executeUpdate();
        }
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

    public boolean deletarPorUsername(String username) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Retorna true se uma linha foi deletada
        }
    }

}