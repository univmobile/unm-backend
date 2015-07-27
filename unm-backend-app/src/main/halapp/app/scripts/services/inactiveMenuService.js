halApp.model.inactiveMenus = null;

halApp.factory('inactiveMenuService', [ '$resource', '$http', function( $resource, $http ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new InactiveMenu( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.inactiveMenus )
            halApp.model.inactiveMenus.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.inactiveMenus.indexOf( item );
        if ( ind > -1 )
            halApp.model.inactiveMenus.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, menuId, universityId, cbSuccess ) {
        
        var serviceUri = isSuperAdmin 
	    	? baseUrl + "api/inactiveMenus/search/findByMenu?menuId=" + menuId
	        : baseUrl + "api/inactiveMenus/search/findByUniversityAndMenu?universityId=" + universityId + "&menuId=" + menuId;
	    
	
        $resource( serviceUri ).get( null, function( res ) {
	        var inactiveMenus = res._embedded ? res._embedded.inactiveMenus : [];
            halApp.model.inactiveMenus = initItems( inactiveMenus );
            cbSuccess( halApp.model.inactiveMenus );
        });
    };
    
    
    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new InactiveMenu() );
        }
        if ( halApp.model.inactiveMenus ) {
            return cbSuccess( getListItemByField( halApp.model.inactiveMenus, 'id', id ) );
        } else {
            $resource( baseUrl + "api/inactiveMenus/"+id ).get( null, function( res ) {
                cbSuccess( new InactiveMenu( res ) );
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
                //var locHeader = headers('location');
                //var id = locHeader.substring( locHeader.lastIndexOf('/') + 1 );
                //item.id = parseInt( id );
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

var InactiveMenu = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.links = raw ? raw._links : {};
    this.universityId = raw ? raw.universityId : 0;
    this.universityTitle = raw ? raw.universityTitle : '';
    this.menuId = raw ? raw.menuId : 0;
    this.universityLink = raw ? raw._links.university.href : '';
    this.menuLink = raw ? raw._links.menu.href : '';
};

InactiveMenu.prototype.toObject = function() {
    //var uni = this.getUni();
    //var menu = this.getMenu();
    return {
        id: this.id ? this.id : null,
        menu: this.menuId ? this.menuLink : null,
        university: this.universityId ? this.universityLink : null
        //university: uni ? uni.getLink() : null
    }
};
/*InactiveMenu.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
InactiveMenu.prototype.getMenu = function() {
	return getListItemByField( halApp.model.menus, 'id', this.menuId );
}*/
InactiveMenu.prototype.getLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/inactiveMenus/"+this.id;
};
InactiveMenu.prototype.getListLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/inactiveMenus/";
};