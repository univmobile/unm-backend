halApp.model.universities = null;

halApp.factory( 'universityService', [ '$resource', function( $resource ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            res.push( new University( rawItems[i] ) );
        }
        return res;
    };

    f.loadItems = function( cbSuccess, force ) {
        if ( !halApp.model.universities || force ) {
            $resource( baseUrl + "api/universities/search/getAuthorizedUniversities" ).get( null, function( res ) {
                halApp.model.universities = initItems( res._embedded ? res._embedded.universities : [] );
                if ( cbSuccess )
                    cbSuccess( halApp.model.universities );
            } );
        } else {
            if ( cbSuccess )
                return cbSuccess( halApp.model.universities );
        }
    };


    f.getItem = function( id ) {
        return getListItemByField( halApp.model.universities, 'id', id );
    };

    return f;

}] );

var University = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.title = raw ? raw.title : '';
};
University.prototype.getLink = function() {
    return halApp.URL_PREF + "api/universities/" + this.id;
};