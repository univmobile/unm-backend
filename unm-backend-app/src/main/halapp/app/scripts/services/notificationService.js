halApp.model.notifications = null;

halApp.factory('notificationService', [ '$resource', '$http', 'universityService', function( $resource, $http, universityService ) {
    var f = {

    };

    var initItems = function( rawItems ) {
        var res = [];
        for ( var i = 0; i < rawItems.length; i++ ) {
            var item = new Notification( rawItems[i] );
            res.push( item );
        }
        return res;
    };

    var addItem = function( item ) {
        if ( halApp.model.notifications )
            halApp.model.notifications.push( item );
    };

    var removeItem = function( item ) {
        var ind = halApp.model.notifications.indexOf( item );
        if ( ind > -1 )
            halApp.model.notifications.splice( ind, 1 );
    };

    f.loadItems = function( isSuperAdmin, universityId, cbSuccess ) {
	var serviceUri = isSuperAdmin 
	    // ? baseUrl + "api/notifications/?size=1000"
		? baseUrl + "api/notifications/findByOrderByCreatedOnDesc?size=1000"
	    : baseUrl + "api/notifications/search/findByUniversityOrderByCreatedOnDesc?universityId=" + universityId + "&size=1000";
	
	$resource( serviceUri ).get( null, function( res ) {
	    var notifications = res._embedded ? res._embedded.notifications : [];
            halApp.model.notifications = initItems( notifications );
            cbSuccess( halApp.model.notifications );
        });
    };
    
    
    f.getItem = function( id, cbSuccess ) {
        if ( !id ) {
            return cbSuccess( new Notification() );
        }
        if ( halApp.model.notifications ) {
            return cbSuccess( getListItemByField( halApp.model.notifications, 'id', id ) );
        } else {
            $resource( baseUrl + "api/notifications/"+id ).get( null, function( res ) {
                cbSuccess( new Notification( res ) );
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

var Notification = function( raw ) {
    this.id = raw ? raw.id : 0;
    this.content = raw ? raw.content : "";
    this.links = raw ? raw._links : {};
    this.universityId = raw ? raw.universityId : 0;
};

Notification.prototype.toObject = function() {
    var uni = this.getUni();
    return {
        id: this.id ? this.id : null,
        content: this.content,
        university: uni ? uni.getLink() : null
    }
};
Notification.prototype.getUni = function() {
    return getListItemByField( halApp.model.universities, 'id', this.universityId );
};
Notification.prototype.getLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/notifications/"+this.id;
};
Notification.prototype.getListLink = function( isShort ) {
    return ( isShort ? "" : halApp.URL_PREF ) + "api/notifications/";
};