<!DOCTYPE html>
<html lang="fr">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="img/favicon.ico">

    <title>UnivMobile Geocampus</title>

    <!-- Bootstrap core CSS -->
    <link href="${baseURL}/css/backend.css" rel="stylesheet">
    
    <link href="${baseURL}/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="${baseURL}/css/dashboard.css" rel="stylesheet">

    <!-- JSTree -->
    <link rel="stylesheet" href="${baseURL}/js/themes/default/style.min.css" />

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
    <link rel="stylesheet" href="${baseURL}/css/jquery.fileupload.css">
      

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- JSTree -->
    <script src="${baseURL}/js/jstree.min.js"></script>
      
    <!--<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&sensor=SET_TO_TRUE_OR_FALSE"></script>-->
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?libraries=geometry"></script>
      
    <script type='text/javascript' src='${baseURL}/js/knockout-3.2.0.js'></script>

    <!-- Placed at the end of the document so the pages load faster -->
      
    <script src="${baseURL}/js/vendor/jquery.ui.widget.js"></script>
    <script src="${baseURL}/js/jquery.iframe-transport.js"></script>
    <script src="${baseURL}/js/jquery.fileupload.js"></script>      

    <script src="${baseURL}/js/bootstrap.min.js"></script>

    <script src="${baseURL}/js/geocampus.js"></script>

    <style>
        #map_canvas { height: 800px; width: 100%; }
        #img_canvas { height: 800px; width: 100%; }
        div.top-section {
            margin-top: 0;
            padding-top: 10px;
        }
        div.sidebar {
            top: 24px;
        }
        #div-entered {
            background-color: #79b8d9;
            color: #fff;
            font-size: small;
            /*padding: 2px 0;*/
            text-align: center;
        }
        /*
        .btn-file {
            position: relative;
            overflow: hidden;
        }
        .btn-file input[type=file] {
            position: absolute;
            top: 0;
            right: 0;
            min-width: 100%;
            min-height: 100%;
            font-size: 100px;
            text-align: right;
            filter: alpha(opacity=0);
            opacity: 0;
            outline: none;
            background: white;
            cursor: inherit;
            display: block;
        }*/        
    </style>
    <script>
        var baseUrl = '${baseURL}/';
    </script>
    <jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body>
	<jsp:include page="div-entered.h.jsp"/>
    <div id="bs-container" class="container-fluid">
        <div class="row">
            <div class="col-sm-4 col-md-3 sidebar top-section">

            <ul class="nav nav-tabs">
                
              <li role="presentation" data-bind="css: { active: activeTab() == 'pois' }"><a href="#" data-bind="click: function(data, event) { switchTab('pois') }">POIs</a></li>
              <li role="presentation" data-bind="css: { active: activeTab() == 'bonplans' }"><a href="#" data-bind="click: function(data, event) { switchTab('bonplans') }">Bon Plans</a></li>
            <!--
              <li role="presentation" data-bind="css: { active: activeTab() == 'images' }"><a href="#" data-bind="click: function(data, event) { switchTab('images') }">Images</a></li>-->
            </ul>
            <p></p>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="btn-toolbar" role="toolbar">
                        <div class="btn-group  btn-group-xs pull-right" role="group">
                          <button type="button" class="btn btn-default" data-bind="click: createRootPoi"><span class="glyphicon glyphicon-plus" aria-label="Add root"></span></button>
                          <button type="button" class="btn btn-default" data-bind="enable: activePoi().id(), click: createPoi"><span class="glyphicon glyphicon-plus-sign" aria-label="Add child"></span></button>
                          <button type="button" class="btn btn-default" data-bind="click: editPoi"><span class="glyphicon glyphicon-edit" aria-label="Edit"></span></button>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <p data-bind="visible: pois().length == 0">
                        S&eacute;lectionnez une r&eacute;gion et une cat&eacute;gorie pour voir POI connexes
                    </p>
                    <div id="jstree1" data-bind="visible: pois().length > 0">
                        <ul data-bind="template: { name: 'treenode-template', foreach: pois }"></ul>
                    </div>
                </div>
            </div>
            </div>
        <div id="mapView" class="col-sm-8 col-sm-offset-2 col-md-9 col-md-offset-3 main top-section" data-bind="visible: activeTab() != 'images'">
            <!--<pre data-bind="text: ko.toJSON(pois, null, 2)"></pre>-->
            <div class="btn-group pull-right"  data-bind="visible: activeTab() == 'pois'">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                Cat&eacute;gorie <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" data-bind="foreach: categoriesUniversities">
                <li><a href="#" data-bind="text: name, click: $parent.changeCategoryUniversities"></a></li>
              </ul>
            </div>
            <div class="btn-group pull-right" data-bind="visible: activeTab() == 'bonplansDisabled'">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                Cat&eacute;gorie <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" data-bind="foreach: categoriesBonplans">
                <li><a href="#" data-bind="text: name, click: $parent.changeCategoryBonplans"></a></li>
              </ul>
            </div>
            <div class="btn-group pull-right">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                R&eacute;gion <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" data-bind="foreach: regions">
                <li><a href="#" data-bind="text: name, click: $parent.changeRegion"></a></li>
              </ul>
            </div>
            
          <h1 class="page-header text-left">POIs <small data-bind="text: activeRegion().name"></small><small data-bind="visible: activeRegion().label">&nbsp;</small><small data-bind="visible: activeTab() == 'pois', text: activeCategoryUniversities().name"></small><small data-bind="visible: activeTab() == 'bonplans', text: activeCategoryBonplans().name"></small></h1>
            
          <div class="row placeholders">
              <div id="map_canvas"></div>
          </div>

        </div>
      </div>

      <div id="imgView" class="col-sm-8 col-sm-offset-2 col-md-9 col-md-offset-3 main" data-bind="visible: activeTab() == 'images'">
            <div class="btn-group pull-right">
              <button type="button" class="btn btn-default" data-bind="click: openImageMapModal"><span class="glyphicon glyphicon-plus"></span></button>
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                Images <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" data-bind="foreach: images">
                <li><a href="#" data-bind="text: name, click: $parent.changeImage"></a></li>
              </ul>
            </div>
            
          <h1 class="page-header">Images <small data-bind="text: activeImage().name"></small></h1>

          <div class="row placeholders">
              <div id="img_canvas"></div>
          </div>

        </div>
      </div>

    </div>

    <div id="poiModal" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title"><span data-bind="text: activePoi().name()">POI nom</span><span data-bind="if: !activePoi().name()">POI nom</span></h4>
          </div>
          <div class="modal-body">

              <ul class="nav nav-tabs">
                <li role="presentation" data-bind="css: { active: activePoiTab() == 'details' }"><a href="#" data-bind="click: function(data, event) { switchPoiTab('details') }">D&eacute;tails</a></li>
                  <!--
                <li role="presentation" data-bind="css: { active: activePoiTab() == 'comments' }"><a href="#" data-bind="click: function(data, event) { switchPoiTab('comments') }">Commentaires</a></li>-->
              </ul>
              <p></p>
              
            <form class="form-horizontal" role="form" data-bind="with: activePoi(), visible: activePoiTab() == 'details'">
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Nom</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" required="required" data-bind="value: name">
                </div>
              </div>
              <div class="form-group" data-bind="visible: $parent.universities().length > 1 && allowsUniversityFilling()">
                <label for="inputPassword3" class="col-sm-2 control-label">Universit&eacute;</label>
                <div class="col-sm-10">
                  <select class="form-control" required="required" data-bind="options: $parent.universities, optionsValue: 'id', optionsText: 'name', value: university">
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label for="inputPassword3" class="col-sm-2 control-label">Cat&eacute;gorie</label>
                <div class="col-sm-10">
                  <select class="form-control" required="required" data-bind="options: $parent.getAvailableCategories(), optionsValue: 'id', optionsText: 'name', value: category"></select>
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Emplacement</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: floor">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Horaires</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: openingHours">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">T&eacute;l&eacute;phones</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: phones">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Adresse</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: address">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">E-mail</label>
                <div class="col-sm-10">
                  <input type="email" class="form-control" id="inputEmail3" placeholder="" data-bind="value: email">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Accès</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: itinerary">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Site web</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" data-bind="value: url">
                </div>
              </div>
            </form>

            <div data-bind="with: activePoi(), visible: activePoiTab() == 'comments'">
                <table class="table table-condensed table-striped">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>#</th>
                            <th>#</th>
                            <th>#</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                        <tr>
                            <td>1</td>
                            <td>lluporini@blitzit.com.ar</td>
                            <td>Comentario 1</td>
                            <td>Unpublish</td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>jluporini@blitzit.com.ar</td>
                            <td>Comentario 2</td>
                            <td>Publish</td>
                        </tr>
                    </tbody>
                </table>
                <form class="form-horizontal" role="form">
                  <div class="form-group">
                    <label for="inputEmail3" class="col-sm-2 control-label">Commentaire</label>
                    <div class="col-sm-10">
                      <textarea class="form-control"></textarea>
                    </div>
                  </div>
                </form>
            </div>
                
            </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-bind="click: cancelPoi">Annuler</button>
            <button type="button" class="btn btn-primary" data-bind="click: savePoi">Enregistrer</button>              
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

    <div id="imageMapModal" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
            <h4 class="modal-title">Image Map</span></h4>
          </div>
          <div class="modal-body">

            <form class="form-horizontal" role="form">
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Nom</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="" required="required">
                </div>
              </div>
              <div class="form-group">
                <label for="inputEmail3" class="col-sm-2 control-label">Emplacement</label>
                <div class="col-sm-10">
                  <input type="text" class="form-control" id="inputEmail3" placeholder="">
                </div>
              </div>
            </form>

          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
    
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!--
    <script src="../../assets/js/docs.min.js"></script>
    -->
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <!--
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
    -->
  </body>
</html>

<!-- 
1. Validaciones form de detalles
2. Comentarios por AJAX
3.
-->