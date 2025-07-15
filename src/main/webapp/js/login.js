Ext.onReady(function() {

    // Inicia o suporte a Quicktips (tooltips para ícones e botões)
    Ext.QuickTips.init();

    /**
     * Função chamada quando o botão de login é clicado ou a tecla Enter é pressionada.
     */
    var onLogin = function() {
        var form = loginForm.getForm();
        // Verifica se o formulário é válido (campos não estão em branco)
        if (form.isValid()) {
            // Submete os dados do formulário para a URL configurada
            form.submit({
                waitMsg: 'Autenticando...', // Mensagem de espera

                // Função a ser executada em caso de SUCESSO na resposta do servidor
                success: function(form, action) {
                    // Se o servidor retornar {success: true}, redireciona o navegador
                    window.location = 'app.jsp';
                },

                // Função a ser executada em caso de FALHA na resposta do servidor
                failure: function(form, action) {
                    var msg = 'Erro desconhecido.';
                    // Pega a mensagem de erro específica do JSON retornado pelo servidor
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
     * Definição do Painel de Formulário (FormPanel) para o login.
     */
    var loginForm = new Ext.form.FormPanel({
        labelWidth: 80,
        url: 'index', // <-- PONTO PRINCIPAL: Aponta para a URL do nosso novo LoginServlet
        frame: true,
        title: 'Por favor, identifique-se',
        bodyStyle: 'padding:10px 10px 0;',
        defaultType: 'textfield',
        monitorValid: true, // Habilita o monitoramento da validade do formulário

        // Itens do formulário (campos de texto)
        items: [{
            fieldLabel: 'Usuário',
            name: 'usuario',
            allowBlank: false,
            iconCls: 'x-icon-user',
            blankText: 'O campo usuário é obrigatório.'
        }, {
            fieldLabel: 'Senha',
            name: 'senha',
            inputType: 'password',
            allowBlank: false,
            iconCls: 'x-icon-key',
            blankText: 'O campo senha é obrigatório.',
            // Adiciona um listener para a tecla Enter no campo de senha
            listeners: {
                specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
                        onLogin();
                    }
                }
            }
        }],

        // Botões do formulário
        buttons: [{
            text: 'Entrar',
            formBind: true, // O botão só fica habilitado se o formulário for válido
            handler: onLogin
        }]
    });

    /**
     * Definição da Janela (Window) que vai conter o formulário de login.
     * Ela é criada sem bordas e não pode ser arrastada ou fechada pelo usuário,
     * para dar a aparência de uma tela de login dedicada.
     */
    var loginWindow = new Ext.Window({
        layout: 'fit',
        width: 350,
        height: 170,
        closable: false,
        resizable: false,
        plain: true,
        border: false,
        draggable: false,
        items: [loginForm] // Adiciona o formulário dentro da janela
    });

    // Finalmente, exibe a janela de login na tela.
    loginWindow.show();
});