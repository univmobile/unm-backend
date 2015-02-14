'use strict';

halApp.controller( 'CtrlNotifications', [ '$rootScope', '$scope', '$location', 'notificationService', 'universityService', function(  $rootScope, $scope, $location, notificationService, universityService ) {

    $scope.items = null;
    $scope.pager = $scope.pagerCache.notifications ? $scope.pagerCache.notifications : new Pager();
    $scope.pagerCache.notifications = null;    

    $scope.loadItems = function() {
        notificationService.loadItems( isSuperAdmin, universityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };
    
    universityService.loadItems( function( unis ) {
        $scope.loadItems();
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; notification?', function() {
            notificationService.remove( item, function() {
                $scope.pagerCache.notifications = null;
                halApp.hideDialog();
                halApp.showAlert( "Notification supprim&eacute;!" );
                $scope.loadItems();
            } );
        } )
    };

    $scope.handleEditClick = function( item ) {
        $rootScope.pagerCache.notifications = $scope.pager;
        $location.path('/notifications/' + (item ? item.id : 0) + '/edit');
    }
  
} ] );

halApp.controller( 'CtrlNotificationEdit', [ '$scope', '$routeParams', '$validator', 'notificationService', 'universityService', function( $scope, $routeParams, $validator, notificationService, universityService ) {

    $scope.itemId = parseInt( $routeParams.itemId );
    $scope.unis = null;
    universityService.loadItems( function( unis ) {
        $scope.unis = unis;
        notificationService.getItem( $scope.itemId, function( item ) {
            $scope.item = $.extend( {}, item );
	    if ($scope.itemId == 0 && !isSuperAdmin) {
	      $scope.item.universityId = universityId;
	    }
        } );
    } );


    $scope.handleSaveClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            notificationService.save( item, function( res ) {
                if ( isNew ) {
                    location.hash = "#/notifications/" + res.id + "/edit";
                    setTimeout( function() {
                        halApp.showAlert( "Notification &eacute;tabli!" );
                    }, 0 )
                } else {
                    halApp.showAlert( "Notification sauv&eacute;!" );
                }
                location.hash = "#/notifications";
            } );
        } )
            .error( function() {} )
        ;

    };

    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };


} ] );
