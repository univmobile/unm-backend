<!doctype html> <html lang="fr" class="no-js"> <head> <meta charset="utf-8"> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> <meta http-equiv="Content-Language" content="fr"> <title>UnivMobile</title> <meta name="description" content=""> <meta name="viewport" content="width=device-width"> <link href="${baseUrl}css/backend.css" rel="stylesheet"> <link rel="stylesheet" href="${baseUrl}css/vendor.17375090.css"> <link rel="stylesheet" href="${baseUrl}css/main.036ed1fa.css"> <script>var universityId = ${universityId};
      var isSuperAdmin = ${isSuperAdmin};
      var baseUrl = "${baseUrl}";</script>  <body ng-app="halSearchApp" ng-controller="CtrlApp"> <!--[if lt IE 7]>
      <p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
    <![endif]--> <jsp:include page="div-entered.h.jsp"></jsp:include><input type="hidden"> <div class="container"> <!--
      <div class="header">
        <ul class="nav nav-pills pull-right">
          <li ng-class="{ 'active' : mainMenu == '' }"><a ng-href="#/">Main</a></li>
          <li ng-class="{ 'active' : mainMenu == 'notifications' }"><a ng-href="#/notifications">Notifications</a></li>
          <li ng-class="{ 'active' : mainMenu == 'feeds' }"><a ng-href="#/feeds">Feeds</a></li>
          <li ng-class="{ 'active' : mainMenu == 'menus' }"><a ng-href="#/menus">Menus</a></li>
        </ul>
      </div>
      --> <div ng-view=""></div> </div> <div id="alert-global" class="alert alert-global" role="alert"></div> <div id="dialog-global" class="modal delete"> <div class="modal-dialog"> <div class="modal-content"> <div class="modal-header"> <a href="#" class="close" data-dismiss="modal"></a> <h3 id="dialog-title">Удалить объект</h3> </div> <div class="modal-body"> <div id="dialog-body" class="modal-body-inner"> Вы уверены? </div> </div> <div class="modal-footer"> <button id="dialog-global-btn-ok" class="btn btn-primary primary">OK</button> <button id="dialog-global-btn-cancel" class="btn btn-secondary secondary">Cancel</button> </div> </div> </div> </div> <!--[if lt IE 9]>
    <script src="${baseUrl}js/oldieshim.76f279db.js"></script>
    <![endif]--> <script src="${baseUrl}js/vendor.d546a3d4.js"></script> <script src="${baseUrl}js/scripts.fb9b3a1a.js"></script>  