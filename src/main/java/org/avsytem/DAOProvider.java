package org.avsytem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avsytem.dao.ReceitaDAO;
import org.avsytem.model.Receita;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class DAOProvider {

    private final ReceitaDAO dao;
    private final EmbeddedPostgresManager postgresManager;

    /**
     * Construtor PRIVADO.
     * Cont�m toda a l�gica de inicializa��o. Por ser privado, ele s� pode ser
     * chamado de dentro desta pr�pria classe, garantindo que ningu�m de fora
     * possa criar novas inst�ncias.
     */
    private DAOProvider() {
        try {
            System.out.println("[DAOProvider] Construtor privado sendo executado. Inicializando o banco de dados UMA �NICA VEZ...");

            // Inicializa o gerenciador do banco
            this.postgresManager = new EmbeddedPostgresManager();
            this.postgresManager.start("db-data"); // Inicia o banco de dados

            // Cria a �nica inst�ncia do DAO
            this.dao = new ReceitaDAO(postgresManager);

            // Garante que a estrutura do banco de dados exista
            this.dao.criarEstruturaBanco();

            // Popula o banco com dados iniciais se estiver vazio
            if (this.dao.listar().isEmpty()) {
                System.out.println("[DAOProvider] Banco de dados vazio. Populando com dados iniciais...");
                popularDadosIniciais(this.dao);
            }

            // Registra o gancho de desligamento para parar o banco de forma segura
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (this.postgresManager != null) {
                    System.out.println("[DAOProvider] Shutdown hook acionado. Finalizando o PostgreSQL embarcado...");
                    try {
                        this.postgresManager.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[DAOProvider] PostgreSQL finalizado.");
                }
            }));

            System.out.println("[DAOProvider] Banco de dados e DAO prontos para uso.");

        } catch (Exception e) {
            e.printStackTrace();
            // Lan�a uma exce��o para indicar que a aplica��o n�o pode continuar
            throw new RuntimeException("Falha cr�tica ao inicializar o DAOProvider.", e);
        }
    }

    /**
     * A classe interna est�tica que segura a inst�ncia.
     * Esta classe s� � carregada na mem�ria quando o m�todo getInstance() � chamado
     * pela primeira vez, o que torna a inicializa��o "pregui�osa" (lazy).
     */
    private static class SingletonHolder {
        private static final DAOProvider INSTANCE = new DAOProvider();
    }

    /**
     * O m�todo P�BLICO e EST�TICO que o resto da sua aplica��o usar�.
     * � a �nica porta de entrada para obter a inst�ncia do DAOProvider.
     * @return A inst�ncia �nica do DAOProvider.
     */
    public static DAOProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * M�todo de inst�ncia para acessar o DAO.
     * @return a inst�ncia do ReceitaDAO.
     */
    public ReceitaDAO getDao() {
        return this.dao;
    }

    /**
     * M�todo corrigido para popular os dados
     */
    private void popularDadosIniciais(ReceitaDAO dao) throws SQLException, IOException {
        // Procure o arquivo no classpath. Coloque "receitas.json" na pasta de resources do seu projeto
        // (ex: src/main/resources se usar Maven, ou diretamente em WEB-INF/classes)
        InputStream inputStream = DAOProvider.class.getClassLoader().getResourceAsStream("receitas.json");
        if (inputStream == null) {
            System.err.println("Arquivo 'receitas.json' n�o encontrado no classpath!");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Receita> receitas = mapper.readValue(inputStream, new TypeReference<List<Receita>>() {});

        // Faltava esta parte: iterar e adicionar cada receita no banco
        for (Receita receita : receitas) {
            dao.adicionar(receita);
        }
    }
}