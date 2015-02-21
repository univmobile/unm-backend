halApp.model.universityLibraries = null;

halApp.factory('universityLibrariesService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new UniversityLibraries( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.universityLibraries )
            halApp.model.universityLibraries.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.universityLibraries.indexOf( item );
        if ( ind > -1 )
            halApp.model.universityLibraries.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, pager, cbSuccess ) {
        /*
        var serviceUri = isSuperAdmin 
	    ? baseUrl + "api/universityLibraries/?" + pager.toQS()
	    : baseUrl + "api/universityLibraries/search/findByUniversity?universityId=" + universityId + "&" + pager.toQS();
	    */
        var serviceUri = baseUrl + "api/universityLibraries/search/findByUniversity?universityId=" + universityId + "&size=100000";
        
        $resource( serviceUri ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
            var universityLibraries = res._embedded ? res._embedded.universityLibraries : [];
            halApp.model.universityLibraries = initItems( universityLibraries );
            cbSuccess( halApp.model.universityLibraries );
        });
    };

    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new UniversityLibraries() );
        }
        if ( halApp.model.universityLibrariess ) {
            return cbSuccess( getListItemByField( halApp.model.universityLibraries, 'id', id ) );
        } else {
            $resource( baseUrl + "api/universityLibraries/"+id ).get( null, function( res ) {
                cbSuccess( new UniversityLibraries( res ) );
            });
        }
    };

    f.save = function( item, cbSuccess ) {
        if ( item.id ) {
            $http.patch( item.getUniversityLibraries( false ), item.toObject() ).success( function( res ) {
                cbSuccess( );
            });
        } else {
            $http.post( item.getListUniversityLibraries( false ), item.toObject() ).success( function( res, p2, headers ) {
                var locHeader = headers('location');
                var id = locHeader.substring( locHeader.lastIndexOf('/') + 1 );
                item.id = parseInt( id );
                addItem( item );
                cbSuccess( item );
            });

        }
    };

    f.remove = function( item, cbSuccess ) {
        $http.delete( item.getUniversityLibraries( false ) ).success( function() {
            removeItem( item );
            cbSuccess();
        } );
    };

    return f;

}]);

var UniversityLibraries = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.poiId = raw ? raw.poiId : 0;
    this.universityId = raw ? raw.universityId : 0;
};

UniversityLibraries.prototype.toObject = function() {
    var uni = this.getUni();
    var poi = this.getPoi();
    return {
        id: this.id ? this.id : null,
        poi: poi ? poi.getLink() : null,
        university: uni ? uni.getLink() : null
    }
};
UniversityLibraries.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
UniversityLibraries.prototype.getPoi = function() {
    return getListItemByField( halApp.model.libraries, 'id', this.poiId );
};
UniversityLibraries.prototype.getUniversityLibraries = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/universityLibraries/"+this.id;
};
UniversityLibraries.prototype.getListUniversityLibraries = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/universityLibraries/";
};