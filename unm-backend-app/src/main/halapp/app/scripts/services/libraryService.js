halApp.model.libraries = null;

halApp.factory( 'libraryService', [ '$resource', function( $resource ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            res.push( new Library( rawItems[i] ) );
        }
        return res;
    };

    f.loadItems = function( cbSuccess, force ) {
        if ( !halApp.model.libraries || force ) {
            $resource( baseUrl + "api/pois/search/findAllLibraries" ).get( null, function( res ) {
                halApp.model.libraries = initItems( res._embedded ? res._embedded.pois : [] );
                if ( cbSuccess )
                    cbSuccess( halApp.model.libraries );
            } );
        } else {
            if ( cbSuccess )
                return cbSuccess( halApp.model.libraries );
        }
    };


    f.getItem = function( id ) {
        return getListItemByField( halApp.model.libraries, 'id', id );
    };

    return f;

}] );

var Library = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.name = raw ? raw.name : '';
};
Library.prototype.getLink = function() {
    return halApp.URL_PREF + "api/pois/" + this.id;
};