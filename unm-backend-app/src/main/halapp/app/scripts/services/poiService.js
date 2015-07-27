halApp.model.pois = [];

halApp.factory('poiService', [ '$resource', function( $resource ) {
    var f = {

    };


    f.search = function( text, isGlobal, universityId, pager, cbSuccess ) {
	var searchUrl = baseUrl + (isGlobal ? "api/pois/search/searchGlobalValue?sort=category&val=" : ("api/pois/search/searchValue?sort=category&universityId=" + universityId + "&val="))  + text + "&" + pager.toQS();
        $resource( searchUrl ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
	    var pois = res._embedded ? res._embedded.pois : [];
            cbSuccess( pois );
        });
    };



    return f;

}]);