package org.avsytem.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta classe gerencia o ciclo de vida da aplica��o web.
 * � respons�vel por inicializar recursos compartilhados, como o pool de conex�es (DataSource),
 * quando a aplica��o inicia, e liber�-los quando a aplica��o para.
 */
@WebListener // 1. Anota��o que registra o Listener no servidor
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppLifecycleListener.class.getName());

    /**
     * Este m�todo � chamado pelo servidor EXATAMENTE UMA VEZ, quando a aplica��o est� iniciando.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Iniciando aplica��o: Configurando recursos compartilhados.");

        try {
            // 2. Faz o JNDI Lookup do DataSource (apenas uma vez!)
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/PostgresDB");

            // 3. Armazena a inst�ncia do DataSource no ServletContext
            // O ServletContext � um "mapa" global, compartilhado por toda a aplica��o.
            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute("dataSource", dataSource);

            LOGGER.info("DataSource configurado e armazenado no ServletContext com sucesso.");

        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "ERRO CR�TICO: N�o foi poss�vel inicializar o DataSource. A aplica��o n�o funcionar� corretamente.", e);
            // Em um cen�rio real, isso deveria impedir a aplica��o de iniciar.
            throw new RuntimeException(e);
        }
    }

    /**
     * Este m�todo � chamado pelo servidor EXATAMENTE UMA VEZ, quando a aplica��o est� parando.
     * �til para fechar recursos, se necess�rio.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Finalizando aplica��o: Liberando recursos.");
        // Se o DataSource precisasse ser fechado manualmente (raro em pools gerenciados),
        // o c�digo viria aqui.
    }
}