Ext.onReady(function() {

    // Define o modelo de dados da Receita para o Store
    var Receita = Ext.data.Record.create([
        {name: 'id', type: 'int'},
        'nome',
        'descricao',
        {name: 'tempoDePreparo', type: 'int'},
        {name: 'porcoes', type: 'int'},
        'dificuldade',
        'categorias', // Note: Estes vir�o como arrays/objetos
        'ingredientes',
        'passos'
    ]);

    // O Store carrega os dados do nosso JSP via AJAX
    var store = new Ext.data.JsonStore({
        url: 'receitas.jsp?action=listar',
        root: 'receitas',
        totalProperty: 'total',
        fields: Receita,
        autoLoad: true // Carrega os dados assim que a p�gina � aberta
    });

    // Fun��o para abrir a janela de edi��o/cria��o
    var janelaReceita = function abrirJanelaReceita(record) {
        var formReceita = new Ext.form.FormPanel({
            labelWidth: 100,
            frame: true,
            bodyStyle: 'padding:5px 5px 0',
            defaultType: 'textfield',
            items: [{
                xtype: 'hidden',
                name: 'id'
            },{
                fieldLabel: 'Nome',
                name: 'nome',
                allowBlank: false,
                anchor: '100%'
            }, {
                fieldLabel: 'Dificuldade',
                name: 'dificuldade',
                anchor: '100%'
            }, {
                xtype: 'numberfield',
                fieldLabel: 'Tempo (min)',
                name: 'tempoDePreparo',
                allowBlank: false,
                width: 100
            }, {
                xtype: 'numberfield',
                fieldLabel: 'Por��es',
                name: 'porcoes',
                allowBlank: false,
                width: 100
            }, {
                xtype: 'textarea',
                fieldLabel: 'Descri��o',
                name: 'descricao',
                anchor: '100%',
                height: 80
            }
                // OBS: Editar ingredientes e passos aqui exigiria componentes mais complexos
                // como um GridPanel edit�vel dentro do formul�rio.
            ]
        });

        var janela = new Ext.Window({
            title: record ? 'Editar Receita' : 'Nova Receita',
            width: 500,
            height: 350,
            layout: 'fit',
            modal: true,
            items: formReceita,
            buttons: [{
                text: 'Salvar',
                handler: function() {
                    formReceita.getForm().submit({
                        url: 'receitas.jsp?action=salvar',
                        success: function(form, action) {
                            Ext.Msg.alert('Sucesso', 'Receita salva com sucesso!');
                            store.reload();
                            janela.close();
                        },
                        failure: function(form, action) {
                            Ext.Msg.alert('Erro', 'Ocorreu um erro ao salvar a receita.');
                        }
                    });
                }
            }, {
                text: 'Cancelar',
                handler: function() {
                    janela.close();
                }
            }]
        });

        if (record) {
            formReceita.getForm().loadRecord(record);
        }

        janela.show();
    }

    // Fun��o para deletar uma receita
    function deletarReceita(grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);
        var id = record.get('id');
        var nome = record.get('nome');

        Ext.Msg.confirm('Confirmar', 'Tem certeza que deseja deletar a receita "' + nome + '"?', function(btn) {
            if (btn === 'yes') {
                Ext.Ajax.request({
                    url: 'receitas.jsp?action=deletar',
                    params: { id: id },
                    success: function(response) {
                        var result = Ext.decode(response.responseText);
                        if (result.success) {
                            store.reload();
                        } else {
                            Ext.Msg.alert('Erro', 'Falha ao deletar a receita.');
                        }
                    },
                    failure: function() {
                        Ext.Msg.alert('Erro', 'Erro de comunica��o com o servidor.');
                    }
                });
            }
        });
    }

    // A Grid que lista as receitas
    var gridReceitas = new Ext.grid.GridPanel({
        store: store,
        renderTo: 'grid-receitas',
        width: '100%',
        height: 500,
        title: 'Minhas Receitas',
        columns: [
            {header: 'ID', width: 40, dataIndex: 'id', sortable: true},
            {header: 'Nome', width: 250, dataIndex: 'nome', sortable: true},
            {header: 'Dificuldade', width: 100, dataIndex: 'dificuldade', sortable: true},
            {header: 'Tempo de Preparo (min)', width: 150, dataIndex: 'tempoDePreparo', sortable: true},
            {header: 'Rendimento (por��es)', width: 150, dataIndex: 'porcoes', sortable: true},
            {
                xtype: 'actioncolumn',
                width: 60,
                header: 'A��es',
                items: [{
                    icon: 'images/edit.png',
                    tooltip: 'Editar/Abrir Receita',
                    handler: function(grid, rowIndex, colIndex) {
                        var record = store.getAt(rowIndex);
                        abrirJanelaReceita(record);
                    }
                }, {
                    icon: 'images/delete.png',
                    tooltip: 'Deletar Receita',
                    handler: function(grid, rowIndex, colIndex) {
                        deletarReceita(grid, rowIndex);
                    }
                }]
            }
        ],
        tbar: [{
            text: 'Nova Receita',
            iconCls: 'x-btn-text-icon-add', // �cone padr�o do Ext JS
            handler: function() {
                abrirJanelaReceita(null); // Passa null para indicar que � um novo registro
            }
        }],
        bbar: new Ext.PagingToolbar({
            pageSize: 25, // Em uma app real, voc� implementaria a pagina��o no back-end
            store: store,
            displayInfo: true,
            displayMsg: 'Mostrando receitas {0} - {1} de {2}',
            emptyMsg: "Nenhuma receita para mostrar"
        })
    });
});