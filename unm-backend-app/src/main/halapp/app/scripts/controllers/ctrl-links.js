'use strict';

halApp.controller( 'CtrlLinks', [ '$rootScope', '$scope', '$location', 'linkService', 'universityService', function( $rootScope, $scope, $location, linkService, universityService ) {

    $scope.items = null;
    $scope.pager = $scope.pagerCache.links ? $scope.pagerCache.links : new Pager();
    $scope.pagerCache.links = null;

    $scope.loadItems = function() {
        linkService.loadItems( isSuperAdmin, universityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };

    universityService.loadItems( function( unis ) {
	$scope.loadItems();
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprimer la  M&eacute;diathèque?', function() {
            linkService.remove( item, function() {
                $scope.pagerCache.links = null;
                halApp.hideDialog();
                halApp.showAlert("Mediathèque supprim&eacute;e !");
                $scope.loadItems();
            } );
        } )
    };

    $scope.handleEditClick = function( item ) {
        $rootScope.pagerCache.links = $scope.pager;
        $location.path('/links/' + (item ? item.id : 0) + '/edit');
    }

} ] );
halApp.controller( 'CtrlLinkEdit', [ '$scope', '$routeParams', '$validator', 'linkService', 'universityService', function(
    $scope, $routeParams, $validator, linkService, universityService ) {

    $scope.itemId = parseInt( $routeParams.itemId );
    $scope.unis = null;
    universityService.loadItems( function( unis ) {
        $scope.unis = unis;
        linkService.getItem( $scope.itemId, function( item ) {
            $scope.item = $.extend( {}, item );
	    if ($scope.itemId == 0 && !isSuperAdmin) {
	      $scope.item.universityId = universityId;
	    }
        } );
    });


    $scope.handleSaveClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            linkService.save( item, function( res ) {
                if ( isNew ) {
                    location.hash = "#/links/" + res.id + "/edit";
                    setTimeout( function() {
                        halApp.showAlert( "M&eacute;diathèque cr&eacute;e !" );
                    }, 0 )
                } else {
                    halApp.showAlert( "Mediathèque sauv&eacute;e !" );
                }
                location.hash = "#/links";
            } );
        });
    };

    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };


} ] );
