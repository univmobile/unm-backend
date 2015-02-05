'use strict';

halApp.controller( 'CtrlNotifications', [ '$scope', 'notificationService', 'universityService', function( $scope, notificationService, universityService ) {

    $scope.items = null;

    universityService.loadItems( function( unis ) {
        notificationService.loadItems( isSuperAdmin, universityId, function( items ) {
            $scope.items = items;
        } );
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; notification?', function() {
            notificationService.remove( item, function() {
                halApp.hideDialog();
                halApp.showAlert( "Notification supprim&eacute;!" );
            } );
        } )
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
            } );
        } )
            .error( function() {} )
        ;

    };

    $scope.isSuperAdmin = function() {
	return isSuperAdmin;
    };


} ] );
