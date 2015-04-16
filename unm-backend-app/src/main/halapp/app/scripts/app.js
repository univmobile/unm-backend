'use strict';

var toInject = ['ngCookies', 'ngResource', 'ngRoute', 'ngSanitize', 'hateoas', 'ui.bootstrap', 'angular-loading-bar', 'angularSpinner', 'validator', 'validator.rules', 'textAngular'];
if (typeof isDev == 'undefined' || !isDev) {
    toInject.push('halApp.templates');
}
var halApp = angular.module( 'halSearchApp', toInject);
halApp.model = {};
halApp.URL_PREF = baseUrl;

halApp.config( [ '$routeProvider', 'HateoasInterceptorProvider', 'HateoasInterfaceProvider', '$validatorProvider', function( $routeProvider, HateoasInterceptorProvider, HateoasInterfaceProvider, $validatorProvider ) {
    $routeProvider
        .when( '/main', {
            templateUrl: 'views/main.html',
            controller: 'MainCtrl',
            resolve: { managedEntity: function() { return { showsAll: true, title: 'Recherche avancée' } } }
        } )
        .when( '/manage/comments', {
            templateUrl: 'views/main.html',
            controller: 'MainCtrl',
            resolve: { managedEntity: function() { return { showsAll: false, tab: 'comments', title: 'Commentaires' } } }
        } )
        .when( '/manage/users', {
            templateUrl: 'views/main.html',
            controller: 'MainCtrl',
            resolve: { managedEntity: function() { return { showsAll: false, tab: 'users', title: 'Utilisateurs' } } }
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
        .when( '/links', {
            templateUrl: 'views/links.html', controller: 'CtrlLinks'
        } )
        .when( '/links/:itemId/edit', {
            templateUrl: 'views/link-edit.html', controller: 'CtrlLinkEdit'
        } )
        .when( '/menus', {
            templateUrl: 'views/menus.html', controller: 'CtrlMenus'
        } )
        .when( '/menus/:itemId/edit', {
            templateUrl: 'views/menu-edit.html', controller: 'CtrlMenuEdit'
        } )
        .when( '/university-libraries', {
            templateUrl: 'views/university-libraries.html', controller: 'CtrlUniversityLibraries'
        } )
        .when( '/university-crous', {
            templateUrl: 'views/university-crous.html', controller: 'CtrlUniversityCrous'
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

halApp.config(function($provide){
    $provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions){
      taRegisterTool('uploadImage', {
        iconclass: 'fa fa-picture-o',
        tooltiptext: 'Upload Image',
        action: function() {
          var editor = this.$editor;
          openImageUploadModal(function(imageLink) {
            editor().wrapSelection('insertImage', imageLink, true);
          });
          return true;
        }/*,
        onElementSelect: {
          element: 'img',
          action: imgOnSelectAction
        }*/
      });
      taOptions.toolbar[1].push('uploadImage');
      return taOptions;
    }]);
});

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