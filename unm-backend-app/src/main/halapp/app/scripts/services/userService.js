halApp.model.users = [];

halApp.factory('userService', [ '$resource', function( $resource ) {
    var f = {

    };

    f.search = function( text, isGlobal, universityId, pager, cbSuccess ) {
	var searchUrl = baseUrl + (isGlobal ? "api/users/search/searchGlobalValue?val=" : ("api/users/search/searchValue?universityId=" + universityId + "&val="))  + text + "&" + pager.toQS();
	$resource( searchUrl ).get( null, function( res ) {	
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
	    
	    var users = res._embedded ? res._embedded.users : [];
            cbSuccess( users );

        });
    };



    return f;

}]);