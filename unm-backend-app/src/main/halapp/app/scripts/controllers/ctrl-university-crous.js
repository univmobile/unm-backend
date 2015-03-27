'use strict';

halApp.controller( 'CtrlUniversityCrous', [ '$rootScope', '$scope', '$location', 'universityCrousService', 'universityService', '$validator', function( $rootScope, $scope, $location, universityCrousService, universityService, $validator ) {
    $scope.selectedUniversityId = universityId;
    $scope.items = null;
    $scope.unis = null;
    $scope.crous = null;
    $scope.pager = $scope.pagerCache.universityCrous ? $scope.pagerCache.universityCrous : new Pager();
    $scope.pagerCache.universityCrous = null;

    $scope.loadItems = function() {
        universityCrousService.loadItems( isSuperAdmin, $scope.selectedUniversityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };

    universityService.loadItemsWithoutCrous( function( unis ) {
        $scope.unis = unis;
        universityService.loadCrous( function( crous ) {
            $scope.crous = crous;
            $scope.loadItems();
        } );
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Veuillez confirmez l\'op&eacute;ration', 'D&eacute;lier ce CROUS de l\'Universit&eacute; ?', function() {
            universityCrousService.remove( item, function() {
                $scope.pagerCache.universityCrous = null;
                halApp.hideDialog();
                halApp.showAlert("L\'universit&eacute; n'est plus li&eacute;e au CROUS !");
            } );
        } )
    };

    $scope.handleAddClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            item.universityId = $scope.selectedUniversityId;
            var newUniversityCrous = new UniversityCrous( item );
            universityCrousService.save( newUniversityCrous, function( res ) {
                setTimeout( function() {
                    halApp.showAlert( "L\'universit&eacute; est maintenant li&eacute;e au CROUS !" );
                    $scope.toAdd = {};
                }, 0 )
            } );
        });
    };
    
    $scope.$watch('selectedUniversityId', function(newValue, oldValue) {
        $scope.loadItems();
    });
    
    $scope.elegibleCrous = function() {
        if (!$scope.items) {
            return $scope.crous;
        } else {
            var elegibles = [];
            var skip = false;
            if ($scope.crous != null) {
	            for (var i = 0; i < $scope.crous.length; i++) {
	                for (var j = 0; j < $scope.items.length; j++) {
	                    if ($scope.crous[i].id == $scope.items[j].crousId) {
	                        skip = true;
	                        continue;
	                    }
	                }
	                if (!skip) {
	                    elegibles.push($scope.crous[i]);
	                }
	                skip = false;
	            }
            }
            return elegibles;
        }
    };
    
} ] );
