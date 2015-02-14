function getListItemByField(a,b,c){if(!a||!a.length)return null;for(var d=0;d<a.length;d++)if(a[d][b]==c)return a[d];return null}angular.module("halApp.templates",[]).run(["$templateCache",function(a){"use strict";a.put("views/feed-edit.html",'<div><div class=page-title><h1 ng-if=item.id>Modifier Flux</h1><h1 ng-if=!item.id>Ajouter Flux</h1></div><form><div class=form-group><label for=name class=control-label>Nom *</label><input class=form-control id=name ng-model=item.name validator="[required]"></div><div class=form-group><label for=university class=control-label>Universit&eacute;</label><select class=form-control id=university ng-model=item.universityId ng-options="uni.id as uni.title group by uni.regionName for uni in unis"><option ng-if=isSuperAdmin() value="">---</option></select></div><div class=form-group><label for=type class=control-label>Type</label><select class=form-control id=type ng-model=item.type ng-options="type for type in types"></select></div><div class=form-group><label for=url class=control-label>URL *</label><input class=form-control id=url ng-model=item.url validator="[requiredUrl]"></div><button type=button class="btn btn-success" ng-click="handleSaveClicked( item )">Enregister</button> <a href=#/feeds class="btn btn-danger">Annuler</a></form></div>'),a.put("views/feeds.html",'<div><div class=page-title><div class=col-sm-9><h1>Flux</h1></div><div class=col-sm-3><button class="btn btn-success pull-right" ng-click="handleEditClick( null )">Ajouter un Flux</button></div><div class=clearfix></div></div><table class="table table-striped table-condensed table-feeds"><tr><th>Université</th><th>Nom</th><th>Type</th><th>Url</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=5>Chargement ...</td></tr><tr ng-if="items && !items.length"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="item in items"><td>{{item.getUni().title}}</td><td>{{item.name}}</td><td>{{item.type}}</td><td>{{item.url}}</td><td><button class="btn btn-info btn-sm" ng-click="handleEditClick( item )"><span class="glyphicon glyphicon-pencil"></span></button> <button class="btn btn-danger btn-sm" ng-click="handleDeleteClick( item )"><span class="glyphicon glyphicon-remove"></span></button></td></tr></table><div class=main-search-pager ng-show="pager.numPages > 1"><pagination total-items=pager.totalCount items-per-page=pager.pageSize max-size=10 boundary-links=true class=pagination-sm ng-model=pager.page ng-change=loadItems() num-pages=pager.numPages previous-text=pr&eacute;c&eacute;dent next-text=suivant first-text=première last-text=dernier></pagination><pre>Page: {{pager.page}} / {{pager.numPages}}</pre></div></div>'),a.put("views/main.html",'<div><div class=page-title><h1>Recherche avanc&eacute;e</h1></div><div class=row><div class=col-sm-12><div class=input-group><input class=form-control ng-model=searchText id="main-search"> <span class=input-group-btn><button class="btn btn-info" ng-click=handleSearchClick()>Rechercher</button></span></div></div></div><div role=tabpanel class=main-tabs><ul class="nav nav-tabs" role=tablist id=main-tabs><li role=presentation class=active><a href=#categories aria-controls=categories role=tab data-toggle=tab>Catégories</a></li><li role=presentation><a href=#comments aria-controls=comments role=tab data-toggle=tab>Commentaires</a></li><li role=presentation><a href=#pois aria-controls=pois role=tab data-toggle=tab>POIs</a></li><li role=presentation><a href=#users aria-controls=users role=tab data-toggle=tab>Utilisateurs</a></li></ul><div class=tab-content us-spinner="{radius:30, width:8, length: 16, opacity: 0.5}" spinner-key=tab-spinner><div role=tabpanel class="tab-pane active" id=categories><table class="table table-striped table-condensed table-cats"><tr><th>Nom</th><th>Description</th><th>Actif</th><th class=alt-action>Action</th></tr><tr ng-if=!cats><td colspan=4>Chargement ...</td></tr><tr ng-if="cats && !cats.length"><td colspan=4>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="cat in cats"><td>{{cat.name}}</td><td>{{cat.description}}</td><td><span ng-if=cat.active class="glyphicon glyphicon-ok text-success"></span> <span ng-if=!cat.active class="glyphicon glyphicon-remove text-danger"></span></td><td><a href="{{ baseUrl }}poicategoriesmodify/{{ cat.id }}">Modifier…</a> <a href="{{ baseUrl }}poicategories/{{ cat.id }}">Sous-catégories…</a></td></tr></table></div><div role=tabpanel class=tab-pane id=comments><table class="table table-striped table-condensed table-comments"><tr><th>Date</th><th>Utilisateurs</th><th>Commentaire</th></tr><tr ng-if=!comments><td colspan=5>Chargement ...</td></tr><tr ng-if="comments && !comments.length"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="comment in comments"><td>{{comment.postedOn | date:\'dd-MM-yyyy HH:mm:ss\'}}</td><td>{{comment.author}}</td><td>{{comment.message}}</td></tr></table></div><div role=tabpanel class=tab-pane id=pois><table class="table table-striped table-condensed table-pois"><tr><th>POI</th><th>Adresse</th><th>Action</th></tr><tr ng-if=!pois><td colspan=4>Chargement ...</td></tr><tr ng-if="pois && !pois.length"><td colspan=4>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="poi in pois"><td>{{poi.name}}</td><td>{{poi.address}}</td><td><a href="{{ baseUrl }}poismodify/{{ poi.id }}">Modifier…</a></td></tr></table></div><div role=tabpanel class=tab-pane id=users><table class="table table-striped table-condensed table-users"><tr><th>Utilisateurs</th><th>rôle</th><th>E-mail</th><th>Action</th></tr><tr ng-if=!users><td colspan=5>Chargement ...</td></tr><tr ng-if="users && !users.length"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="user in users"><td>{{user.username}}</td><td><span ng-if=user.superAdmin>S</span><span ng-if=user.admin>A</span><span ng-if=user.student>E</span></td><td>{{user.email}}</td><td><a href="{{ baseUrl }}usermodify/{{ user.id }}">Modifier…</a></td></tr></table></div><div class=main-search-pager ng-show="pager.numPages > 1"><pagination total-items=pager.totalCount items-per-page=pager.pageSize max-size=10 boundary-links=true class=pagination-sm ng-model=pager.page ng-change="showCurrentTab( true )" num-pages=pager.numPages previous-text=pr&eacute;c&eacute;dent next-text=suivant first-text=première last-text=dernier></pagination><pre>Page: {{pager.page}} / {{pager.numPages}}</pre></div></div></div><script></script></div>'),a.put("views/menu-edit.html",'<div><div class=page-title><h1 ng-if=item.id>Modifier Menu</h1><h1 ng-if=!item.id>Ajouter Menu</h1></div><div class="alert alert-danger" role=alert ng-if="submitTracking && !validateUrlOrContent(item)"><span class="glyphicon glyphicon-exclamation-sign" aria-hidden=true></span> <span class=sr-only>Error:</span> Entrez une URL ou le contenu</div><form><div class=form-group><label for=name class=control-label>Nom *</label><input class=form-control id=name ng-model=item.name validator="[required]"></div><div class=form-group><label for=university class=control-label>Université</label><select class=form-control id=university ng-model=item.universityId ng-options="uni.id as uni.title group by uni.regionName for uni in unis"><option ng-if=isSuperAdmin() value="">---</option></select></div><div class=form-group><label for=grouping class=control-label>Groupe *</label><select class=form-control id=type ng-model=item.grouping ng-options="grouping for grouping in groups" validator=[required]></select></div><div class=checkbox><label><input type=checkbox ng-model="item.active"> Actif</label></div><div class=form-group><label for=ordinal class=control-label>Ordre *</label><input class=form-control id=ordinal ng-model=item.ordinal validator="[required]"></div><div class=form-group><label for=url class=control-label>URL</label><input class=form-control id=url ng-model=item.url validator=[optionalUrl] validator-required="false"></div><div class=form-group><label for=content class=control-label>Content</label><textarea class="form-control notification-content" id=content ng-model=item.content></textarea></div><button type=button class="btn btn-success" ng-click="handleSaveClicked( item )">Enregister</button> <a href=#/menus class="btn btn-danger">Annuler</a></form></div>'),a.put("views/menus.html",'<div><div class=page-title><div class=col-sm-9><h1>Menus</h1></div><div class=col-sm-3><button class="btn btn-success pull-right" ng-click="handleEditClick( null )">Ajouter un Menu</button></div><div class=clearfix></div></div><table class="table table-striped table-condensed table-menus"><tr><th>Université</th><th>Nom</th><th>Groupe</th><th>Actif</th><th>Ordre</th><th>Url</th><th>Content</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=8>Chargement ...</td></tr><tr ng-if="items && !items.length"><td colspan=8>- Aucun donn&eacute;es -</td></tr><tr ng-repeat="item in items"><td>{{item.getUni().title}}</td><td>{{item.name}}</td><td>{{item.grouping}}</td><td><span ng-if=item.active class="glyphicon glyphicon-ok text-success"></span> <span ng-if=!item.active class="glyphicon glyphicon-remove text-danger"></span></td><td>{{item.ordinal}}</td><td>{{item.url}}</td><td>{{item.content}}</td><td><button class="btn btn-info btn-sm" ng-click="handleEditClick( item )"><span class="glyphicon glyphicon-pencil"></span></button> <button class="btn btn-danger btn-sm" ng-click="handleDeleteClick( item )"><span class="glyphicon glyphicon-remove"></span></button></td></tr></table><div class=main-search-pager ng-show="pager.numPages > 1"><pagination total-items=pager.totalCount items-per-page=pager.pageSize max-size=10 boundary-links=true class=pagination-sm ng-model=pager.page ng-change=loadItems() num-pages=pager.numPages previous-text=pr&eacute;c&eacute;dent next-text=suivant first-text=première last-text=dernier></pagination><pre>Page: {{pager.page}} / {{pager.numPages}}</pre></div></div>'),a.put("views/notification-edit.html",'<div><div class=page-title><h1 ng-if=item.id>Modifier Notification</h1><h1 ng-if=!item.id>Ajouter Notification</h1></div><form><div class=form-group><label for=university class=control-label>Université</label><select class=form-control id=university ng-model=item.universityId ng-options="uni.id as uni.title group by uni.regionName for uni in unis"><option ng-if=isSuperAdmin() value="">---</option></select></div><div class=form-group><label for=content class=control-label>Content *</label><textarea class="form-control notification-content" id=content ng-model=item.content validator=[required]></textarea></div><button type=button class="btn btn-success" ng-click="handleSaveClicked( item )">Enregister</button> <a href=#/notifications class="btn btn-danger">Annuler</a></form></div>'),a.put("views/notifications.html",'<div><div class=page-title><div class=col-sm-9><h1>Notifications</h1></div><div class=col-sm-3><button class="btn btn-success pull-right" ng-click="handleEditClick( null )">Ajouter Notification</button></div><div class=clearfix></div></div><table class="table table-striped table-condensed table-notifs"><tr><th>Université</th><th>Content</th><th>Date</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=3>Chargement ...</td></tr><tr ng-if="items && !items.length"><td colspan=4>- Aucun donn&eacute;e -</td></tr><tr ng-repeat="item in items"><td>{{item.getUni().title}}</td><td>{{item.content}}</td><td>{{item.notificationTime | date : \'dd-MM-yy H:mm:ss\'}}</td><td><button class="btn btn-info btn-sm" ng-click="handleEditClick( item )"><span class="glyphicon glyphicon-pencil"></span></button> <button class="btn btn-danger btn-sm" ng-click="handleDeleteClick( item )"><span class="glyphicon glyphicon-remove"></span></button></td></tr></table><div class=main-search-pager ng-show="pager.numPages > 1"><pagination total-items=pager.totalCount items-per-page=pager.pageSize max-size=10 boundary-links=true class=pagination-sm ng-model=pager.page ng-change=loadItems() num-pages=pager.numPages previous-text=pr&eacute;c&eacute;dent next-text=suivant first-text=première last-text=dernier></pagination><pre>Page: {{pager.page}} / {{pager.numPages}}</pre></div></div>')}]);var toInject=["ngCookies","ngResource","ngRoute","ngSanitize","hateoas","ui.bootstrap","angular-loading-bar","angularSpinner","validator","validator.rules"];"undefined"!=typeof isDev&&isDev||toInject.push("halApp.templates");var halApp=angular.module("halSearchApp",toInject);halApp.model={},halApp.URL_PREF=baseUrl,halApp.config(["$routeProvider","HateoasInterceptorProvider","HateoasInterfaceProvider","$validatorProvider",function(a,b,c,d){a.when("/main",{templateUrl:"views/main.html",controller:"MainCtrl"}).when("/notifications",{templateUrl:"views/notifications.html",controller:"CtrlNotifications"}).when("/notifications/:itemId/edit",{templateUrl:"views/notification-edit.html",controller:"CtrlNotificationEdit"}).when("/feeds",{templateUrl:"views/feeds.html",controller:"CtrlFeeds"}).when("/feeds/:itemId/edit",{templateUrl:"views/feed-edit.html",controller:"CtrlFeedEdit"}).when("/menus",{templateUrl:"views/menus.html",controller:"CtrlMenus"}).when("/menus/:itemId/edit",{templateUrl:"views/menu-edit.html",controller:"CtrlMenuEdit"}).otherwise({redirectTo:"/main"}),b.transformAllResponses(),c.setLinksKey("_links"),d.register("optionalUrl",{invoke:"blur",validator:/((((http[s]{0,1}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)|^$)/,error:"Ce champ doit être l'URL ."}),d.register("requiredUrl",{invoke:"blur",validator:/(((http[s]{0,1}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/,error:"Ce champ doit être l'URL ."})}]),halApp.showAlert=function(a,b){var c=$("#alert-global");c.html(a),c.removeClass("alert-success").removeClass("alert-danger"),c.addClass(b?"alert-danger":"alert-success"),c.show(),c.css("top",$(window).scrollTop()+2),setTimeout(function(){c.hide()},3e3)},halApp.showDialog=function(a,b,c,d){var e=$("#dialog-global");e.find("#dialog-global-btn-cancel").off("click"),e.find("#dialog-global-btn-cancel").on("click",function(){e.modal("hide"),d&&d()}),e.find("#dialog-global-btn-ok").off("click"),e.find("#dialog-global-btn-ok").on("click",function(){c()}),$("#dialog-title").html(a),$("#dialog-body").html(b),e.modal({backdrop:"static",keyboard:!0,show:!0})},halApp.hideDialog=function(){var a=$("#dialog-global");a.modal("hide")},halApp.model.universities=null,halApp.factory("universityService",["$resource",function(a){var b={},c=function(a){for(var b=[],c=0;c<a.length;c++)b.push(new University(a[c]));return b};return b.loadItems=function(b,d){if(!halApp.model.universities||d)a(baseUrl+"api/universities/search/getAuthorizedUniversities").get(null,function(a){halApp.model.universities=c(a._embedded?a._embedded.universities:[]),b&&b(halApp.model.universities)});else if(b)return b(halApp.model.universities)},b.getItem=function(a){return getListItemByField(halApp.model.universities,"id",a)},b}]);var University=function(a){this.id=a?a.id:0,this.title=a?a.title:"",this.regionName=a?a.regionName:""};University.prototype.getLink=function(){return halApp.URL_PREF+"api/universities/"+this.id},halApp.model.categories=[],halApp.factory("categoryService",["$resource",function(a){var b={};return b.search=function(b,c,d){a(baseUrl+"api/categories/search/searchValue?val="+b+"&"+c.toQS()).get(null,function(a){c.totalCount=a.page.totalElements,c.numPages=a.page.totalPages;var b=a._embedded?a._embedded.categories:[];d(b)})},b}]),halApp.model.comments=[],halApp.factory("commentsService",["$resource",function(a){var b={};return b.search=function(b,c,d,e,f){var g=baseUrl+(c?"api/comments/search/searchGlobalValue?val=":"api/comments/search/searchValue?universityId="+d+"&val=")+b+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.comments:[];f(b)})},b}]),halApp.model.feeds=null,halApp.factory("feedService",["$resource","$http","universityService",function(a,b){var c={},d=function(a){for(var b=[],c=0;c<a.length;c++){var d=new Feed(a[c]);b.push(d)}return b},e=function(a){halApp.model.feeds&&halApp.model.feeds.push(a)},f=function(a){var b=halApp.model.feeds.indexOf(a);b>-1&&halApp.model.feeds.splice(b,1)};return c.loadItems=function(b,c,e,f){var g=b?baseUrl+"api/feeds/?"+e.toQS():baseUrl+"api/feeds/search/findByUniversityOrderByCreatedOnDesc?universityId="+c+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.feeds:[];halApp.model.feeds=d(b),f(halApp.model.feeds)})},c.getItem=function(b,c){return b?halApp.model.feeds?c(getListItemByField(halApp.model.feeds,"id",b)):void a(baseUrl+"api/feeds/"+b).get(null,function(a){c(new Feed(a))}):c(new Feed)},c.save=function(a,c){a.id?b.patch(a.getLink(!1),a.toObject()).success(function(){c()}):b.post(a.getListLink(!1),a.toObject()).success(function(b,d,f){var g=f("location"),h=g.substring(g.lastIndexOf("/")+1);a.id=parseInt(h),e(a),c(a)})},c.remove=function(a,c){b["delete"](a.getLink(!1)).success(function(){f(a),c()})},c}]);var Feed=function(a){this.id=a?a.id:0,this.name=a?a.name:"",this.url=a?a.url:"",this.type=a?a.type:"RSS",this.universityId=a?a.universityId:0};Feed.prototype.toObject=function(){var a=this.getUni();return{id:this.id?this.id:null,name:this.name,url:this.url,type:this.type,university:a?a.getLink():null}},Feed.prototype.getUni=function(){return getListItemByField(halApp.model.universities,"id",this.universityId)},Feed.prototype.getLink=function(a){return(a?"":halApp.URL_PREF)+"api/feeds/"+this.id},Feed.prototype.getListLink=function(a){return(a?"":halApp.URL_PREF)+"api/feeds/"},halApp.model.menus=null,halApp.factory("menuService",["$resource","$http","universityService",function(a,b){var c={},d=function(a){for(var b=[],c=0;c<a.length;c++){var d=new Menu(a[c]);b.push(d)}return b},e=function(a){halApp.model.menus&&halApp.model.menus.push(a)},f=function(a){var b=halApp.model.menus.indexOf(a);b>-1&&halApp.model.menus.splice(b,1)};return c.loadItems=function(b,c,e,f){var g=b?baseUrl+"api/menues/search/findByOrderByGroupingAscOrdinalAsc?"+e.toQS():baseUrl+"api/menues/search/findByUniversityOrderByGroupingAscOrdinalAsc?universityId="+c+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.menu:[];halApp.model.menus=d(b),f(halApp.model.menus)})},c.getItem=function(b,c){return b?halApp.model.menus?c(getListItemByField(halApp.model.menus,"id",b)):void a(baseUrl+"api/menues/"+b).get(null,function(a){c(new Menu(a))}):c(new Menu)},c.save=function(a,c){a.id?b.patch(a.getLink(!1),a.toObject()).success(function(){c()}):b.post(a.getListLink(!1),a.toObject()).success(function(b,d,f){var g=f("location"),h=g.substring(g.lastIndexOf("/")+1);a.id=parseInt(h),e(a),c(a)})},c.remove=function(a,c){b["delete"](a.getLink(!1)).success(function(){f(a),c()})},c}]);var Menu=function(a){this.id=a?a.id:0,this.name=a?a.name:"",this.universityId=a?a.universityId:0,this.grouping=a?a.grouping:"MS",this.active=a?a.active:!0,this.ordinal=a?a.ordinal:0,this.url=a?a.url:"",this.content=a?a.content:""};Menu.prototype.toObject=function(){var a=this.getUni();return{id:this.id?this.id:null,name:this.name,university:a?a.getLink():null,grouping:this.grouping,active:this.active,ordinal:this.ordinal,url:this.url,content:this.content}},Menu.prototype.getUni=function(){return getListItemByField(halApp.model.universities,"id",this.universityId)},Menu.prototype.getLink=function(a){return(a?"":halApp.URL_PREF)+"api/menues/"+this.id},Menu.prototype.getListLink=function(a){return(a?"":halApp.URL_PREF)+"api/menues/"},halApp.model.notifications=null,halApp.factory("notificationService",["$resource","$http","universityService",function(a,b){var c={},d=function(a){for(var b=[],c=0;c<a.length;c++){var d=new Notification(a[c]);b.push(d)}return b},e=function(a){halApp.model.notifications&&halApp.model.notifications.push(a)},f=function(a){var b=halApp.model.notifications.indexOf(a);b>-1&&halApp.model.notifications.splice(b,1)};return c.loadItems=function(b,c,e,f){var g=b?baseUrl+"api/notifications/search/findByOrderByCreatedOnDesc?"+e.toQS():baseUrl+"api/notifications/search/findByUniversityOrderByCreatedOnDesc?universityId="+c+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.notifications:[];halApp.model.notifications=d(b),f(halApp.model.notifications)})},c.getItem=function(b,c){return b?halApp.model.notifications?c(getListItemByField(halApp.model.notifications,"id",b)):void a(baseUrl+"api/notifications/"+b).get(null,function(a){c(new Notification(a))}):c(new Notification)},c.save=function(a,c){a.id?b.patch(a.getLink(!1),a.toObject()).success(function(){c()}):b.post(a.getListLink(!1),a.toObject()).success(function(b,d,f){var g=f("location"),h=g.substring(g.lastIndexOf("/")+1);a.id=parseInt(h),e(a),c(a)})},c.remove=function(a,c){b["delete"](a.getLink(!1)).success(function(){f(a),c()})},c}]);var Notification=function(a){this.id=a?a.id:0,this.content=a?a.content:"",this.links=a?a._links:{},this.universityId=a?a.universityId:0,this.notificationTime=a?a.notificationTime:0};Notification.prototype.toObject=function(){var a=this.getUni();return{id:this.id?this.id:null,content:this.content,university:a?a.getLink():null}},Notification.prototype.getUni=function(){return getListItemByField(halApp.model.universities,"id",this.universityId)},Notification.prototype.getLink=function(a){return(a?"":halApp.URL_PREF)+"api/notifications/"+this.id},Notification.prototype.getListLink=function(a){return(a?"":halApp.URL_PREF)+"api/notifications/"},halApp.model.pois=[],halApp.factory("poiService",["$resource",function(a){var b={};return b.search=function(b,c,d,e,f){var g=baseUrl+(c?"api/pois/search/searchGlobalValue?val=":"api/pois/search/searchValue?universityId="+d+"&val=")+b+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.pois:[];f(b)})},b}]),halApp.model.users=[],halApp.factory("userService",["$resource",function(a){var b={};return b.search=function(b,c,d,e,f){var g=baseUrl+(c?"api/users/search/searchGlobalValue?val=":"api/users/search/searchValue?universityId="+d+"&val=")+b+"&"+e.toQS();a(g).get(null,function(a){e.totalCount=a.page.totalElements,e.numPages=a.page.totalPages;var b=a._embedded?a._embedded.users:[];f(b)})},b}]),angular.module("halSearchApp").controller("CtrlApp",["$rootScope","$scope","$route","$http",function(a,b){var c=function(){};b.$on("$routeChangeSuccess",function(){var a=location.hash?location.hash.split("/"):[];a.length&&(b.mainMenu=a[1]),a.length>1&&(b.subMenu=a[2])}),b.isMenuActive=function(a){return a.url?0==a.url.indexOf("#/"+b.mainMenu):a.children&&a.children.length?0==a.children[0].url.indexOf("#/"+b.mainMenu):!1},b.isSubmenuActive=function(a){return a.url?0==a.url.indexOf("#/"+b.mainMenu+"/"+b.subMenu):!1},a.pagerCache={},c()}]);var Pager=function(){this.pageSize=10,this.page=1,this.totalCount=null,this.numPages=null};Pager.prototype.toQS=function(){return"size="+this.pageSize+"&page="+(this.page-1)};var modes=["categories","comments","pois","users"];angular.module("halSearchApp").controller("MainCtrl",["$scope","$resource","usSpinnerService","categoryService","commentsService","poiService","userService",function(a,b,c,d,e,f,g){a.baseUrl=baseUrl,a.searchText="",a.pager=new Pager,a.cats=[],a.comments=[],$("#main-tabs a").click(function(b){b.preventDefault(),a.searchText="",a.pager=new Pager,$(this).tab("show"),a.showCurrentTab()}),setTimeout(function(){$("#main-search").focus(),a.showCurrentTab()}),a.handleSearchClick=function(){a.pager=new Pager,a.showCurrentTab()},a.showCurrentTab=function(b){"categories"==h()?a.showCats(b):"comments"==h()?a.showComments(b):"pois"==h()?a.showPois(b):"users"==h()&&a.showUsers(b),$("#main-search").focus()},a.showCats=function(b){!b&&a.cats&&a.cats.length||c.spin("tab-spinner"),d.search(a.searchText,a.pager,function(b){a.cats=b,c.stop("tab-spinner")})},a.showComments=function(b){!b&&a.comments&&a.comments.length||c.spin("tab-spinner"),e.search(a.searchText,isSuperAdmin,universityId,a.pager,function(b){a.comments=b,c.stop("tab-spinner")})},a.showPois=function(b){!b&&a.pois&&a.pois.length||c.spin("tab-spinner"),f.search(a.searchText,isSuperAdmin,universityId,a.pager,function(b){a.pois=b,c.stop("tab-spinner")})},a.showUsers=function(b){!b&&a.users&&a.users.length||c.spin("tab-spinner"),g.search(a.searchText,isSuperAdmin,universityId,a.pager,function(b){a.users=b,c.stop("tab-spinner")})};var h=function(){return $("#main-tabs li.active a").attr("aria-controls")}}]),halApp.controller("CtrlNotifications",["$rootScope","$scope","$location","notificationService","universityService",function(a,b,c,d,e){b.items=null,b.pager=b.pagerCache.notifications?b.pagerCache.notifications:new Pager,b.pagerCache.notifications=null,b.loadItems=function(){d.loadItems(isSuperAdmin,universityId,b.pager,function(a){b.items=a})},e.loadItems(function(){b.loadItems()}),b.handleDeleteClick=function(a){halApp.showDialog("Confirmez l'op&eacute;ration","Supprim&eacute; notification?",function(){d.remove(a,function(){b.pagerCache.notifications=null,halApp.hideDialog(),halApp.showAlert("Notification supprim&eacute;!"),b.loadItems()})})},b.handleEditClick=function(d){a.pagerCache.notifications=b.pager,c.path("/notifications/"+(d?d.id:0)+"/edit")}}]),halApp.controller("CtrlNotificationEdit",["$scope","$routeParams","$validator","notificationService","universityService",function(a,b,c,d,e){a.itemId=parseInt(b.itemId),a.unis=null,e.loadItems(function(b){a.unis=b,d.getItem(a.itemId,function(b){a.item=$.extend({},b),0!=a.itemId||isSuperAdmin||(a.item.universityId=universityId)})}),a.handleSaveClicked=function(b){var e=!b.id;c.validate(a,"item").success(function(){d.save(b,function(a){e?(location.hash="#/notifications/"+a.id+"/edit",setTimeout(function(){halApp.showAlert("Notification &eacute;tabli!")},0)):halApp.showAlert("Notification sauv&eacute;!"),location.hash="#/notifications"})}).error(function(){})},a.isSuperAdmin=function(){return isSuperAdmin}}]),halApp.controller("CtrlFeeds",["$rootScope","$scope","$location","feedService","universityService",function(a,b,c,d,e){b.items=null,b.pager=b.pagerCache.feeds?b.pagerCache.feeds:new Pager,b.pagerCache.feeds=null,b.loadItems=function(){d.loadItems(isSuperAdmin,universityId,b.pager,function(a){b.items=a})},e.loadItems(function(){b.loadItems()}),b.handleDeleteClick=function(a){halApp.showDialog("Confirmez l'op&eacute;ration","Supprim&eacute; Flux?",function(){d.remove(a,function(){b.pagerCache.feeds=null,halApp.hideDialog(),halApp.showAlert("Flux supprim&eacute;!"),b.loadItems()})})},b.handleEditClick=function(d){a.pagerCache.feeds=b.pager,c.path("/feeds/"+(d?d.id:0)+"/edit")}}]),halApp.controller("CtrlFeedEdit",["$scope","$routeParams","$validator","feedService","universityService",function(a,b,c,d,e){a.types=["RSS","RESTO"],a.itemId=parseInt(b.itemId),a.unis=null,e.loadItems(function(b){a.unis=b,d.getItem(a.itemId,function(b){a.item=$.extend({},b),0!=a.itemId||isSuperAdmin||(a.item.universityId=universityId)})}),a.handleSaveClicked=function(b){var e=!b.id;c.validate(a,"item").success(function(){d.save(b,function(a){e?(location.hash="#/feeds/"+a.id+"/edit",setTimeout(function(){halApp.showAlert("Flux &eacute;tabli!")},0)):halApp.showAlert("Flux sauv&eacute;!"),location.hash="#/feeds"})})},a.isSuperAdmin=function(){return isSuperAdmin}}]),halApp.controller("CtrlMenus",["$rootScope","$scope","$location","menuService","universityService",function(a,b,c,d,e){b.items=null,b.pager=b.pagerCache.menus?b.pagerCache.menus:new Pager,b.pagerCache.menus=null,b.loadItems=function(){d.loadItems(isSuperAdmin,universityId,b.pager,function(a){b.items=a})},e.loadItems(function(){b.loadItems()}),b.handleDeleteClick=function(a){halApp.showDialog("Confirmez l'op&eacute;ration","Supprim&eacute; Menu?",function(){d.remove(a,function(){b.pagerCache.menus=null,halApp.hideDialog(),halApp.showAlert("Menu supprim&eacute;!"),b.loadItems()})})},b.handleEditClick=function(d){a.pagerCache.menus=b.pager,c.path("/menus/"+(d?d.id:0)+"/edit")}}]),halApp.controller("CtrlMenuEdit",["$scope","$routeParams","$validator","menuService","universityService",function(a,b,c,d,e){a.groups=["MS","AU","TT","MU"],a.submitTracking=!1,a.itemId=parseInt(b.itemId),a.unis=null,e.loadItems(function(b){a.unis=b,d.getItem(a.itemId,function(b){a.item=$.extend({},b),0!=a.itemId||isSuperAdmin||(a.item.universityId=universityId)})}),a.validateUrlOrContent=function(a){var b=a.url&&a.url.replace(" ",""),c=a.content&&a.content.replace(" ","");return b&&!c||c&&!b},a.handleSaveClicked=function(b){a.submitTracking=!0;var e=!b.id;c.validate(a,"item").success(function(){a.validateUrlOrContent(b)&&d.save(b,function(a){e?(location.hash="#/menus/"+a.id+"/edit",setTimeout(function(){halApp.showAlert("Menu &eacute;tabli!")},0)):halApp.showAlert("Menu sauv&eacute;!"),location.hash="#/menus"})})},a.isSuperAdmin=function(){return isSuperAdmin}}]);