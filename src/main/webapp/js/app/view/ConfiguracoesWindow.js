Ext.ns('App.view');

App.view.ConfiguracoesWindow = Ext.extend(Ext.Window, {
    title: 'Configura��es da Conta',
    width: 400,
    height:'auto',
    layout: 'form',
    modal: true,
    bodyStyle: 'padding:15px;',

    initComponent: function() {
        Ext.apply(this, {
            items: [{
                xtype: 'label',
                text: 'Aten��o: A exclus�o da sua conta � uma a��o permanente e n�o pode ser desfeita.'
            }],
            buttons: [{
                text: 'Excluir Minha Conta Permanentemente',
                iconCls: 'x-icon-delete',
                handler: this.onDeleteAccount,
                scope: this
            }, {
                text: 'Cancelar',
                handler: function() { this.close(); },
                scope: this
            }]
        });
        App.view.ConfiguracoesWindow.superclass.initComponent.call(this);
    },

    onDeleteAccount: function() {
        Ext.Msg.confirm('Confirma��o Final', 'Voc� tem certeza ABSOLUTA que deseja excluir sua conta? Todos os seus dados ser�o perdidos.', function(btn) {
            if (btn === 'yes') {
                this.el.mask('Excluindo...', 'x-mask-loading');
                Ext.Ajax.request({
                    url: 'usuarios', // Aponta para o doDelete do UsuarioServlet
                    method: 'DELETE',
                    scope: this,
                    success: function(response) {
                        this.el.unmask();
                        Ext.Msg.alert('Conta Exclu�da', 'Sua conta foi removida com sucesso.', function() {
                            window.location = 'login.jsp';
                        });
                    },
                    failure: function(response) {
                        this.el.unmask();
                        var result = Ext.decode(response.responseText);
                        Ext.Msg.alert('Erro', result.message || 'N�o foi poss�vel excluir a conta.');
                    }
                });
            }
        }, this);
    }
});