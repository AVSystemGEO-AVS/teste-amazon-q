package org.avsytem.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        req.setCharacterEncoding("ISO-8859-1");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false); // false = n�o cria uma nova sess�o

        String loginURI = request.getContextPath() + "/index.jsp";
        String loginServletURI = request.getContextPath() + "/index";

        boolean loggedIn = session != null
                && session.getAttribute("username") != null
                && session.getAttribute("usuario_id") != null;

        boolean loginRequest = request.getRequestURI().equals(loginURI) || request.getRequestURI().equals(loginServletURI);

        // Se j� est� logado ou est� tentando logar, continua a requisi��o
        if (loggedIn || loginRequest)
            chain.doFilter(request, response);
        // Se n�o est� logado e n�o est� na p�gina de login, redireciona para o login
        else
            response.sendRedirect(loginURI);
    }

    public void init(FilterConfig fConfig) throws ServletException {}
    public void destroy() {}
}