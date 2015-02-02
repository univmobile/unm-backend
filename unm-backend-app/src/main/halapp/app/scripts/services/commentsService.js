halApp.model.comments = [];

halApp.factory('commentsService', [ '$resource', function( $resource ) {
    var f = {

    };

    f.search = function( text, isGlobal, universityId, pager, cbSuccess ) {
	var searchUrl = baseUrl + (isGlobal ? "api/comments/search/searchGlobalValue?val=" : ("api/comments/search/searchValue?universityId=" + universityId + "&val="))  + text + "&" + pager.toQS();
	$resource( searchUrl ).get( null, function( res ) {	
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
	    var comments = res._embedded ? res._embedded.comments : [];
            cbSuccess( comments );	    
        });
    };




    return f;

}]);