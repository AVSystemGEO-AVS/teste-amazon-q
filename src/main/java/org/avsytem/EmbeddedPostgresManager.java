package org.avsytem;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmbeddedPostgresManager {

    private EmbeddedPostgres postgres;
    private Connection connection;

    /**
     * Inicia uma inst�ncia do Embedded Postgres, usando o diret�rio especificado para persist�ncia.
     *
     * @param directory Caminho para o diret�rio de dados do Postgres.
     * @throws IOException Se o diret�rio n�o puder ser criado ou o Postgres n�o iniciar.
     * @throws SQLException Se a conex�o com o banco de dados falhar.
     */
    public void start(String directory) throws IOException, SQLException {
        if (postgres != null) {
            System.out.println("[INFO] Embedded Postgres j� est� iniciado.");
            return;
        }

        File dir = new File(directory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("[INFO] Diret�rio criado com sucesso: " + dir.getAbsolutePath());
            } else {
                throw new IOException("[ERROR] Falha ao criar o diret�rio: " + dir.getAbsolutePath());
            }
        } else if (!dir.isDirectory()) {
            throw new IOException("[ERROR] O caminho especificado n�o � um diret�rio v�lido: " + dir.getAbsolutePath());
        } else {
            System.out.println("[INFO] Diret�rio j� existe: " + dir.getAbsolutePath());
        }

        try {
            System.out.println("[INFO] Iniciando Embedded Postgres...");
            postgres = EmbeddedPostgres.builder()
                    .setDataDirectory(dir.toPath())
                    .setCleanDataDirectory(false)
                    .start();
            System.out.println("[INFO] Embedded Postgres iniciado com sucesso.");
        } catch (IOException e) {
            System.err.println("[ERROR] Falha ao iniciar o Embedded Postgres: " + e.getMessage());
            throw e;
        }

        try {
            String url = postgres.getJdbcUrl("postgres", "postgres");
            connection = DriverManager.getConnection(url);
            System.out.println("[INFO] Conex�o com banco de dados estabelecida: " + url);
        } catch (SQLException e) {
            System.err.println("[ERROR] Falha ao conectar com o banco de dados: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void stop() throws IOException {
        if (postgres != null) {
            System.out.println("[INFO] Parando Embedded Postgres...");
            postgres.close();
            postgres = null;
            connection = null;
            System.out.println("[INFO] Embedded Postgres parado com sucesso.");
        }
    }
}
