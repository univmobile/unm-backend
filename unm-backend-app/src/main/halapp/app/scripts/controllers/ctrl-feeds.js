'use strict';

halApp.controller( 'CtrlFeeds', [ '$scope', 'feedService', 'universityService', function( $scope, feedService, universityService ) {

    $scope.items = null;

    universityService.loadItems( function( unis ) {
        feedService.loadItems( isSuperAdmin, universityId, function( items ) {
            $scope.items = items;
        } );
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; Flux?', function() {
            feedService.remove( item, function() {
                halApp.hideDialog();
                halApp.showAlert("Flux supprim&eacute;!");
            } );
        } )
    }

} ] );
halApp.controller( 'CtrlFeedEdit', [ '$scope', '$routeParams', '$validator', 'feedService', 'universityService', function(
    $scope, $routeParams, $validator, feedService, universityService ) {

    $scope.types = ['RSS', 'RESTO'];


    $scope.itemId = parseInt( $routeParams.itemId );
    $scope.unis = null;
    universityService.loadItems( function( unis ) {
        $scope.unis = unis;
        feedService.getItem( $scope.itemId, function( item ) {
            $scope.item = $.extend( {}, item );
	    if ($scope.itemId == 0 && !isSuperAdmin) {
	      $scope.item.universityId = universityId;
	    }
        } );
    });


    $scope.handleSaveClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            feedService.save( item, function( res ) {
                if ( isNew ) {
                    location.hash = "#/feeds/" + res.id + "/edit";
                    setTimeout( function() {
                        halApp.showAlert( "Flux &eacute;tabli!" );
                    }, 0 )
                } else {
                    halApp.showAlert( "Flux sauv&eacute;!" );
                }
            } );
        });
    };

    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };


} ] );
