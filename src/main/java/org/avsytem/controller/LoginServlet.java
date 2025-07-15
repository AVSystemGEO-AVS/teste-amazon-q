package org.avsytem.controller;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.avsytem.dao.UserDAO;
import org.avsytem.database.PostgresConnection;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("usuario");
        String password = request.getParameter("senha");

        try (Connection conn = new PostgresConnection().createConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            String storedHash = userDAO.getPasswordHashByUsername(username);

            // Valida��o:
            // 1. O usu�rio existe (storedHash n�o � nulo)?
            // 2. A senha fornecida corresponde ao hash armazenado?
            if (BCrypt.checkpw(password, storedHash)) {
                // Sucesso na autentica��o
                // Em uma aplica��o real, voc� criaria uma sess�o de usu�rio aqui.
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                response.getWriter().write("{\"success\": true}");
            } else {
                // Falha na autentica��o
                response.getWriter().write("{\"success\": false, \"message\": \"Usu�rio ou senha inv�lidos.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro interno no servidor.\"}");
        }
    }
}
