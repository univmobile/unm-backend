'use strict';

halApp.controller( 'CtrlUniversityLibraries', [ '$rootScope', '$scope', '$location', 'universityLibrariesService', 'universityService', 'libraryService', '$validator', function( $rootScope, $scope, $location, universityLibrariesService, universityService, libraryService, $validator ) {

    $scope.selectedUniversityId = universityId;
    $scope.items = null;
    $scope.unis = null;
    $scope.libraries = null;
    $scope.pager = $scope.pagerCache.universityLibraries ? $scope.pagerCache.universityLibraries : new Pager();
    $scope.pagerCache.universityLibraries = null;

    $scope.loadItems = function() {
        universityLibrariesService.loadItems( isSuperAdmin, $scope.selectedUniversityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };

    universityService.loadItems( function( unis ) {
        $scope.unis = unis;
        libraryService.loadItems( function( libraries ) {
            $scope.libraries = libraries;
            $scope.loadItems();
        } );
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprim&eacute; Bibliothèque?', function() {
            universityLibrariesService.remove( item, function() {
                $scope.pagerCache.universityLibraries = null;
                halApp.hideDialog();
                halApp.showAlert("Bibliothèque supprim&eacute;!");
            } );
        } )
    };

    $scope.handleAddClicked = function( item ) {
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            item.universityId = $scope.selectedUniversityId;
            var newUniversityLibrary = new UniversityLibraries( item );
            universityLibrariesService.save( newUniversityLibrary, function( res ) {
                setTimeout( function() {
                    halApp.showAlert( "Bibliothèque &eacute;tabli!" );
                    $scope.toAdd = {};
                }, 0 )
            } );
        });
    };
    
    $scope.$watch('selectedUniversityId', function(newValue, oldValue) {
        $scope.loadItems();
    });
    
    $scope.elegibleLibraries = function() {
        if (!$scope.items) {
            return $scope.libraries;
        } else {
            var elegibles = [];
            var skip = false;
            for (var i = 0; i < $scope.libraries.length; i++) {
                for (var j = 0; j < $scope.items.length; j++) {
                    if ($scope.libraries[i].id == $scope.items[j].poiId) {
                        skip = true;
                        continue;
                    }
                }
                if (!skip) {
                    elegibles.push($scope.libraries[i]);
                }
                skip = false;
            }
            return elegibles;
        }
    };
    
} ] );
