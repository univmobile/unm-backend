'use strict';

halApp.controller( 'CtrlMenus', [ '$rootScope', '$scope', '$location', 'menuService', 'universityService', function( $rootScope, $scope, $location, menuService, universityService ) {

    $scope.items = null;
    $scope.pager = $scope.pagerCache.menus ? $scope.pagerCache.menus : new Pager();
    $scope.pagerCache.menus = null;

    $scope.loadItems = function() {
        menuService.loadItems( isSuperAdmin, universityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };
    
    universityService.loadItems( function( unis ) {
        $scope.loadItems();
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; Menu?', function() {
            menuService.remove( item, function() {
                $scope.pagerCache.menus = null;
                halApp.hideDialog();
                halApp.showAlert("Menu supprim&eacute;!");
                $scope.loadItems();
            } );
        } )
    };

    $scope.handleEditClick = function( item ) {
        $rootScope.pagerCache.menus = $scope.pager;
        $location.path('/menus/' + (item ? item.id : 0) + '/edit');
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
                location.hash = "#/menus";
            } );
        });
    };

    
    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };

} ] );
