halApp.model.links = null;

halApp.factory('linkService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new Link( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.links )
            halApp.model.links.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.links.indexOf( item );
        if ( ind > -1 )
            halApp.model.links.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, pager, cbSuccess ) {
	var serviceUri = isSuperAdmin 
	    ? baseUrl + "api/links/?" + pager.toQS()
	    : baseUrl + "api/links/search/findByUniversity?universityId=" + universityId + "&" + pager.toQS();
	    
        $resource( serviceUri ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
            var links = res._embedded ? res._embedded.links : [];
            halApp.model.links = initItems( links );
            cbSuccess( halApp.model.links );
        });
    };

    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new Link() );
        }
        if ( halApp.model.links ) {
            return cbSuccess( getListItemByField( halApp.model.links, 'id', id ) );
        } else {
            $resource( baseUrl + "api/links/"+id ).get( null, function( res ) {
                cbSuccess( new Link( res ) );
            });
        }
    };

    f.save = function( item, cbSuccess ) {
        if ( item.id ) {
            $http.patch( item.getLink( false ), item.toObject() ).success( function( res ) {
                cbSuccess( );
            });
        } else {
            $http.post( item.getListLink( false ), item.toObject() ).success( function( res, p2, headers ) {
                var locHeader = headers('location');
                var id = locHeader.substring( locHeader.lastIndexOf('/') + 1 );
                item.id = parseInt( id );
                addItem( item );
                cbSuccess( item );
            });

        }
    };

    f.remove = function( item, cbSuccess ) {
        $http.delete( item.getLink( false ) ).success( function() {
            removeItem( item );
            cbSuccess();
        } );
    };

    return f;

}]);

var Link = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.label = raw ? raw.label : '';
    this.url = raw ? raw.url : '';
    this.universityId = raw ? raw.universityId : 0;
};

Link.prototype.toObject = function() {
    var uni = this.getUni();
    return {
        id: this.id ? this.id : null,
        label: this.label,
        url: this.url,
        university: uni ? uni.getLink() : null
    }
};
Link.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
Link.prototype.getLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/links/"+this.id;
};
Link.prototype.getListLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/links/";
};