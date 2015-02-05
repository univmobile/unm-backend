halApp.model.categories = [];

halApp.factory('categoryService', [ '$resource', function( $resource ) {
    var f = {

    };

    f.search = function( text, pager, cbSuccess ) {
        $resource( baseUrl + "api/categories/search/searchValue?val=" + text + "&" + pager.toQS() ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
	    var categories = res._embedded ? res._embedded.categories : [];
            cbSuccess( categories );	    
        });
    };


    return f;

}]);