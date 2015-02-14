'use strict';

halApp.controller( 'CtrlFeeds', [ '$rootScope', '$scope', '$location', 'feedService', 'universityService', function( $rootScope, $scope, $location, feedService, universityService ) {

    $scope.items = null;
    $scope.pager = $scope.pagerCache.feeds ? $scope.pagerCache.feeds : new Pager();
    $scope.pagerCache.feeds = null;

    $scope.loadItems = function() {
        feedService.loadItems( isSuperAdmin, universityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };

    universityService.loadItems( function( unis ) {
	$scope.loadItems();
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; Flux?', function() {
            feedService.remove( item, function() {
                $scope.pagerCache.feeds = null;
                halApp.hideDialog();
                halApp.showAlert("Flux supprim&eacute;!");
            } );
        } )
    }

    $scope.handleEditClick = function( item ) {
        $rootScope.pagerCache.feeds = $scope.pager;
        $location.path('/feeds/' + (item ? item.id : 0) + '/edit');
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
                location.hash = "#/feeds";
            } );
        });
    };

    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };


} ] );
