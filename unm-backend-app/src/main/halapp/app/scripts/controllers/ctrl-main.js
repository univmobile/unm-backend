'use strict';

var modes = [ 'categories', 'comments', 'pois', 'users' ];

angular.module( 'halSearchApp' ).controller( 'MainCtrl', [ '$scope', '$resource', 'usSpinnerService',
        'categoryService', 'commentsService', 'poiService', 'userService', 'managedEntity',
    function( $scope, $resource, usSpinnerService, categoryService, commentsService, poiService, userService, managedEntity) {
        $scope.managedEntity = managedEntity;
        $scope.baseUrl = baseUrl;
        $scope.searchText = "";
        $scope.pager = new Pager();

        $scope.cats = [];
        $scope.comments = [];

        $( '#main-tabs a' ).click( function( e ) {
            e.preventDefault();
            $scope.searchText = "";
            $scope.pager = new Pager();
            $( this ).tab( 'show' );
            $scope.showCurrentTab();
        } );
        setTimeout( function() {
            $( "#main-search" ).focus();
            if (!$scope.managedEntity.showsAll) {
                $( '#main-tabs li a[aria-controls="' + $scope.managedEntity.tab + '"]' ).click();
            }
            $scope.showCurrentTab();
        } );


        $scope.handleSearchClick = function() {
            $scope.pager = new Pager();
            $scope.showCurrentTab();
        };

        $scope.showCurrentTab = function( fromPager ) {
            if ( getCurrentMode() == "categories" ) {
                $scope.showCats( fromPager );
            } else if ( getCurrentMode() == "comments" ) {
                $scope.showComments( fromPager );
            } else if ( getCurrentMode() == "pois" ) {
                $scope.showPois( fromPager );
            } else if ( getCurrentMode() == "users" ) {
                $scope.showUsers( fromPager );
            }
            $( "#main-search" ).focus();
        };

        $scope.showCats = function( fromPager ) {
            if ( fromPager || !$scope.cats || !$scope.cats.length )
                usSpinnerService.spin( 'tab-spinner' );
            categoryService.search( $scope.searchText, $scope.pager, function( items ) {
                $scope.cats = items;
                usSpinnerService.stop( 'tab-spinner' );
            } );
        };
        $scope.showComments = function( fromPager ) {
            if ( fromPager || !$scope.comments || !$scope.comments.length )
                usSpinnerService.spin( 'tab-spinner' );
            commentsService.search( $scope.searchText, isSuperAdmin, universityId, $scope.pager, function( items ) {
                $scope.comments = items;
                usSpinnerService.stop( 'tab-spinner' );
            } );
        };
        $scope.showPois = function( fromPager ) {
            if ( fromPager || !$scope.pois || !$scope.pois.length )
                usSpinnerService.spin( 'tab-spinner' );
            poiService.search( $scope.searchText, isSuperAdmin, universityId, $scope.pager, function( items ) {
                $scope.pois = items;
                usSpinnerService.stop( 'tab-spinner' );
            } );
        };
        $scope.showUsers = function( fromPager ) {
            if ( fromPager || !$scope.users || !$scope.users.length )
                usSpinnerService.spin( 'tab-spinner' );
            userService.search( $scope.searchText, isSuperAdmin, universityId, $scope.pager, function( items ) {
                $scope.users = items;
                usSpinnerService.stop( 'tab-spinner' );
            } );
        };

        $scope.toggleCommentStatus = function( comment ) {
            commentsService.changeStatus(comment, comment.active, function() { 
                // nothing to do
            } );
        };
        
        var getCurrentMode = function() {
            return $( '#main-tabs li.active a' ).attr( 'aria-controls' );
        };

        /*
         $scope.cats = [
         { name: 'Cat 1', description: 'This is a first category', active: true},
         { name: 'Some other cat', description: 'Another category, much better than first', active: true},
         { name: 'Third Cat', description: 'This is not very good cat', active: false},
         { name: 'Category Four', description: 'Nice Category', active: true}
         ];
         */

/*
        $scope.comments = [
            {date: '2015-01-12', twittername: 'johnsmith', username: 'user1', message: 'Test Messge', active: false },
            {date: '2015-01-12', twittername: 'MyNick', username: 'user2', message: 'What is going on?', active: true },
            {date: '2015-01-12', twittername: 'jajamodo', username: 'user3', message: 'Something is happening!', active: true },
            {date: '2015-01-12', twittername: 'qucumber', username: 'user4', message: 'The weather is fine', active: false },
            {date: '2015-01-12', twittername: 'muchacho', username: 'user5', message: 'The dog is barking', active: true }
        ];
*/

/*
        $scope.pois = [
            { university: { title: 'University 1' }, name: 'POI 1', address: 'Long Street 35' },
            { university: { title: 'University 2' }, name: 'POI 2', address: 'Long Street 35' },
            { university: { title: 'University 3' }, name: 'POI 3', address: 'Long Street 35' },
            { university: { title: 'University 4' }, name: 'POI 4', address: 'Long Street 35' }
        ];
*/

/*
        $scope.users = [
            {id: 1, username: 'User 1', role: 'ADMIN', email: 'user1@example.com'},
            {id: 2, username: 'User 2', role: 'USER', email: 'user2@example.com'},
            {id: 3, username: 'User 3', role: 'GUEST', email: 'use31@example.com'},
            {id: 4, username: 'User 4', role: 'USER', email: 'user4@example.com'},
            {id: 5, username: 'User 5', role: 'USER', email: 'user5@example.com'},
        ];
*/


    } ] );
