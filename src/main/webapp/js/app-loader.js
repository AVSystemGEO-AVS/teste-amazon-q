// app.js
Ext.onReady(function() {
    // Carrega os arquivos da aplica��o na ordem correta de depend�ncia
    Ext.Loader.load([
        'js/app/model/models.js',
        'js/app/store/ReceitaStore.js',
        'js/app/view/ReceitaWindow.js',
        'js/app/view/ReceitaGrid.js'
    ], function()
    {
        // Namespace principal da aplica��o
        // Registra um 'apelido' para a sua grid
        Ext.reg('receitagrid', App.view.ReceitaGrid);

        // Inicia o Quicktips para os tooltips dos �cones funcionarem
        Ext.QuickTips.init();

        // Cria o Viewport que ocupa 100% da tela
        new Ext.Viewport({
            layout: 'fit', // Faz o item filho (a grid) ocupar todo o espa�o
            items: [
                {
                    xtype: 'receitagrid' // Cria a sua grid principal
                }
            ]
        });
    }, this, true); // O 'true' no final preserva a ordem de carregamento
});