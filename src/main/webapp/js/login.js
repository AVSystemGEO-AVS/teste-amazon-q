Ext.onReady(function() {

    // Inicia o suporte a Quicktips (tooltips para �cones e bot�es)
    Ext.QuickTips.init();

    /**
     * Fun��o chamada quando o bot�o de login � clicado ou a tecla Enter � pressionada.
     */
    var onLogin = function() {
        var form = loginForm.getForm();
        // Verifica se o formul�rio � v�lido (campos n�o est�o em branco)
        if (form.isValid()) {
            // Submete os dados do formul�rio para a URL configurada
            form.submit({
                waitMsg: 'Autenticando...', // Mensagem de espera

                // Fun��o a ser executada em caso de SUCESSO na resposta do servidor
                success: function(form, action) {
                    // Se o servidor retornar {success: true}, redireciona o navegador
                    window.location = 'app.jsp';
                },

                // Fun��o a ser executada em caso de FALHA na resposta do servidor
                failure: function(form, action) {
                    var msg = 'Erro desconhecido.';
                    // Pega a mensagem de erro espec�fica do JSON retornado pelo servidor
                    if (action.result && action.result.message) {
                        msg = action.result.message;
                    }
                    // Exibe uma caixa de alerta com o erro
                    Ext.Msg.alert('Falha no Login', msg);
                    form.findField('senha').reset(); // Limpa apenas o campo de senha
                }
            });
        }
    };

    /**
     * Defini��o do Painel de Formul�rio (FormPanel) para o login.
     */
    var loginForm = new Ext.form.FormPanel({
        labelWidth: 80,
        url: 'index', // <-- PONTO PRINCIPAL: Aponta para a URL do nosso novo LoginServlet
        frame: true,
        title: 'Por favor, identifique-se',
        bodyStyle: 'padding:10px 10px 0;',
        defaultType: 'textfield',
        monitorValid: true, // Habilita o monitoramento da validade do formul�rio

        // Itens do formul�rio (campos de texto)
        items: [{
            fieldLabel: 'Usuario',
            name: 'usuario',
            allowBlank: false,
            iconCls: 'x-icon-user',
            blankText: 'O campo usu�rio � obrigat�rio.'
        }, {
            fieldLabel: 'Senha',
            name: 'senha',
            inputType: 'password',
            allowBlank: false,
            iconCls: 'x-icon-key',
            blankText: 'O campo senha � obrigat�rio.',
            // Adiciona um listener para a tecla Enter no campo de senha
            listeners: {
                specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
                        onLogin();
                    }
                }
            }
        }],

        // Bot�es do formul�rio
        buttons: [{
            text: 'Entrar',
            handler: onLogin
        }]
    });

    /**
     * Defini��o da Janela (Window) que vai conter o formul�rio de login.
     * Ela � criada sem bordas e n�o pode ser arrastada ou fechada pelo usu�rio,
     * para dar a apar�ncia de uma tela de login dedicada.
     */
        // Cria a janela que conter� o formul�rio
    var loginWindow = new Ext.Window({
            // layout: 'fit', // Remova 'fit' para acomodar o link
            width: 350,
            height: 200, // Aumente um pouco a altura
            closable: false,
            resizable: false,
            plain: true,
            border: false,
            draggable: false,
            items: [
                loginForm,
                {
                    xtype: 'box',
                    autoEl: {
                        tag: 'div',
                        style: 'text-align: center; padding-top: 5px;',
                        cn: [{
                            tag: 'a',
                            href: '#',
                            html: 'N�o tem uma conta? Cadastre-se',
                            style: 'text-decoration: none; color: #333;',
                            onclick: 'return false;' // Previne que o link mude a URL
                        }]
                    },
                    listeners: {
                        // Adiciona o evento de clique ao elemento
                        render: function(box) {
                            box.getEl().on('click', function() {
                                new App.view.UsuarioWindow().show();
                            });
                        }
                    }
                }
            ]
        });

    // Finalmente, exibe a janela de login na tela.
    loginWindow.show();
});