halApp.model.universityLibraries = null;

halApp.factory('universityCrousService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new UniversityCrous( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.universityCrous )
            halApp.model.universityCrous.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.universityCrous.indexOf( item );
        if ( ind > -1 )
            halApp.model.universityCrous.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, pager, cbSuccess ) {
        /*
        var serviceUri = isSuperAdmin 
	    ? baseUrl + "api/universityLibraries/?" + pager.toQS()
	    : baseUrl + "api/universityLibraries/search/findByUniversity?universityId=" + universityId + "&" + pager.toQS();
	    */
        var serviceUri = baseUrl + "api/universityCrouses/search/findByUniversity?universityId=" + universityId + "&size=100000";
        
        $resource( serviceUri ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
            var universityCrous = res._embedded ? res._embedded.universityCrouses : [];
            halApp.model.universityCrous = initItems( universityCrous );
            cbSuccess( halApp.model.universityCrous );
        });
    };

    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new UniversityCrous() );
        }
        if ( halApp.model.universityCrous ) {
            return cbSuccess( getListItemByField( halApp.model.universityCrous, 'id', id ) );
        } else {
            $resource( baseUrl + "api/universityCrous/"+id ).get( null, function( res ) {
                cbSuccess( new UniversityCrous( res ) );
            });
        }
    };

    f.save = function( item, cbSuccess ) {
        if ( item.id ) {
            $http.patch( item.getUniversityCrous( false ), item.toObject() ).success( function( res ) {
                cbSuccess( );
            });
        } else {
            $http.post( item.getListUniversityCrous( false ), item.toObject() ).success( function( res, p2, headers ) {
                var locHeader = headers('location');
                var id = locHeader.substring( locHeader.lastIndexOf('/') + 1 );
                item.id = parseInt( id );
                addItem( item );
                cbSuccess( item );
            });

        }
    };

    f.remove = function( item, cbSuccess ) {
        $http.delete( item.getUniversityCrous( false ) ).success( function() {
            removeItem( item );
            cbSuccess();
        } );
    };

    return f;

}]);

var UniversityCrous = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.crousId = raw ? raw.crousId : 0;
    this.universityId = raw ? raw.universityId : 0;
};

UniversityCrous.prototype.toObject = function() {
    var uni = this.getUni();
    var crousO = this.getCrous();
    return {
        id: this.id ? this.id : null,
        crous: crousO ? crousO.getLink() : null,
        university: uni ? uni.getLink() : null
    }
};
UniversityCrous.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
UniversityCrous.prototype.getCrous = function() {
    return getListItemByField( halApp.model.crous, 'id', this.crousId );
};
UniversityCrous.prototype.getUniversityCrous = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/universityCrouses/"+this.id;
};
UniversityCrous.prototype.getListUniversityCrous = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/universityCrouses/";
};