'use strict';

halApp.controller( 'CtrlMenus', [ '$scope', 'menuService', 'universityService', function( $scope, menuService, universityService ) {

    $scope.items = null;

    universityService.loadItems( function( unis ) {
        menuService.loadItems( isSuperAdmin, universityId, function( items ) {
            $scope.items = items;
        } );
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; Menu?', function() {
            menuService.remove( item, function() {
                halApp.hideDialog();
                halApp.showAlert("Menu supprim&eacute;!");
            } );
        } )
    }

} ] );
halApp.controller( 'CtrlMenuEdit', [ '$scope', '$routeParams', '$validator', 'menuService', 'universityService', function(
    $scope, $routeParams, $validator, menuService, universityService ) {

    $scope.groups = ['MS', 'AU', 'TT', 'MU'];


    $scope.itemId = parseInt( $routeParams.itemId );
    $scope.unis = null;
    universityService.loadItems( function( unis ) {
        $scope.unis = unis;
        menuService.getItem( $scope.itemId, function( item ) {
            $scope.item = $.extend( {}, item );
	    if ($scope.itemId == 0 && !isSuperAdmin) {
	      $scope.item.universityId = universityId;
	    }
        } );
    });


    $scope.handleSaveClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            menuService.save( item, function( res ) {
                if ( isNew ) {
                    location.hash = "#/menus/" + res.id + "/edit";
                    setTimeout( function() {
                        halApp.showAlert( "Menu &eacute;tabli!" );
                    }, 0 )
                } else {
                    halApp.showAlert( "Menu sauv&eacute;!" );
                }
            } );
        });
    };

    
    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };

} ] );
