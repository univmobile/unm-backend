'use strict';

halApp.controller( 'CtrlMenus', [ '$rootScope', '$scope', '$location', 'menuService', 'universityService', function( $rootScope, $scope, $location, menuService, universityService ) {

    $scope.items = null;
    $scope.pager = $scope.pagerCache.menus ? $scope.pagerCache.menus : new Pager();
    $scope.pagerCache.menus = null;

    $scope.loadItems = function() {
        menuService.loadItems( isSuperAdmin, universityId, $scope.pager, function( items ) {
            $scope.items = items;
        } );
    };
    
    universityService.loadItemsWithoutCrous( function( unis ) {
        $scope.loadItems();
    } );

    $scope.handleDeleteClick = function( item ) {
        halApp.showDialog( 'Confirmez l\'op&eacute;ration', 'Supprimer le menu?', function() {
            menuService.remove( item, function() {
                $scope.pagerCache.menus = null;
                halApp.hideDialog();
                halApp.showAlert("Menu supprim&eacute; !");
                $scope.loadItems();
            } );
        } )
    };

    $scope.handleEditClick = function( item ) {
        $rootScope.pagerCache.menus = $scope.pager;
        $location.path('/menus/' + (item ? item.id : 0) + '/edit');
    }
    
} ] );

halApp.controller( 'CtrlMenuEdit', [ '$scope', '$routeParams', '$validator', 'menuService', 'inactiveMenuService', 'universityService', 'textAngularManager', function(
    $scope, $routeParams, $validator, menuService, inactiveMenuService, universityService, textAngularManager ) {

    $scope.uploadUrl = baseUrl + '/api/admin/menu/upload';
    
    $scope.upload = {
      progress: false,
      lastError: null
    };
      
    $scope.groups = ['MS', 'AU', 'TT', 'MU'];

    $scope.submitTracking = false;

    $scope.itemId = parseInt( $routeParams.itemId );
    $scope.unis = null;
    $scope.inactiveMenus = null;
    $scope.temporalImageFile = '';
    
    universityService.loadItemsWithoutCrous( function( unis ) {
        for ( var j = 0; j < unis.length; j++ ) {
        	unis[j].inactiveMenu = new InactiveMenu();
        	unis[j].inactiveMenu.active = true;
        }
        $scope.unis = unis;
        menuService.getItem( $scope.itemId, function( item ) {
            $scope.item = $.extend( {}, item );
		    if ($scope.itemId == 0 && !isSuperAdmin) {
		      $scope.item.universityId = universityId;
		    }
        } );
        inactiveMenuService.loadItems(isSuperAdmin, $scope.itemId, universityId, function(inactiveMenus) {
        	$scope.inactiveMenus = inactiveMenus;
        	for ( var i = 0; i < inactiveMenus.length; i++ ) {
        		for ( var j = 0; j < unis.length; j++ ) {
                	if (unis[j].id == inactiveMenus[i].universityId) {
                		unis[j].inactiveMenu = inactiveMenus[i];
                		inactiveMenus[i].active = false;
                	}
                }
        	}
        });
    });

    $scope.validateUrlOrContent = function( item ) {
        var urlPresent = item.url && item.url.replace(' ', '');
        var contentPresent = item.content && item.content.replace(' ', '');
        return (urlPresent && !contentPresent) || (contentPresent && !urlPresent);
    };
    
    $scope.handleSaveClicked = function( item, unis ) {
        $scope.submitTracking = true;
        var isNew = !item.id;
        $validator.validate( $scope, 'item' ).success( function() {
            if ( $scope.validateUrlOrContent(item) ) {
                // Update the list of saved items
                for ( var j = 0; j < unis.length; j++ ) {
                	if (unis[j].inactiveMenu.id > 0 && (unis[j].inactiveMenu.active || item.universityId)) {
                		inactiveMenuService.remove(unis[j].inactiveMenu, function(res) {
                			// nothing to do
                		});
                	}
                	if (!item.universityId && unis[j].inactiveMenu.id == 0 && !unis[j].inactiveMenu.active) {
                		unis[j].inactiveMenu.universityId = unis[j].id;
                		unis[j].inactiveMenu.universityLink = unis[j].getLink();
                		unis[j].inactiveMenu.menuId = item.id;
                		unis[j].inactiveMenu.menuLink = item.getLink(false);
                		inactiveMenuService.save(unis[j].inactiveMenu, function(res) {
                			// nothing to do
                		});
                	}
                }
                if (isSuperAdmin || item.universityId) {
	                menuService.save( item, function( res ) {
	                    if ( isNew ) {
	                        location.hash = "#/menus/" + res.id + "/edit";
	                        setTimeout( function() {
	                            halApp.showAlert( "Menu cr&eacute;&eacute; !" );
	                        }, 0 )
	                    } else {
	                        halApp.showAlert( "Menu sauv&eacute;!" );
	                    }
	                    location.hash = "#/menus";
	                } );
                } else {
                    // Nothing to do
                	halApp.showAlert( "Menu sauv&eacute;!" );
                }
            }
        });
    };
    
    $scope.handleUniversityChanged = function( item ) {
    	if (!item.universityId) {
    		// We display the block permitting to choose the universities
    	}
    }

    $scope.isSuperAdmin = function() {
      return isSuperAdmin;
    };
    
    $scope.selectFile = function() {
      $('#imageupload input[type=file]').click();
      $scope.editorScope = textAngularManager.retrieveEditor('content').scope; 
    };
    
    $scope.openImageUploadModal = function(cb) {
      var options = { backdrop: 'static' };
      $('#imageMapModal').modal(options);
      uploadCallback = cb;
    }

    $scope.closeImageUploadModal = function() {
      $('#imageMapModal').modal('hide');
      $scope.temporalImageFile = null;
      $scope.upload.lastError = null;
      $scope.upload.progress = false;
      textAngularManager.retrieveEditor('content').scope.displayElements.text.trigger('focus');
    }

    $('#imageupload').fileupload({
      maxNumberOfFiles: 1,
      autoUpload: false,
        dataType: 'json',
        change: function (e, data) {
          $.each(data.files, function (index, file) {
            $scope.$apply(function() {
              $scope.temporalImageFile = file.name + ' (' + Math.round(file.size/1024) + 'KB)';
            });
          });
        },
        add: function(e, data) {
          $('#uploadSubmit').unbind('click');
          data.context = $('#uploadSubmit').click(function() {
            data.submit();
          });
        },     
        done: function (e, data) {
            $('#uploadSubmit').unbind('click');
            $scope.closeImageUploadModal();
            $scope.editorScope.displayElements.text.trigger('focus');
            uploadCallback(data.result.url);
            $scope.upload.lastError = null;
            $scope.upload.progress = false;
        },
        fail: function (e, data) {
            $scope.upload.lastError = "Impossible de télécharger l'image";
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $scope.$apply(function() {
              $scope.upload.progress = progress;
            });
        }
    });    
    

} ] );

var uploadCallback;
function openImageUploadModal(cb) {
      var options = { backdrop: 'static' };
      $('#imageMapModal').modal(options);
      uploadCallback = cb;
}

