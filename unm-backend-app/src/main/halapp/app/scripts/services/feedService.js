halApp.model.feeds = null;

halApp.factory('feedService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new Feed( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.feeds )
            halApp.model.feeds.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.feeds.indexOf( item );
        if ( ind > -1 )
            halApp.model.feeds.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, pager, cbSuccess ) {
	var serviceUri = isSuperAdmin 
	    ? baseUrl + "api/feeds/?" + pager.toQS()
	    : baseUrl + "api/feeds/search/findByUniversityOrderByCreatedOnDesc?universityId=" + universityId + "&" + pager.toQS();
	    
        $resource( serviceUri ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;
            var feeds = res._embedded ? res._embedded.feeds : [];
            halApp.model.feeds = initItems( feeds );
            cbSuccess( halApp.model.feeds );
        });
    };

    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new Feed() );
        }
        if ( halApp.model.feeds ) {
            return cbSuccess( getListItemByField( halApp.model.feeds, 'id', id ) );
        } else {
            $resource( baseUrl + "api/feeds/"+id ).get( null, function( res ) {
                cbSuccess( new Feed( res ) );
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

var Feed = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.name = raw ? raw.name : '';
    this.url = raw ? raw.url : '';
    this.type = raw ? raw.type : 'RSS';
    this.universityId = raw ? raw.universityId : 0;
    this.active = raw ? raw.active : true;
};

Feed.prototype.toObject = function() {
    var uni = this.getUni();
    return {
        id: this.id ? this.id : null,
        name: this.name,
        url: this.url,
        type: this.type,
        university: uni ? uni.getLink() : null,
        active: this.active
    }
};
Feed.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
Feed.prototype.getLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/feeds/"+this.id;
};
Feed.prototype.getListLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/feeds/";
};