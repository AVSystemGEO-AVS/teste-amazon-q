Ext.onReady(function() {
    // Carrega os arquivos da aplica��o na ordem correta de depend�ncia
    Ext.Loader.load([
        'js/app/model/Receita.js',
        'js/app/store/Receitas.js',
        'js/app/view/ReceitaWindow.js',
        'js/app/view/ReceitaGrid.js'
    ], function() {
        // Namespace principal da aplica��o
        Ext.ns('App');

        // Inicializa o sistema de dicas (tooltips)
        Ext.QuickTips.init();

        // Imagem transparente padr�o do ExtJS
        Ext.BLANK_IMAGE_URL = 'extjs/resources/images/default/s.gif';

        // Cria a inst�ncia principal da nossa View (a Grid)
        var grid = new App.view.ReceitaGrid({
            renderTo: 'grid-receitas-container'
        });
    }, this, true); // O 'true' no final preserva a ordem de carregamento
});