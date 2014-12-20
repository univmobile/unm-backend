var Univ = function(id, name) {
    this.id = id;
    this.name = name;
};

var Poi = function(data) {
    this.id = ko.observable();
    this.name = ko.observable();
    this.university = ko.observable();
    this.parent = ko.observable();
    this.category = ko.observable();
    this.floor = ko.observable();
    this.openingHours = ko.observable();
    this.phone = ko.observable();
    this.address = ko.observable();
    this.email = ko.observable();
    this.itinerary = ko.observable();
    this.url = ko.observable();
    this.lat = ko.observable();
    this.lng = ko.observable();
    
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
        this.phone(data ? data.phone : '');
        this.address(data ? data.address : '');
        this.email(data ? data.email : '');
        this.itinerary(data ? data.itinerary : '');
        this.url(data ? data.url : '');
        this.lat(data ? data.lat : null);
        this.lng(data ? data.lng : null);
        
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
    createMarker: function(type) {
        if (this.lat() && this.lng()) {
            this.clearMarker();
            this.marker = new google.maps.Marker({
                position: new google.maps.LatLng(this.lat(), this.lng()),
                map: type == 'image' ? imageMmap : gmap,
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
        $.getJSON( this.getFullPath("/api/admin/geocampus"), { } )
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
        if (type == 'images') {
            var imagePois = filters && filters.imageMap && filters.imageMap.pois ? filters.imageMap.pois : [];
            for (var i in imagePois) {
                pois.push(new Poi(imagePois[i]));
            }
        } else {
            var self = this;
            var regionId = filters.region && filters.region.id ? filters.region.id : 'all';
            var categoryId = filters.category && filters.category.id ? filters.category.id : 0;
            if (regionId != 'all' || categoryId != 0) {
                var serviceUrl = this.getFullPath('api/admin/geocampus/filter');
                $.getJSON( serviceUrl, { type: type, reg: regionId, cat: categoryId } )
                    .done(function( json ) {
                        var localPois = [];
                        if (json && json.length > 0) {
                            for (var i in json) {
                                var sanitizePoi = json[i];
                                /*
                                var coords = jsonPois[i].coordinates && jsonPois[i].coordinates.length > 0 ? jsonPois[i].coordinates.split(',') : null;
                                sanitizePoi.lat = coords ? coords[0] : null;
                                sanitizePoi.lng = coords ? coords[1] : null;
                                */
                                localPois.push(new Poi(sanitizePoi));
                            }
                        }
                        /*
                        if (json.groups && json.groups.length > 0) {
                            for (var gr = 0; gr < json.groups.length; gr++) {
                                var jsonPois = json.groups[gr].pois ? json.groups[gr].pois : [];
                                for (var i in jsonPois) {
                                    var sanitizePoi = jsonPois[i];
                                    var coords = jsonPois[i].coordinates && jsonPois[i].coordinates.length > 0 ? jsonPois[i].coordinates.split(',') : null;
                                    sanitizePoi.lat = coords ? coords[0] : null;
                                    sanitizePoi.lng = coords ? coords[1] : null;
                                    localPois.push(new Poi(sanitizePoi));
                                }
                            }
                        }
                        */
                        myViewModel.pois(localPois); // FIXME: Dependency
                    })
                    .fail(function( jqxhr, textStatus, error ) {
                        var err = textStatus + ", " + error;
                        console.log( "Request Failed: " + err );
                        // TODO: Report error
                });
            }
        }
        return pois;
    };
    this.getRegions = function () {
        return this.cache && this.cache.regions ? this.cache.regions : [];
    };
    this.getCategories = function (type) {
        if (!(this.cache && this.cache.regions)) {
            return [];
        }
        var categories = (type == 'bonplans') ? 
        		/*
            this.adaptCategories(this.cache['bonPlansCategories']) : 
            this.adaptCategories(this.cache['plansCategories']);*/
            this.cache['bonPlansCategories'] : 
           	this.cache['plansCategories'];
        return categories;
    };
    this.getImages = function () {
        return this.cache && this.cache['imageMaps'] ? this.cache['imageMaps'] : [];
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
                var isNew = myViewModel.activePoi().isNew();
                myViewModel.activePoi().commit();
                closePoiModal();
                if (isNew) {
                    myViewModel.pois().push(myViewModel.activePoi());
                    addNode(myViewModel.activePoi().id(), myViewModel.activePoi().name(), myViewModel.activePoi());
                }
                selectNode(myViewModel.activePoi(), true);
                // force KO pois refresh if there where no nodes (KO bug?)
                if (myViewModel.pois().length == 1) {
                    myViewModel.pois.valueHasMutated();
                }
            })
            .fail(function( data ) {
                alert( "Data error: " + data );
        });
    };
    this.getUniversities = function() {
        var univs = [];
        for (var r in this.cache.regions) {
            for (var v in this.cache.regions[r].universities) {
                var univ = this.cache.regions[r].universities[v];
                univs.push(new Univ(univ.id, univ.title));
            }
        }
        return univs;
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
    
    self.regions = ko.observableArray(self.ds.getRegions());
    self.categoriesUniversities = ko.observableArray(self.ds.getCategories('universities'));
    self.categoriesBonplans = ko.observableArray(self.ds.getCategories('bonplans'));
    self.images = ko.observableArray(self.ds.getImages());
    self.pois = ko.observableArray([]);
    self.universities = ko.observableArray(self.ds.getUniversities());

    self.activeTab = ko.observable('pois');
    self.activePoiTab = ko.observable('details');
    
    self.activeRegion = ko.observable({name: ''});
    self.activeCategoryUniversities = ko.observable({name: ''});
    self.activeCategoryBonplans = ko.observable({name: ''});
    self.activeImage = ko.observable({name: ''});
    self.activePoi = ko.observable();
    
    
    self.reload = function() {
        self.regions(self.ds.getRegions());
        self.categoriesUniversities(self.ds.getCategories('universities'));
        self.categoriesBonplans(self.ds.getCategories('bonplans'));
        self.images(self.ds.getImages());
        self.universities(self.ds.getUniversities());
        self.pois([]);

        /*
        self.activeTab = ko.observable('pois');
        self.activePoiTab = ko.observable('details');
        
        self.activeRegion = ko.observable({name: ''});
        self.activeCategoryUniversities = ko.observable({name: ''});
        self.activeCategoryBonplans = ko.observable({name: ''});
        self.activeImage = ko.observable({name: ''});
        self.activePoi = ko.observable();
        */
    }
    
    self.resetActivePoi = function() {
        var emptyPoi = new Poi();
        emptyPoi.name('');
        emptyPoi.parent('');
        self.activePoi(emptyPoi);
    };

    self.resetActivePoi();
    
    self.getActiveCategory = function() {
        return self.activeTab() == 'pois' ? self.activeCategoryUniversities() : self.activeCategoryBonplans();
    }

    self.getAvailableCategories = function() {
        return self.activeTab() == 'pois' ? self.categoriesUniversities() : self.categoriesBonplans();
    }

    self.switchTab = function(tab) {
        self.activeTab(tab);
        if (tab == 'images') {
            self.pois(self.ds.getPois('images', { imageMap: self.activeImage() } ));    
        } else {
            self.pois(self.ds.getPois(self.activeTab(), { region: self.activeRegion(), category: self.getActiveCategory() }));    
        }
    };
    
    self.switchPoiTab = function(tab) {
        self.activePoiTab(tab);
    };
    
    self.setActivePoi = function(poi) {
        if (self.activePoi())
            self.activePoi().setInactive();
        self.activePoi(poi);
        poi.setActive();
    };
    
    self.changeRegion = function(region) {
        self.activeRegion(region);    
    };
    
    self.changeCategoryUniversities = function(category) {
        self.activeCategoryUniversities(category);    
    };

    self.changeCategoryBonplans = function(category) {
        self.activeCategoryBonplans(category);    
    };

    self.changeImage = function(image) {
        self.activeImage(image);
        var newUrl = image && image.imageUrl && image.imageUrl.length > 0 ? image.imageUrl : null;
        changeImageMap(newUrl);
    };

    self.activeRegion.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { region: self.activeRegion(), category: self.getActiveCategory() }));
    });

    self.activeCategoryUniversities.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { region: self.activeRegion(), category: self.getActiveCategory() }));
    });

    self.activeCategoryBonplans.subscribe(function(newValue) {
        self.pois(self.ds.getPois(self.activeTab(), { region: self.activeRegion(), category: self.getActiveCategory() }));
    });

    self.activeImage.subscribe(function(newValue) {
        self.pois(self.ds.getPois('images', { imageMap: newValue } ));
    });

    self.pois.subscribe(function(oldValue) {
        clearMarkers();
    }, null, "beforeChange");
    
    self.pois.subscribe(function(newValue) {
        loadTree(newValue);
        self.resetActivePoi();
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
        if (myViewModel.activePoi().id() != data.node.data.id) {
            var poi = getPoiByUid(data.node.data.id, myViewModel.pois())
            selectNode(poi);
        }
    })
    .on('deselect_node.jstree', function (e, data) {
        if (myViewModel.activePoi().id() != null) {
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

function createRootPoi() {
    return createPoi(false);
}

function createPoi(asChild) {
    $tree.jstree(true).deselect_all(true);
    var pos = asChild ? myViewModel.activePoi().id() : null;
    myViewModel.setActivePoi(new Poi({ parent: pos}));
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
    var uid = myViewModel.activePoi().id();
    if (getPoiByUid(uid, myViewModel.pois()) == null) {
        myViewModel.ds.savePoi();
    } else {
        // TODO: Error, uid exists   
    }
}

function updatePoi() {
    // TODO: DS call and commit on success/close modal, error otherwise
    myViewModel.ds.savePoi();
    /*
    myViewModel.activePoi().commit();
    selectNode(myViewModel.activePoi(), true)
    closePoiModal();
    */
}

function cancelPoi() {
    myViewModel.activePoi().revert();
    closePoiModal();
}

function clearMarkers() {
    var pois = myViewModel.pois();
    for (var i = 0; i < pois.length; i++) {
        pois[i].clearMarker();
    }
}

function openPoiModal(action) {
    var options = { backdrop: 'static' };
    $poiModal.modal(options);
}

function openImageMapModal(action) {
    var options = { backdrop: 'static' };
    $imageMapModal.modal(options);
}

function closePoiModal() {
    $poiModal.modal('hide');
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
        poi.lat(e.latLng.lat());
        poi.lng(e.latLng.lng());
        poi.createMarker();
        poi.setActive();
        updatePoi();
    }
}

function handleImageNewPoiMarker(e) {
    var poi = myViewModel.activePoi();
    if (poi && poi.id() && !poi.lat()) {
        // TODO: Save coords on server
        poi.lat(e.latLng.lat());
        poi.lng(e.latLng.lng());
        poi.createMarker('image');
        poi.setActive();
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
        disableDoubleClickZoom: true,
        name: 'Image'
    };
    var customMapType = new google.maps.ImageMapType(customMapTypeOptions);

    var myLatlng = new google.maps.LatLng(0, 0);
    var size = new google.maps.Size(527, 938); //FIXME: imagen
    var optimalZoom = zoomLevelByDiag(size);
    var mapOptions = {
        center: myLatlng,
        //zoom: 17,
        zoom: optimalZoom,
        mapTypeControlOptions: {
            mapTypeIds: ['image']
        }
    };

    var map = new google.maps.Map(document.getElementById(canvasId), mapOptions);
    map.mapTypes.set('image', customMapType);
    map.setMapTypeId('image');

    var center = new google.maps.LatLng(0, 0);
    imageBounds = calcBounds(center, size)
    
    //imageOverlay = new google.maps.GroundOverlay('poiimages/image1.png', imageBounds);
    //imageOverlay.setMap(map);
    
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

function changeImageMap(newImage) {
    if (newImage == null) {
        imageOverlay.setMap(null);
        delete imageOverlay;
        imageOverlay = null;
        return;  
    } 
    
    // Hack
    if (newImage == 'http://univmobile-dev.univ-paris1.fr/image/plan/imagemap1.png') {
        newImage = 'poiimages/2.jpg';
    } else {
        newImage = 'poiimages/image1.png';
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
            imageOverlay = new google.maps.GroundOverlay(url, imageBounds);
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
    imageMmap = initImagemap(imageCanvasId);
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
   
    /*
    // Image upload event firing
    $(document).on('change', '.btn-file :file', function() {
        var input = $(this),
            numFiles = input.get(0).files ? input.get(0).files.length : 1,
            label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
        input.trigger('fileselect', [numFiles, label]);
    });
    // Handler for upload image event
    $('.btn-file :file').on('fileselect', function(event, numFiles, label) {
        alert('cargo');
        console.log(numFiles);
        console.log(label);
    });
    */
   $('#fileupload').fileupload({
        dataType: 'json',
        done: function (e, data) {
            console.log('done');
            //console.log(data.result);
            /*
            $.each(data.result.files, function (index, file) {
                $('<p/>').text(file.name).appendTo(document.body);
            });
            */
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            console.log(progress);
            /*
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
            */
        }
    });    
});
// Init code ends
