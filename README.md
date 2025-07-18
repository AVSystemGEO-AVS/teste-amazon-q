# Teste Amazon Q

## 1\. Descri��o

Este projeto � uma aplica��o web desenvolvida com o framework **ExtJS** e **Java**. A aplica��o � renderizada em uma p�gina JSP e executada em um servidor Apache Tomcat 9. O objetivo � simular um ambiente com tecnologias e vers�es semelhantes **�** aplica��o **principal** da empresa para testes de migra��o e moderniza��o do sistema.

### Aplica��o principal da empresa

A aplica��o principal da empresa � legada e antiga. N�o est� t�o bem organizada, e � muito maior e mais ca�tica. Este projeto-piloto � um estudo para orientar a moderniza��o da aplica��o principal. Visa validar que podemos usar o Amazon Q para nos auxiliar com as tecnologias legadas e aprender um pouco mais sobre a ferramenta em um cen�rio mais alinhado com a nossa realidade.

## 2\. Tecnologias Utilizadas

* **Frontend**: ExtJS 3.4
* **Backend**: Java 8 (Servlets/JSP)
* **Servidor de Aplica��o**: Apache Tomcat 9

## 3\. Arquitetura e Fluxo de Integra��o

H� uma integra��o entre o frontend ExtJS e o backend em Java. O ExtJS � respons�vel por toda a UI e intera��o com o usu�rio, e o Java atua como um servi�o de API, fornecendo e recebendo dados.

### Vis�o Geral da Arquitetura

1. **Browser (Cliente)**: O usu�rio acessa uma p�gina JSP. Esta p�gina serve principalmente como um cont�iner para carregar as bibliotecas ExtJS e os arquivos JavaScript da aplica��o.
2. **Frontend (ExtJS)**: Uma vez carregado, o JavaScript (ExtJS) assume o controle. Ele renderiza toda a interface do usu�rio (grids, formul�rios, pain�is) dinamicamente no corpo da p�gina JSP.
3. **Servidor (Tomcat)**: O Tomcat hospeda a aplica��o, compilada como um arquivo `.war`, e gerencia o ciclo de vida das requisi��es HTTP, direcionando-as para os Servlets apropriados.

### Fluxo de Requisi��o de Dados (Ex: Carregar um Grid)

Quando um componente ExtJS, como um `GridPanel`, precisa exibir dados, ele inicia um fluxo de requisi��o ass�ncrona.

```mermaid
sequenceDiagram
    participant Browser (ExtJS)
    participant Tomcat (Java Servlet)
    participant Database

    Browser (ExtJS)->>Tomcat (Java Servlet): 1. GET /seu-app/listarDados
    Tomcat (Java Servlet)->>Database: 2. Realiza a consulta (ex: SELECT * FROM tabela)
    Database-->>Tomcat (Java Servlet): 3. Retorna os dados
    Tomcat (Java Servlet)->>Browser (ExtJS): 4. Envia resposta HTTP com JSON <br> `{"success":true, "data":[...]}`
    Note left of Browser (ExtJS): 5. Ext.data.Store carrega os dados <br> e o GridPanel � atualizado.
```

**Passo a Passo:**

1. **Componente ExtJS**: Um `Ext.grid.GridPanel` � configurado com um `Ext.data.Store` (por exemplo, `JsonStore`).
2. **Proxy**: O `Store` possui um `Proxy` (geralmente `HttpProxy` ou `AjaxProxy`) configurado com a URL do endpoint Java (ex: `/seu-app/listarDados`).
3. **Requisi��o**: Ao ser carregado, o `Store` utiliza o `Proxy` para fazer uma requisi��o AJAX (GET) para a URL especificada.
4. **Servlet Java**: O Tomcat direciona a requisi��o para o Servlet Java mapeado para essa URL. O Servlet processa a requisi��o, busca os dados no banco de dados e os serializa para o formato JSON.
5. **Resposta JSON**: O Servlet retorna uma resposta HTTP com `Content-Type: application/json` e o payload JSON. Uma estrutura de resposta t�pica que o ExtJS espera �: `{ "success": true, "total": 50, "data": [...] }`.
6. **Reader**: O `Store` do ExtJS usa seu `Reader` (ex: `JsonReader`) para ler a resposta JSON, interpretar a estrutura e criar inst�ncias de `Ext.data.Record`.
7. **Renderiza��o**: O `Store` notifica o `GridPanel` de que os dados foram carregados, e o grid se renderiza, exibindo os dados em linhas e colunas.

### Fluxo de Submiss�o de Dados (Ex: Salvar um Formul�rio)

Quando o usu�rio preenche um formul�rio e clica em "Salvar", um fluxo de submiss�o de dados � iniciado.

```mermaid
sequenceDiagram
    participant Browser (ExtJS)
    participant Tomcat (Java Servlet)
    participant Database

    Note left of Browser (ExtJS): 1. Usu�rio preenche o Ext.form.FormPanel <br> e clica em Salvar.
    Browser (ExtJS)->>Tomcat (Java Servlet): 2. POST /seu-app/salvarDados com dados do form
    Tomcat (Java Servlet)->>Database: 3. Processa e persiste os dados (INSERT/UPDATE)
    Database-->>Tomcat (Java Servlet): 4. Confirma a persist�ncia
    Tomcat (Java Servlet)->>Browser (ExtJS): 5. Envia resposta HTTP com JSON <br> `{"success":true, "message":"Salvo com sucesso!"}`
    Note left of Browser (ExtJS): 6. ExtJS exibe notifica��o (ex: Ext.Msg) ao usu�rio.
```

**Passo a Passo:**

1. **Formul�rio ExtJS**: O usu�rio interage com um `Ext.form.FormPanel`.
2. **A��o de Submiss�o**: Ao clicar no bot�o "Salvar", a fun��o handler associada � chamada. Esta fun��o utiliza o m�todo `form.submit()` do `FormPanel`.
3. **Requisi��o AJAX**: O m�todo `submit()` faz uma requisi��o AJAX (POST) para a URL configurada no `FormPanel` (ex: `/seu-app/salvarDados`), enviando os dados dos campos do formul�rio no corpo da requisi��o.
4. **Servlet Java**: O Servlet correspondente recebe os dados do formul�rio, valida-os e executa a l�gica de neg�cios para salvar as informa��es no banco de dados.
5. **Resposta de Sucesso/Falha**: O Servlet retorna uma resposta JSON indicando o resultado da opera��o, por exemplo: `{ "success": true, "message": "Dados salvos com sucesso!" }` ou `{ "success": false, "errors": {"campo": "Mensagem de erro"} }`.
6. **Callback do ExtJS**: A submiss�o no ExtJS � configurada com callbacks de `success` e `failure`. Com base na resposta do servidor, o callback apropriado � executado, permitindo, por exemplo, exibir uma mensagem de sucesso ao usu�rio com `Ext.Msg` ou tratar os erros.


## 4\. Autentica��o

A aplica��o implementa um sistema de autentica��o robusto com as seguintes caracter�sticas:

- **Interface de Login**: Desenvolvida com ExtJS, permite ao usu�rio inserir seu nome de usu�rio e senha.
- **Processamento no Backend**:
    - O `LoginServlet` processa a requisi��o e valida as credenciais usando o DAO (`UserDAO`).
    - As senhas s�o protegidas com hashing utilizando o algoritmo `BCrypt`.
- **Sess�o HTTP**: Ao autenticar-se com sucesso, uma sess�o � criada e o usu�rio tem acesso �s funcionalidades protegidas.
- **Prote��o de Rotas**:
    - O `AuthenticationFilter` garante que apenas usu�rios autenticados possam acessar as rotas protegidas da aplica��o.
    - Usu�rios n�o autenticados s�o redirecionados automaticamente para a tela de login (`index.jsp`).

Este mecanismo reflete pr�ticas comuns em sistemas legados com JSP/Servlets, fornecendo controle de acesso baseado em sess�o e seguran�a de senha com hash.

## 5\. Deploy no Tomcat (.war)

A aplica��o � empacotada como um arquivo **WAR** (Web Application Archive) para deploy no Apache Tomcat.

1. **Estrutura do Projeto**: O projeto Java deve seguir a estrutura padr�o de uma aplica��o web para que possa ser empacotado corretamente. Os arquivos ExtJS (bibliotecas e c�digo da aplica��o) s�o inclu�dos como recursos est�ticos, geralmente dentro de um diret�rio como `src/main/webapp/extjs`.
2. **Build**: O processo de build (usando Maven) compila as classes Java, agrupa os arquivos JSP, os arquivos de configura��o (`web.xml`) e os recursos est�ticos (JS, CSS) em um �nico arquivo `.war`.
3. **Deploy**: O arquivo `.war` gerado � ent�o copiado para o diret�rio `webapps` do Tomcat. O Tomcat descompacta e implanta a aplica��o, tornando-a acess�vel pela URL configurada (ex: `http://localhost:8080/nome-do-projeto`).

### Configura��o do Banco de Dados

A conex�o com o banco de dados � centralizada e configur�vel:

* **Conex�o**: � configurada em `src/main/webapp/META-INF/context.xml`. O tomcat gerencia as cone��es e acessos ao database.
* **Driver**: O `pom.xml` inclui a depend�ncia do driver JDBC do PostgreSQL, que � essencial para a comunica��o.

#### Database Configuration

```
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource
            name="jdbc/PostgresDB"
            auth="Container"
            type="javax.sql.DataSource"
            driverClassName="org.postgresql.Driver"
            url="jdbc:postgresql://localhost:4589/database"
            username="user"
            password="senha"
            maxTotal="20"
            maxIdle="10"
            maxWaitMillis="10000"
    />
</Context>
```
