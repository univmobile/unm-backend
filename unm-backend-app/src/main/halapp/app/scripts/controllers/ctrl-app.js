angular.module( 'halSearchApp' ).controller( "CtrlApp", ["$rootScope", "$scope", "$route", "$http", function( $rootScope, $scope, $route, $http ) {

    var initAppData = function() {
    };


    $scope.$on( "$routeChangeSuccess", function( $currentRoute, $previousRoute ) {
        var hashParts = location.hash ? location.hash.split("/") : [];
        if ( hashParts.length )
            $scope.mainMenu = hashParts[1];
        if ( hashParts.length > 1 )
            $scope.subMenu = hashParts[2];
    } );


    $scope.isMenuActive = function( menuItem ) {
        if ( menuItem.url ) {
            return menuItem.url.indexOf("#/" + $scope.mainMenu ) == 0;
        } else if ( menuItem.children && menuItem.children.length ){
            return menuItem.children[0].url.indexOf("#/" + $scope.mainMenu ) == 0;
        }
        return false;
    };

    $scope.isSubmenuActive = function( menuChild ) {
        if ( menuChild.url ) {
            return menuChild.url.indexOf("#/" + $scope.mainMenu + "/" + $scope.subMenu ) == 0;
        }
        return false;
    };

    $rootScope.pagerCache = {};
    
    initAppData();


} ] );

var Pager = function() {
    this.pageSize = 50;
    this.page = 1;
    this.totalCount = null;
    this.numPages = null;
};
Pager.prototype.toQS = function() {
    return "size=" + this.pageSize + "&page=" + (this.page - 1 );
};