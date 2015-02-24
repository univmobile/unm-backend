var Univ = function(id, title, allowBonplans, regionName) {
    this.id = id;
    this.title = title;
    this.allowBonplans = allowBonplans;
    this.regionName = regionName;
};

var Comment = function(data) {
    this.id = ko.observable();
    this.title = ko.observable();
    this.message = ko.observable();
    this.author = ko.observable();
    this.active = ko.observable();
    this.createdOn = ko.observableArray([]);

    this.cache = function() {};
    
    this.update(data);
};

ko.utils.extend(Comment.prototype, {
    isNew: function() {
        return !(this.cache && this.cache.latestData && this.cache.latestData.id);
    },
    update: function (data) {
        this.id(data ? data.id : '');
        this.title(data ? data.title : '');
        this.message(data ? data.message : '');
        this.author(data ? data.author : null);
        this.active(data ? data.active : true);
        this.createdOn(data ? data.createdOn : null);
        
        this.cache.latestData = data;
    },
    revert: function() {
        this.update(this.cache.latestData);
    },
    commit: function() {
        this.cache.latestData = this.serialize();
    },
    serialize: function() {
        var serialized = ko.toJS(this);
        delete serialized['cache'];
        delete serialized['commit'];
        delete serialized['revert'];
        delete serialized['update'];        
        delete serialized['isNew'];
        return serialized;
    }
});

var ImageMap = function(data) {
    this.id = ko.observable();
    this.name = ko.observable();
    this.url = ko.observable();
    this.description = ko.observable();
    this.active = ko.observable();
    this.pois = ko.observableArray([]);
    this.temporalImageFile = ko.observable();

    this.cache = function() {};
    
    this.update(data);
};

ko.utils.extend(ImageMap.prototype, {
    isNew: function() {
        return !(this.cache && this.cache.latestData && this.cache.latestData.id);
    },
    update: function (data) {
        this.id(data ? data.id : '');
        this.name(data ? data.name : '');
        this.url(data ? data.url : '');
        this.description(data ? data.description : null);
        this.active(data ? data.active : true);
        
        this.cache.latestData = data;
    },
    revert: function() {
        this.update(this.cache.latestData);
    },
    commit: function() {
        this.cache.latestData = this.serialize();
    },
    serialize: function() {
        var serialized = ko.toJS(this);
        delete serialized['cache'];
        delete serialized['commit'];
        delete serialized['revert'];
        delete serialized['update'];      
        delete serialized['isNew'];      
        return serialized;
    }
});

var Poi = function(data) {
    this.id = ko.observable();
    this.name = ko.observable();
    this.university = ko.observable();
    this.parent = ko.observable();
    this.category = ko.observable();
    this.floor = ko.observable();
    this.openingHours = ko.observable();
    this.phones = ko.observable();
    this.address = ko.observable();
    this.email = ko.observable();
    this.itinerary = ko.observable();
    this.url = ko.observable();
    this.lat = ko.observable();
    this.lng = ko.observable();
    this.imageMap = ko.observable();
    this.qrCode = ko.observable();

    // Library supporting fields
    this.publicWelcome = ko.observable();
    this.disciplines = ko.observable();
    this.hasWifi = ko.observable();
    this.hasEthernet = ko.observable();
    this.iconRuedesfacs = ko.observable();
    this.closingHours = ko.observable();
    // End library supporting fields

    this.restoMenuUrl = ko.observable();
    this.restoId = ko.observable();
    
    this.marker = null;
    
    this.cache = function() {};
    
    this.update(data);
};

ko.utils.extend(Poi.prototype, {
    isNew: function() {
        return !(this.cache && this.cache.latestData && this.cache.latestData.id);
    },
    update: function (data) {
        this.id(data ? data.id : '');
        this.name(data ? data.name : '');
        this.university(data ? data.university : null);
        this.parent(data ? data.parent : null);
        this.category(data ? data.category : null);
        this.floor(data ? data.floor : '');
        this.openingHours(data ? data.openingHours : '');
        this.phones(data ? data.phones : '');
        this.address(data ? data.address : '');
        this.email(data ? data.email : '');
        this.itinerary(data ? data.itinerary : '');
        this.url(data ? data.url : '');
        this.lat(data ? data.lat : null);
        this.lng(data ? data.lng : null);
        this.imageMap(data ? data.imageMap : null);
        this.qrCode(data ? data.qrCode : null);
        this.publicWelcome(data ? data.publicWelcome : '');
        this.disciplines(data ? data.disciplines : '');
        this.hasWifi(data ? data.hasWifi : false);
        this.hasEthernet(data ? data.hasEthernet : false);
        this.iconRuedesfacs(data ? data.iconRuedesfacs : false);
        this.closingHours(data ? data.closingHours : '');
        this.restoMenuUrl(data ? data.restoMenuUrl : '');
        this.restoId(data ? data.restoId : '');
        
        this.cache.latestData = data;
    },
    revert: function() {
        this.update(this.cache.latestData);
    },
    commit: function() {
        this.cache.latestData = this.serialize();
    },
    serialize: function() {
        var tmpMarkerHolder = this.marker; // Marker hack for serialization: Excluding gmaps marker object
        this.marker = null;
        var serialized = ko.toJS(this);
        delete serialized['cache'];
        delete serialized['clearMarker'];
        delete serialized['commit'];
        delete serialized['createMarker'];
        delete serialized['revert'];
        delete serialized['serialize'];
        delete serialized['setActive'];
        delete serialized['setInactive'];
        delete serialized['update']        
        delete serialized['isNew']        
        delete serialized['marker']        
        this.marker = tmpMarkerHolder;
        return serialized;
    },
    createMarker: function() {
        if (this.lat() && this.lng()) {
            this.clearMarker();
            this.marker = new google.maps.Marker({
                position: new google.maps.LatLng(this.lat(), this.lng()),
                map: this.imageMap() ? imageMmap : gmap,
                title: this.name(),
                visible: true,
                draggable: true,
                icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'
            });
            this.marker.poi = this;
            var marker = this.marker;
            google.maps.event.addListener(this.marker, 'click', function(e) {
                selectNode(marker.poi, true); // FIXME: dependency
            });
            google.maps.event.addListener(this.marker, 'dragend', function(e) {
                marker.poi.lat(marker.getPosition().lat());
                marker.poi.lng(marker.getPosition().lng());
                selectNode(marker.poi, true); // FIXME: dependency
                updatePoi(); // FIXME: dependency
            });
        }
    },
    clearMarker: function() {
        if (this.marker) {
            this.marker.setMap(null);
            this.marker = null;
        }
    },    
    setActive: function() {
        if (this.marker) {
            this.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
            //this.marker.map.setCenter(this.marker.getPosition());
        }
    },
    setInactive: function() {
        if (this.marker) {
            this.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/red-dot.png');
        }
    },
    allowsUniversityFilling: function() {
    	return this.isNew && this.isNew() && (this.parent() == undefined || this.parent() == '');
    }
});

var DataSource = function(baseUrl) {
    this.baseUrl = baseUrl;
    this.cache = {};

    this.getFullPath = function(path) {
        return this.baseUrl + path;
    };
    
    this.initGeocampus = function() {
        var self = this;
        $.getJSON( this.getFullPath("api/admin/geocampus"), { } )
            .done(function( json ) {
                self.cache = json;
                myViewModel.reload(); // FIXME: Dependency
            })
            .fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                // TODO: Report error
        });
    };
    this.getPois = function (type, filters) {
        var pois = [];
        var self = this;
        var params;

        var universityId = filters.university && filters.university.id ? filters.university.id : '';
        var categoryId = filters.category && filters.category.id ? filters.category.id : 0;
        var imageMapId = filters.imageMap && filters.imageMap.id ? filters.imageMap.id : 0;

        if (type == 'images') {
            params = { type: type, im: imageMapId };
        } else {
            params = { type: type, uni: universityId, cat: categoryId };
        }
            
        if (universityId != '' || categoryId != 0 || imageMapId != 0) {
            var serviceUrl = this.getFullPath('api/admin/geocampus/filter');
            $.getJSON( serviceUrl, params )
                .done(function( json ) {
                    var localPois = [];
                    if (json && json.length > 0) {
                        for (var i in json) {
                            var sanitizePoi = json[i];
                            localPois.push(new Poi(sanitizePoi));
                        }
                    }
                    myViewModel.pois(localPois); // FIXME: Dependency
                })
                .fail(function( jqxhr, textStatus, error ) {
                    var err = textStatus + ", " + error;
                    console.log( "Request Failed: " + err );
                    // TODO: Report error
            });
        }
        return pois;
    };
    this.getPoiComments = function (poiId) {
        var serviceUrl = this.getFullPath('api/admin/geocampus/comments');
        $.getJSON( serviceUrl, { poi: poiId })
            .done(function( json ) {
                var comments = [];
                if (json && json.length > 0) {
                    for (var i in json) {
                        var sanitize = json[i];
                        comments.push(new Comment(sanitize));
                    }
                }
                myViewModel.poiComments(comments); // FIXME: Dependency
            })
            .fail(function( jqxhr, textStatus, error ) {
                var err = textStatus + ", " + error;
                console.log( "Request Failed: " + err );
                // TODO: Report error
        });
    };
    this.getCategories = function (type) {
        if (!(this.cache && this.cache.universities)) {
            return [];
        }
        var categories;
        switch (type) {
            case 'images':
                categories = this.cache['imagesCategories'];
                break;
            case 'libraries':
                categories = this.cache['librariesCategories'];
                break;
            case 'bonplans':
                categories = this.cache['bonPlansCategories'];
                break;
            case 'pois':
            default:
                categories = this.cache['plansCategories'];
        }
        return categories;
    };
    this.getImages = function () {
        return this.cache && this.cache['imageMaps'] ? this.adaptImageMaps(this.cache['imageMaps']) : [];
    };
    this.adaptImageMaps = function(ims) {
        var adaptedIms = [];
        for (var im in ims) {
            adaptedIms.push(new ImageMap(ims[im]));
        }
        return adaptedIms;
    };
    this.adaptCategories = function(rootCategory) {
        var adaptedCategories = [];
        return treeWalk(rootCategory, adaptedCategories);
    };
    this.savePoi = function() {
        var poi = myViewModel.activePoi().serialize();
        poi.isNew = myViewModel.activePoi().isNew();
        if (myViewModel.activeTab() == 'images' && myViewModel.activeImage() && myViewModel.activeImage().id) {
            poi.imageMapId = myViewModel.activeImage().id;
        }
        $.post( this.getFullPath("json/admin/geocampus/poi/manage/"), poi)
            .done(function( data ) {
                if (data.result == 'ok') {
                    myViewModel.lastError(false);
                    var isNew = myViewModel.activePoi().isNew();
                    closePoiModal();
                    if (isNew) {
                        myViewModel.activePoi().id(data.data);
                        myViewModel.pois().push(myViewModel.activePoi());
                        addNode(myViewModel.activePoi().id(), myViewModel.activePoi().name(), myViewModel.activePoi());
                    }
                    myViewModel.activePoi().commit();
                    selectNode(myViewModel.activePoi(), true);
                    // force KO pois refresh if there where no nodes (KO bug?)
                    if (myViewModel.pois().length == 1) {
                        myViewModel.pois.valueHasMutated();
                    }
                    if (poi.isNew && poi.imageMap == null && poi.address) {
                        // Center map and a marker for the just created poi
                        geocoder = new google.maps.Geocoder();
                        geocoder.geocode( { 'address': poi.address }, function(results, status) {
                            if (status == google.maps.GeocoderStatus.OK) {
                                gmap.setCenter(results[0].geometry.location);
                                handleNewPoiMarker(results[0].geometry.location);
                            } else {
                                // Marker not created. Geocoding could not resolve the address
                            }
                        });
                    }
                } else {
                    myViewModel.lastError("Erreur lors du chargement. Se il vous plaît donner votre avis");
                }
            })
            .fail(function( data ) {
                myViewModel.lastError("Erreur lors du chargement. Se il vous plaît donner votre avis");
        });
    };
    this.getUniversities = function() {
        var univs = [];
	for (var v in this.cache.universities) {
	    var univ = this.cache.universities[v];
	    univs.push(new Univ(univ.id, univ.title, univ.allowBonplans, univ.regionName));
        }
        return univs;
    };
    this.createQrCode = function(imageMapId, poi) {
        $.post( this.getFullPath("api/admin/geocampus/qr/create"), { poiId: poi.id(), imId: imageMapId })
            .done(function( data ) {
                poi.update(data);
                poi.commit();
                myViewModel.lastError(false);
            })
            .fail(function( data ) {
                myViewModel.lastError("Erreur de création de QR . se il vous plaît donner votre avis");
        });
    };
    this.toggleComment = function(comment) {
        $.post( this.getFullPath("api/admin/geocampus/comment/toggle"), { id: comment.id() })
            .done(function( data ) {
                comment.update(data);
                comment.commit();
                myViewModel.lastError(false);
            })
            .fail(function( data ) {
                comment.revert();
                myViewModel.lastError("Erreur lors du chargement. Se il vous plaît donner votre avis");
                
        });
    };
};

function treeWalk(treeRoot, holder) {
    var children;
    if ($.isArray(treeRoot)) {
        var children = treeRoot;
    } else {
        holder.push({ id: treeRoot.id, name: treeRoot.name });
        var children = treeRoot.children;
    }
    for (var i = 0; i < children.length; i++) {
        treeWalk(children[i], holder);
    }
    return holder;
}

var MyViewModel = function(ds) {
    var self = this;
    
    self.ds = ds;
    
    self.bonplansUniversities = ko.observableArray([]);
    self.categoriesUniversities = ko.observableArray(self.ds.getCategories('universities'));
    self.categoriesBonplans = ko.observableArray(self.ds.getCategories('bonplans'));
    self.categoriesLibraries = ko.observableArray(self.ds.getCategories('libraries'));
    self.categoriesImages = ko.observableArray(self.ds.getCategories('images'));
    self.images = ko.observableArray(self.ds.getImages());
    self.pois = ko.observableArray([]);
    self.poiComments = ko.observableArray([]);
    self.universities = ko.observableArray(self.ds.getUniversities());

    self.activeTab = ko.observable(userRole == 'librarian' ? 'libraries' : 'pois');
    self.activePoiTab = ko.observable('details');
    
    self.activeUniversity = ko.observable({title: ''});
    self.activeCategoryUniversities = ko.observable({name: ''});
    self.activeCategoryBonplans = ko.observable({name: ''});
    self.activeCategoryLibraries = ko.observable({name: ''});
    self.activeCategoryImages = ko.observable({name: ''});
    self.activeImage = ko.observable({name: ''});
    self.activePoi = ko.observable();
    
    self.lastError = ko.observable(false);
    
    self.reload = function() {
        self.universities(self.ds.getUniversities());
        self.categoriesUniversities(self.ds.getCategories('universities'));
        self.categoriesBonplans(self.ds.getCategories('bonplans'));
        self.categoriesLibraries(self.ds.getCategories('libraries'));
        self.categoriesImages(self.ds.getCategories('images'));
        self.images(self.ds.getImages());
        self.pois([]);
        self.poiComments([]);
    }
    
    self.resetActivePoi = function() {
        var emptyPoi = new Poi();
        emptyPoi.name('');
        emptyPoi.parent('');
        self.activePoi(emptyPoi);
    };

    self.resetActivePoi();
    
    self.getActiveCategory = function() {
        switch (self.activeTab()) {
            case 'images':
                return self.activeCategoryImages();
            case 'libraries':
                return self.activeCategoryLibraries();
            case 'bonplans':
                return self.activeCategoryBonplans();
            case 'pois':
            default:
                return self.activeCategoryUniversities();
        }       
    }

    self.getTabTitle = function() {
        switch (self.activeTab()) {
            case 'images':
                return 'Images';
            case 'libraries':
                return 'Bibliothèques';
            case 'bonplans':
                return 'Bon Plans';
            case 'pois':
                return 'POIs';
        }       
    }

    self.getAvailableCategories = function() {
        switch (self.activeTab()) {
            case 'images':
                return self.categoriesImages();
            case 'libraries':
                return self.categoriesLibraries();
            case 'bonplans':
                return self.categoriesBonplans();
            case 'pois':
            default:
                return self.categoriesUniversities();
        }       
    }

    self.switchTab = function(tab) {
        self.activeTab(tab);
        if (tab == 'images') {
            if (!imageMmap) {
                imageMmap = initImagemap(imageCanvasId);
            }
            self.pois(self.ds.getPois('images', { imageMap: self.activeImage() } ));    
        } else if (tab == 'libraries') {
            self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
        } else if (tab == 'bonplans') {
            if (self.activeUniversity() && !self.activeUniversity().allowBonplans) {
                self.activeUniversity(self.bonplansUniversities()[0]);
            }
            self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
        } else {
            self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));    
        }
    };
    
    self.switchPoiTab = function(tab) {
        if (tab == 'comments') {
            self.ds.getPoiComments(self.activePoi().id());
        }
        self.activePoiTab(tab);
    };
    
    self.setActivePoi = function(poi) {
        if (self.activePoi())
            self.activePoi().setInactive();
        self.activePoi(poi);
        poi.setActive();
        self.poiComments([]);
    };
    
    self.changeUniversity = function(university) {
        self.activeUniversity(university);
    };

    self.changeCategoryImages = function(category) {
        self.activeCategoryImages(category);    
    };
    
    self.changeCategoryLibraries = function(category) {
        self.activeCategoryLibraries(category);    
    };

    self.changeCategoryUniversities = function(category) {
        self.activeCategoryUniversities(category);    
    };

    self.changeCategoryBonplans = function(category) {
        self.activeCategoryBonplans(category);    
    };

    self.changeImage = function(image) {
        self.activeImage(image);
        var newUrl = image && image.url() && image.url().length > 0 ? image.url() : null;
        changeImageMap(newUrl);
    };
    
    self.getAvailableUniversities = function() {
        switch (self.activeTab()) {
            case 'images':
                return self.universities();
            case 'bonplans':
                return self.bonplansUniversities();
            case 'pois':
            default:
                return self.universities();
        }       
    };

    self.activePoi.subscribe(function(newValue) {
        myViewModel.lastError(false);
    });

    self.activeUniversity.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
    });

    self.activeCategoryUniversities.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
    });

    self.activeCategoryBonplans.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
    });

    self.activeCategoryLibraries.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { university: self.activeUniversity(), category: self.getActiveCategory() }));
    });

    self.activeImage.subscribe(function(newValue) {
        if (newValue && newValue.id()) {
            self.pois(self.ds.getPois('images', { imageMap: newValue } ));
        } else {
            self.pois([]);
        }
    });

    self.pois.subscribe(function(oldValue) {
        clearMarkers();
    }, null, "beforeChange");
    
    self.pois.subscribe(function(newValue) {
        loadTree(newValue);
        self.resetActivePoi();
    });

    self.universities.subscribe(function(newValue) {
        if (newValue != null && newValue.length == 1) {
            self.activeUniversity(newValue[0]);
        }
        
        var bonplansUniversities = [];
        for (var i in newValue) {
            if (newValue[i].allowBonplans) {
                bonplansUniversities.push(newValue[i]);
            }
        }
        self.bonplansUniversities(bonplansUniversities);
        
    });
};
  
function getPoiByUid(id, pois) {
    for (var i = 0; i < pois.length; i++) {
        if (pois[i].id() == id)
            return pois[i];
    }
    return null;
}

function loadTree(pois) {
    $tree.jstree("destroy");
    $tree.jstree({ 
        'core': {
            'data': buildTree(pois),
            'check_callback': true,
            'multiple': false
        }
    })
    .on('select_node.jstree', function (e, data) {
        if (myViewModel.activePoi() == null || (myViewModel.activePoi().id() != data.node.data.id)) {
            var poi = getPoiByUid(data.node.data.id, myViewModel.pois())
            selectNode(poi);
        }
    })
    .on('deselect_node.jstree', function (e, data) {
        if (myViewModel.activePoi() != null && myViewModel.activePoi().id() != null) {
            myViewModel.resetActivePoi();
        }
    });
}

function selectNode(poi, doSelectOnTree) {
    if (doSelectOnTree) {
        var t = $tree.jstree(true);
        t.deselect_all(true);
        t.select_node(poi.id());
    }
    myViewModel.setActivePoi(poi);
    myViewModel.activePoiTab('details');
}

function generateQrCode() {
    ds.createQrCode(myViewModel.activeImage().id(), myViewModel.activePoi());
}

function createRootPoi() {
    return createPoi(false);
}

function createPoi(asChild) {
    $tree.jstree(true).deselect_all(true);
    var pos = asChild ? myViewModel.activePoi().id() : null;
    var attrs;
    if (myViewModel.activeTab() == 'images') {
        attrs = { parent: pos, imageMap: myViewModel.activeImage().id, university: myViewModel.activeUniversity().id };
    } else {
        attrs = { parent: pos, university: myViewModel.activeUniversity().id };
    }
    myViewModel.setActivePoi(new Poi(attrs));
    openPoiModal('create');
}
        
function editPoi() {
    openPoiModal('edit');
}

function savePoi() {
    if (myViewModel.activePoi().isNew()) {
        newPoi();
    } else {
        updatePoi();
    }
}

function newPoi() {
    myViewModel.ds.savePoi();
}

function updatePoi() {
    myViewModel.ds.savePoi();
}

function cancelPoi() {
    myViewModel.activePoi().revert();
    closePoiModal();
}

function cancelImageMap() {
    myViewModel.activeImage().revert();
    closeImageMapModal();
}

function clearMarkers() {
    var pois = myViewModel.pois();
    for (var i = 0; i < pois.length; i++) {
        pois[i].clearMarker();
    }
}

function openPoiModal(action) {
    var options = { backdrop: 'static' };
    myViewModel.switchPoiTab('details');
    $poiModal.modal(options);
}

function createImageMap() {
    $tree.jstree(true).deselect_all(true);
    myViewModel.changeImage(new ImageMap());
    openImageMapModal('create');
}
        
function editPoi() {
    openPoiModal('edit');
}


function openImageMapModal(action) {
    var options = { backdrop: 'static' };
    $imageMapModal.modal(options);
}

function closePoiModal() {
    $poiModal.modal('hide');
    myViewModel.lastError(false);
}

function closeImageMapModal() {
    $imageMapModal.modal('hide');
}

function buildTree(pois) {
    var treeData =  [];
    for (var i in pois) {
        var poi = pois[i];
        treeData.push(buildNode(poi.id(), poi.name(), poi.parent() ? poi.parent() : '#', poi));
        poi.createMarker();
    }
    return treeData;
}

function buildNode(nodeId, nodeText, parentId, nodeData) {
    var node = {
      id          : nodeId,
      text        : nodeText,
      parent      : parentId,
      /*icon      : "string" // string for custom*/
      state       : {
        opened    : true
        //disabled: boolean  // is the node disabled
        //selected: boolean  // is the node selected
      },
      /*  
      li_attr     : {}  // attributes for the generated LI node
      a_attr      : {}  // attributes for the generated A node
      */
      data: {
          'id': nodeId,
      }
    };
    return node;
}

function updateNode(nodeId, nodeText) {
    $tree.jstree(true).set_text('#' + nodeId, nodeText);
}

function addNode(nodeId, nodeText, nodeData) {
    var parentId = nodeData.parent() ? nodeData.parent() : '#';
    var newNode = buildNode(nodeId, nodeText, parentId, nodeData);
    $tree.jstree(true).create_node(parentId, newNode);
}

function handleNewPoiMarker(e) {
    var poi = myViewModel.activePoi();
    if (poi && poi.id() && !poi.lat()) {
        if (e.latLng) {
            // e is in fact an event
            poi.lat(e.latLng.lat());
            poi.lng(e.latLng.lng());
        } else {
            // e is a latLng object
            poi.lat(e.lat());
            poi.lng(e.lng());
        }
        poi.createMarker();
        poi.setActive();
        updatePoi();
    }
}

function handleImageNewPoiMarker(e) {
    var poi = myViewModel.activePoi();
    if (poi && poi.id() && !poi.lat()) {
        poi.lat(e.latLng.lat());
        poi.lng(e.latLng.lng());
        poi.createMarker('image');
        poi.setActive();
        updatePoi();
    }
}

function initGmaps(canvasId, lat, lng) {
    var mapOptions = {
        center: new google.maps.LatLng(lat, lng),
        zoom: 13,
        disableDoubleClickZoom: true/*,
        mapTypeId: google.maps.MapTypeId.ROADMAP*/
    };
    var map = new google.maps.Map(document.getElementById(canvasId), mapOptions);
    google.maps.event.addListener(map, 'dblclick', handleNewPoiMarker);
    return map;
}

function initImagemap(canvasId) {
    var customMapTypeOptions = {
        getTileUrl: function(coord, zoom) {
            return null;
        },
        tileSize: new google.maps.Size(256, 256),
        maxZoom: 20,
        minZoom: 15,
        radius: 0,
        name: 'Image'
    };
    var customMapType = new google.maps.ImageMapType(customMapTypeOptions);

    var myLatlng = new google.maps.LatLng(0, 0);
    var size = new google.maps.Size(1200, 1200); //FIXME: imagen
    var optimalZoom = zoomLevelByDiag(size);
    var mapOptions = {
        center: myLatlng,
        zoom: optimalZoom,
        disableDoubleClickZoom: true,
        streetViewControl: false,
        mapTypeControlOptions: {
            mapTypeIds: ['image']
        }
    };

    var map = new google.maps.Map(document.getElementById(canvasId), mapOptions);
    map.mapTypes.set('image', customMapType);
    map.setMapTypeId('image');

    var center = new google.maps.LatLng(0, 0);
    imageBounds = calcBounds(center, size)
    
    var lastValidCenter = map.getCenter();
    google.maps.event.addListener(map, 'center_changed', function() {
        if (imageBounds.contains(map.getCenter())) {
            lastValidCenter = map.getCenter();
            return; 
        }   
        map.panTo(lastValidCenter);
    });    
    
    google.maps.event.addListener(map, 'dblclick', handleImageNewPoiMarker);
    return map;
}

function toggleComment(comment) {
    myViewModel.ds.toggleComment(comment);
}

function changeImageMap(newImage) {
    if (imageOverlay != null) {
        imageOverlay.setMap(null);
        delete imageOverlay;
        imageOverlay = null;
    } 

    if (newImage == null) {
        return;
    }

    var url = newImage;
    var img = $("<img />").attr('src', url)
    .load(function() {
        if (!this.complete || typeof this.naturalWidth == "undefined" || this.naturalWidth == 0) {
            console.log('broken image!');
        } else {
            var size = new google.maps.Size(this.naturalWidth, this.naturalHeight);
            var optimalZoom = zoomLevelByDiag(size);
            var center = new google.maps.LatLng(0, 0);
            imageMmap.setCenter(center);
            imageMmap.setZoom(optimalZoom);
            imageBounds = calcBounds(center, size);
            if (imageOverlay) {
                imageOverlay.setMap(null);
                delete imageOverlay;
                imageOverlay = null;
            }
            var options = { clickable: false };
            imageOverlay = new google.maps.GroundOverlay(url, imageBounds, options);
            imageOverlay.setMap(imageMmap);
        }
    });
}

function calcBounds(center, size) {
    var n = google.maps.geometry.spherical.computeOffset(center, size.height / 2, 0).lat(),
        s = google.maps.geometry.spherical.computeOffset(center, size.height / 2, 180).lat(),
        e = google.maps.geometry.spherical.computeOffset(center, size.width / 2, 90).lng(),
        w = google.maps.geometry.spherical.computeOffset(center, size.width / 2, 270).lng();
    return new google.maps.LatLngBounds(
        new google.maps.LatLng(s, w),
        new google.maps.LatLng(n, e)
    );
}        

function diagLenght(size) {
    return Math.sqrt(Math.pow(size.height, 2) + Math.pow(size.width, 2));
}

function zoomLevelByDiag(size) {
    var diag = diagLenght(size);
    if (diag < 500) {
        return 18;
    } else if (diag < 1000) {
        return 17;
    } else if (diag < 3000) {
        return 16;
    } else {
        return 15;
    }
}

function selectFile() {
    $('#imageupload input[type=file]').click();
}

// Init code starts
var gmapsCanvasId = "map_canvas";
var imageCanvasId = "img_canvas";
var gmapsDefaultLat = 48.84650925911;
var gmapsDefaultLng = 2.3459243774;
var imageOverlay;
var imageBounds;

var gmap;
var imageMmap;
var myViewModel;
var $tree;
var $poiModal;
var $imageMapModal;
var ds;

$(function () {
    gmap = initGmaps(gmapsCanvasId, gmapsDefaultLat, gmapsDefaultLng);    
    $tree = $('#jstree1');
    $poiModal = $('#poiModal');
    $imageMapModal = $('#imageMapModal');
    $poiModal.on('hide.bs.modal', function(e) {
        updateNode(myViewModel.activePoi().id(), myViewModel.activePoi().name());
    });
    ds = new DataSource(baseUrl);
    ds.initGeocampus();
    myViewModel = new MyViewModel(ds);
    ko.applyBindings(myViewModel);
   
   $('#imageupload').fileupload({
       maxNumberOfFiles: 1,
       autoUpload: false,
        dataType: 'json',
        change: function (e, data) {
            $.each(data.files, function (index, file) {
                myViewModel.activeImage().temporalImageFile(file.name + ' (' + Math.round(file.size/1024) + 'KB)');
            });
        },
        add: function(e, data) {
            $('#uploadSubmit').unbind('click');
            data.context = $('#uploadSubmit').click(function() {
                //console.log('sending upload data...');
                data.submit();
            });
        },     
        done: function (e, data) {
            $('#uploadSubmit').unbind('click');
            //console.log('done');
            if (!myViewModel.activeImage().id()) {
                myViewModel.activeImage().update(data.result);
                myViewModel.activeImage().commit();
                myViewModel.ds.cache['imageMaps'].push(myViewModel.activeImage().serialize());
                myViewModel.reload();
                closeImageMapModal();
                myViewModel.changeImage(myViewModel.activeImage());
                myViewModel.lastError(false);
            }
        },
        fail: function (e, data) {
            myViewModel.lastError("Impossible de télécharger l'image");
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            //console.log(progress);
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
        }
    });    

});
// Init code ends
