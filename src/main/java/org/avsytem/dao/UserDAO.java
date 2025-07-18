package org.avsytem.dao;

import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * DAO para a entidade Usuario.
 * Lida com opera��es de banco de dados para usu�rios de forma otimizada,
 * utilizando um DataSource para gerenciar um pool de conex�es.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // 1. As queries s�o definidas como constantes para clareza e manuten��o.
    private static final String INSERT_USER_SQL = "INSERT INTO usuarios (nome_completo, email, username, password_hash) VALUES (?, ?, ?, ?)";
    private static final String GET_HASH_SQL = "SELECT password_hash FROM usuarios WHERE username = ? AND ativo = true";
    private static final String DELETE_USER_SQL = "DELETE FROM usuarios WHERE username = ?";

    // 2. O DAO armazena a refer�ncia ao pool de conex�es.
    private final DataSource dataSource;

    /**
     * Construtor que recebe o DataSource.
     * @param dataSource O pool de conex�es a ser usado.
     */
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
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
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());

        // 3. O m�todo obt�m, usa e fecha sua pr�pria conex�o do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_SQL)) {

            pstmt.setString(1, nomeCompleto);
            pstmt.setString(2, email);
            pstmt.setString(3, username);
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca o hash da senha de um usu�rio pelo seu username.
     * Retorna o hash se o usu�rio for encontrado e estiver ativo, caso contr�rio, null.
     */
    public String getPasswordHashByUsername(String username) throws SQLException {
        // 3. O m�todo obt�m, usa e fecha sua pr�pria conex�o do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_HASH_SQL)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }
        return null; // Usu�rio n�o encontrado ou inativo
    }

    /**
     * Deleta um usu�rio do banco de dados pelo seu username.
     * @param username O nome de usu�rio a ser deletado.
     * @return true se um usu�rio foi deletado, false caso contr�rio.
     * @throws SQLException se ocorrer um erro no banco.
     */
    public boolean deletarPorUsername(String username) throws SQLException {
        // 3. O m�todo obt�m, usa e fecha sua pr�pria conex�o do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER_SQL)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}