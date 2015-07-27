halApp.model.menus = null;

halApp.factory('menuService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new Menu( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.menus )
            halApp.model.menus.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.menus.indexOf( item );
        if ( ind > -1 )
            halApp.model.menus.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, pager, cbSuccess ) {
	var serviceUri = isSuperAdmin 
	    ? baseUrl + "api/menues/search/findByOrderByGroupingAscOrdinalAsc?" + pager.toQS()
	    : baseUrl + "api/menues/search/findByUniversityOrderByGroupingAscOrdinalAsc?universityId=" + universityId + "&" + pager.toQS();
	
	$resource( serviceUri ).get( null, function( res ) {
            pager.totalCount = res.page.totalElements;
            pager.numPages = res.page.totalPages;	  
	    var menus = res._embedded ? res._embedded.menu : [];
            halApp.model.menus = initItems( menus );
            cbSuccess( halApp.model.menus );
        });
    };

    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new Menu() );
        }
        if ( halApp.model.menus ) {
            return cbSuccess( getListItemByField( halApp.model.menus, 'id', id ) );
        } else {
            $resource( baseUrl + "api/menues/"+id ).get( null, function( res ) {
                cbSuccess( new Menu( res ) );
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

var Menu = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.name = raw ? raw.name : '';
    this.universityId = raw ? raw.universityId : 0;
    this.grouping = raw ? raw.grouping : 'MS';
    this.active = raw ? raw.active : true;
    this.ordinal = raw ? raw.ordinal : 0;
    this.url = raw ? raw.url : '';
    this.content = raw ? raw.content : '';
    this.inactiveMenus = raw ? raw.inactiveMenus : null;
};

Menu.prototype.toObject = function() {
    var uni = this.getUni();
    return {
        id: this.id ? this.id : null,
        name: this.name,
        university: uni ? uni.getLink() : null,
        grouping: this.grouping,
        active: this.active,
        ordinal: this.ordinal,
        url: this.url,
        content: this.content
    }
};
Menu.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};

Menu.prototype.getLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/menues/"+this.id;
};
Menu.prototype.getListLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/menues/";
};