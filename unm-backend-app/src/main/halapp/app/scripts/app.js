'use strict';

var halApp = angular.module( 'halSearchApp', [
    'halApp.templates', 'ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'hateoas', 'ui.bootstrap', 'angular-loading-bar', 'angularSpinner', 'validator', 'validator.rules'
] );
halApp.model = {};
halApp.URL_PREF = baseUrl;

halApp.config( [ '$routeProvider', 'HateoasInterceptorProvider', 'HateoasInterfaceProvider', '$validatorProvider', function( $routeProvider, HateoasInterceptorProvider, HateoasInterfaceProvider, $validatorProvider ) {
    $routeProvider
        .when( '/main', {
            templateUrl: 'views/main.html',
            controller: 'MainCtrl'
        } )
        .when( '/notifications', {
            templateUrl: 'views/notifications.html', controller: 'CtrlNotifications'
        } )
        .when( '/notifications/:itemId/edit', {
            templateUrl: 'views/notification-edit.html', controller: 'CtrlNotificationEdit'
        } )
        .when( '/feeds', {
            templateUrl: 'views/feeds.html', controller: 'CtrlFeeds'
        } )
        .when( '/feeds/:itemId/edit', {
            templateUrl: 'views/feed-edit.html', controller: 'CtrlFeedEdit'
        } )
        .when( '/menus', {
            templateUrl: 'views/menus.html', controller: 'CtrlMenus'
        } )
        .when( '/menus/:itemId/edit', {
            templateUrl: 'views/menu-edit.html', controller: 'CtrlMenuEdit'
        } )
        .otherwise( {
            redirectTo: '/main'
        } );

        HateoasInterceptorProvider.transformAllResponses();
        HateoasInterfaceProvider.setLinksKey("_links");
        
        $validatorProvider.register('optionalUrl', {
            invoke: 'blur',
            validator: /((((http[s]{0,1}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)|^$)/,
            error: "Ce champ doit être l'URL ."
        });
        $validatorProvider.register('requiredUrl', {
            invoke: 'blur',
            validator: /(((http[s]{0,1}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/,
            error: "Ce champ doit être l'URL ."
        });
} ] );

halApp.showAlert = function( msg, error ) {
    var $alert = $("#alert-global");
    $alert.html( msg );
    $alert.removeClass("alert-success" ).removeClass("alert-danger");
    if ( error ) {
        $alert.addClass("alert-danger");
    } else {
        $alert.addClass("alert-success");
    }
    $alert.show();
    $alert.css("top", $(window ).scrollTop() + 2 );
    setTimeout( function() {
        $alert.hide();
    }, 3000 );
};

halApp.showDialog = function( title, body, cbSuccess, cbCancel ) {
    var $dlg = $("#dialog-global");
    $dlg.find('#dialog-global-btn-cancel').off('click');
    $dlg.find('#dialog-global-btn-cancel').on('click', function( e ) {
        $dlg.modal('hide');
        if ( cbCancel )
            cbCancel();
    });
    $dlg.find('#dialog-global-btn-ok').off('click');
    $dlg.find('#dialog-global-btn-ok').on('click', function() {
        cbSuccess( );
    });

    $("#dialog-title" ).html( title );
    $("#dialog-body" ).html( body );

    $dlg.modal({backdrop: 'static', keyboard: true, show: true});
};
halApp.hideDialog = function() {
    var $dlg = $("#dialog-global");
    $dlg.modal('hide');
};




function getListItemByField( list, field, value ) {
    if ( !list || !list.length )
        return null;
    for ( var i = 0; i < list.length; i++ ) {
        if ( list[i][field] == value )
            return list[i];
    }
    return null;
}