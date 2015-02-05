angular.module('halApp.templates', []).run(['$templateCache', function($templateCache) {
  "use strict";
  $templateCache.put("views/feed-edit.html",
    "<div><div class=page-title><h1 ng-if=item.id>Modifier Flux</h1><h1 ng-if=!item.id>Ajouter Flux</h1></div><form><div class=form-group><label for=name class=control-label>Nom *</label><input class=form-control id=name ng-model=item.name validator=\"[required]\"></div><div class=form-group><label for=university class=control-label>Universit&eacute;</label><select class=form-control id=university ng-model=item.universityId ng-options=\"uni.id as uni.title for uni in unis\"><option ng-if=isSuperAdmin() value=\"\">---</option></select></div><div class=form-group><label for=type class=control-label>Type</label><select class=form-control id=type ng-model=item.type ng-options=\"type for type in types\"></select></div><div class=form-group><label for=url class=control-label>URL *</label><input class=form-control id=url ng-model=item.url validator=\"[required]\"></div><button type=button class=\"btn btn-success\" ng-click=\"handleSaveClicked( item )\">Enregister</button> <a href=#/feeds class=\"btn btn-danger\">Annuler</a></form></div>");
  $templateCache.put("views/feeds.html",
    "<div><div class=page-title><div class=col-sm-9><h1>Flux</h1></div><div class=col-sm-3><a href=#/feeds/0/edit class=\"btn btn-success pull-right\">Ajouter un Flux</a></div><div class=clearfix></div></div><table class=\"table table-striped table-condensed table-feeds\"><tr><th>Université</th><th>Nom</th><th>Type</th><th>Url</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=5>Chargement ...</td></tr><tr ng-if=\"items && !items.length\"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"item in items\"><td>{{item.getUni().title}}</td><td>{{item.name}}</td><td>{{item.type}}</td><td>{{item.url}}</td><td><a class=\"btn btn-info btn-sm\" href=#/feeds/{{item.id}}/edit><span class=\"glyphicon glyphicon-pencil\"></span></a> <button class=\"btn btn-danger btn-sm\" ng-click=\"handleDeleteClick( item )\"><span class=\"glyphicon glyphicon-remove\"></span></button></td></tr></table></div>");
  $templateCache.put("views/main.html",
    "<div><div class=page-title><h1>Recherche avanc&eacute;e</h1></div><div class=row><div class=col-sm-12><div class=input-group><input class=form-control ng-model=searchText id=\"main-search\"> <span class=input-group-btn><button class=\"btn btn-info\" ng-click=handleSearchClick()>Rechercher</button></span></div></div></div><div role=tabpanel class=main-tabs><ul class=\"nav nav-tabs\" role=tablist id=main-tabs><li role=presentation class=active><a href=#categories aria-controls=categories role=tab data-toggle=tab>Catégories</a></li><li role=presentation><a href=#comments aria-controls=comments role=tab data-toggle=tab>Commentaires</a></li><li role=presentation><a href=#pois aria-controls=pois role=tab data-toggle=tab>POIs</a></li><li role=presentation><a href=#users aria-controls=users role=tab data-toggle=tab>Utilisateurs</a></li></ul><div class=tab-content us-spinner=\"{radius:30, width:8, length: 16, opacity: 0.5}\" spinner-key=tab-spinner><div role=tabpanel class=\"tab-pane active\" id=categories><table class=\"table table-striped table-condensed table-cats\"><tr><th>Nom</th><th>Description</th><th>Actif</th><th class=alt-action>Action</th></tr><tr ng-if=!cats><td colspan=4>Chargement ...</td></tr><tr ng-if=\"cats && !cats.length\"><td colspan=4>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"cat in cats\"><td>{{cat.name}}</td><td>{{cat.description}}</td><td><span ng-if=cat.active class=\"glyphicon glyphicon-ok text-success\"></span> <span ng-if=!cat.active class=\"glyphicon glyphicon-remove text-danger\"></span></td><td><a href=\"{{ baseUrl }}poicategoriesmodify/{{ cat.id }}\">Modifier…</a> <a href=\"{{ baseUrl }}poicategories/{{ cat.id }}\">Sous-catégories…</a></td></tr></table></div><div role=tabpanel class=tab-pane id=comments><table class=\"table table-striped table-condensed table-comments\"><tr><th>Date</th><th>Utilisateurs</th><th>Commentaire</th></tr><tr ng-if=!comments><td colspan=5>Chargement ...</td></tr><tr ng-if=\"comments && !comments.length\"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"comment in comments\"><td>{{comment.postedOn | date:'dd-MM-yyyy HH:mm:ss'}}</td><td>{{comment.author}}</td><td>{{comment.message}}</td></tr></table></div><div role=tabpanel class=tab-pane id=pois><table class=\"table table-striped table-condensed table-pois\"><tr><th>POI</th><th>Adresse</th><th>Action</th></tr><tr ng-if=!pois><td colspan=4>Chargement ...</td></tr><tr ng-if=\"pois && !pois.length\"><td colspan=4>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"poi in pois\"><td>{{poi.name}}</td><td>{{poi.address}}</td><td><a href=\"{{ baseUrl }}poismodify/{{ poi.id }}\">Modifier…</a></td></tr></table></div><div role=tabpanel class=tab-pane id=users><table class=\"table table-striped table-condensed table-users\"><tr><th>Utilisateurs</th><th>rôle</th><th>E-mail</th><th>Action</th></tr><tr ng-if=!users><td colspan=5>Chargement ...</td></tr><tr ng-if=\"users && !users.length\"><td colspan=5>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"user in users\"><td>{{user.username}}</td><td><span ng-if=user.superAdmin>S</span><span ng-if=user.admin>A</span><span ng-if=user.student>E</span></td><td>{{user.email}}</td><td><a href=\"{{ baseUrl }}usermodify/{{ user.id }}\">Modifier…</a></td></tr></table></div><div class=main-search-pager ng-show=\"pager.numPages > 1\"><pagination total-items=pager.totalCount items-per-page=pager.pageSize max-size=10 boundary-links=true class=pagination-sm ng-model=pager.page ng-change=\"showCurrentTab( true )\" num-pages=pager.numPages previous-text=pr&eacute;c&eacute;dent next-text=suivant first-text=première last-text=dernier></pagination><pre>Page: {{pager.page}} / {{pager.numPages}}</pre></div></div></div><script></script></div>");
  $templateCache.put("views/menu-edit.html",
    "<div><div class=page-title><h1 ng-if=item.id>Modifier Menu</h1><h1 ng-if=!item.id>Ajouter Menu</h1></div><form><div class=form-group><label for=name class=control-label>Nom *</label><input class=form-control id=name ng-model=item.name validator=\"[required]\"></div><div class=form-group><label for=university class=control-label>Université</label><select class=form-control id=university ng-model=item.universityId ng-options=\"uni.id as uni.title for uni in unis\"><option ng-if=isSuperAdmin() value=\"\">---</option></select></div><div class=form-group><label for=grouping class=control-label>Groupe *</label><select class=form-control id=type ng-model=item.grouping ng-options=\"grouping for grouping in groups\" validator=[required]></select></div><div class=checkbox><label><input type=checkbox ng-model=\"item.active\"> Actif</label></div><div class=form-group><label for=ordinal class=control-label>Ordre *</label><input class=form-control id=ordinal ng-model=item.ordinal validator=\"[required]\"></div><div class=form-group><label for=url class=control-label>URL</label><input class=form-control id=url ng-model=\"item.url\"></div><div class=form-group><label for=content class=control-label>Content</label><textarea class=\"form-control notification-content\" id=content ng-model=item.content></textarea></div><button type=button class=\"btn btn-success\" ng-click=\"handleSaveClicked( item )\">Enregister</button> <a href=#/menus class=\"btn btn-danger\">Annuler</a></form></div>");
  $templateCache.put("views/menus.html",
    "<div><div class=page-title><div class=col-sm-9><h1>Menus</h1></div><div class=col-sm-3><a href=#/menus/0/edit class=\"btn btn-success pull-right\">Ajouter un Menu</a></div><div class=clearfix></div></div><table class=\"table table-striped table-condensed table-menus\"><tr><th>Université</th><th>Nom</th><th>Groupe</th><th>Actif</th><th>Ordre</th><th>Url</th><th>Content</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=8>Chargement ...</td></tr><tr ng-if=\"items && !items.length\"><td colspan=8>- Aucun donn&eacute;es -</td></tr><tr ng-repeat=\"item in items\"><td>{{item.getUni().title}}</td><td>{{item.name}}</td><td>{{item.grouping}}</td><td><span ng-if=item.active class=\"glyphicon glyphicon-ok text-success\"></span> <span ng-if=!item.active class=\"glyphicon glyphicon-remove text-danger\"></span></td><td>{{item.ordinal}}</td><td>{{item.url}}</td><td>{{item.content}}</td><td><a class=\"btn btn-info btn-sm\" href=#/menus/{{item.id}}/edit><span class=\"glyphicon glyphicon-pencil\"></span></a> <button class=\"btn btn-danger btn-sm\" ng-click=\"handleDeleteClick( item )\"><span class=\"glyphicon glyphicon-remove\"></span></button></td></tr></table></div>");
  $templateCache.put("views/notification-edit.html",
    "<div><div class=page-title><h1 ng-if=item.id>Modifier Notification</h1><h1 ng-if=!item.id>Ajouter Notification</h1></div><form><div class=form-group><label for=university class=control-label>Université</label><select class=form-control id=university ng-model=item.universityId ng-options=\"uni.id as uni.title for uni in unis\"><option ng-if=isSuperAdmin() value=\"\">---</option></select></div><div class=form-group><label for=content class=control-label>Content *</label><textarea class=\"form-control notification-content\" id=content ng-model=item.content validator=[required]></textarea></div><button type=button class=\"btn btn-success\" ng-click=\"handleSaveClicked( item )\">Enregister</button> <a href=#/notifications class=\"btn btn-danger\">Annuler</a></form></div>");
  $templateCache.put("views/notifications.html",
    "<div><div class=page-title><div class=col-sm-9><h1>Notifications</h1></div><div class=col-sm-3><a href=#/notifications/0/edit class=\"btn btn-success pull-right\">Ajouter Notification</a></div><div class=clearfix></div></div><table class=\"table table-striped table-condensed table-notifs\"><tr><th>Université</th><th>Content</th><th class=alt-action>Action</th></tr><tr ng-if=!items><td colspan=3>Chargement ...</td></tr><tr ng-if=\"items && !items.length\"><td colspan=4>- Aucun donn&eacute;e -</td></tr><tr ng-repeat=\"item in items\"><td>{{item.getUni().title}}</td><td>{{item.content}}</td><td><a class=\"btn btn-info btn-sm\" href=#/notifications/{{item.id}}/edit><span class=\"glyphicon glyphicon-pencil\"></span></a> <button class=\"btn btn-danger btn-sm\" ng-click=\"handleDeleteClick( item )\"><span class=\"glyphicon glyphicon-remove\"></span></button></td></tr></table></div>");
}]);
