halApp.model.users = [];

halApp.factory('userService', [ '$resource', function( $resource ) {
    var f = {

    };

    f.search = function( text, roles, isGlobal, universityId, pager, cbSuccess ) {
    if (roles != null && Array.isArray(roles) && roles.length > 0) {
    	var searchUrl = baseUrl + (isGlobal ? "api/users/search/searchGlobalValueInRoles?val=" : ("api/users/search/searchValue?universityId=" + universityId + "&val="))  + text + "&" + pager.toQS();
        searchUrl += "&roles=" + roles.join("&roles=");
    } else {
    	var searchUrl = baseUrl + (isGlobal ? "api/users/search/searchGlobalValue?val=" : ("api/users/search/searchValue?universityId=" + universityId + "&val="))  + text + "&" + pager.toQS();
    }
	$resource( searchUrl ).get( null, function( res ) {	
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
	    
	    var users = res._embedded ? res._embedded.users : [];
            cbSuccess( users );

        });
    };



    return f;

}]);