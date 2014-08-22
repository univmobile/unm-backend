<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<!--
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.min.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.structure.min.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.theme.min.css">
-->
<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css">
<style type="text/css">
body {
	xposition: relative;
	xheight: 100%;
}
#div-map {
	height: 200px;
	width: 900px;
	xborder: 1px solid #f00;
}
#div-left {
	float: left;
	border-right: 4px solid #ccc;
}
#div-left-bottom {
	border-top: 4px solid #ccc;
	xbackground-color: #ff0;
}
#div-left-top {
	xbackground-color: #0f0;
}
#div-left-top {
	overflow: scroll;
}
/*
.ui-tabs-nav .ui-state-active a, 
.ui-tabs-nav .ui-state-active a:link, 
.ui-tabs-nav .ui-state-active a:visited {
	color: #f00;
}
*/
.ui-tabs-nav .ui-state-default a, 
.ui-tabs-nav .ui-state-default a:link, 
.ui-tabs-nav .ui-state-default a:visited {
	xcolor: #ff0;
	font-size: 10px;
}
</style>
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<!--
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.min.js"></script>
-->
<script type="text/javascript" src="http://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
<!--
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${API_KEY}"></script>
-->
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js"></script>
<script type="text/javascript">

	<% pageContext.setAttribute("newLine", "\n"); %>
	
	// 0. GENERATED
	
	var pois = [<c:forEach
			var="category" items="${pois}"><c:forEach
			var="poi" items="${category.pois}">{
		name: "${poi.name}",
		lat: ${poi.latitude},
		lng: ${poi.longitude},<c:if test="${poi.address != null and poi.address != ''}">
		address: "${fn:replace(poi.address, newLine, ' ')}",</c:if><c:if
			test="${poi.floor != null and poi.floor != ''}">
		floor: "${poi.floor}",</c:if><c:if test="${poi.openingHours != null and poi.openingHours != ''}">
		openingHours: "${poi.openingHours}",</c:if><c:if test="${poi.phone != null and poi.phone != ''}">
		phone: "${poi.phone}",</c:if><c:if test="${poi.email != null and poi.email != ''}">
		email: "${poi.email}",</c:if><c:if test="${poi.itinerary != null and poi.itinerary != ''}">
		itinerary: "${poi.itinerary}",</c:if><c:if test="${poi.imageUrl != null and poi.imageUrl != ''}">
		imageUrl: "${poi.imageUrl}",
		imageWidth: ${poi.imageWidth},
		imageHeight: ${poi.imageHeight},</c:if><c:if test="${poi.url != null and poi.url != ''}">
		url: "${poi.url}",</c:if>
		markerType: "${poi.markerType}",
		markerIndex: "${poi.markerIndex}",
		id: ${poi.id}
	},</c:forEach></c:forEach>];

	// 1. LAYOUT
	
	function resizeLeft(event, ui) {
	
		var totalWidth = $('#body-pois').width() - 4;
		
		var width = $('#div-left').width();
				
		// if (width < 200) { width = 200; }
		if (width > totalWidth - 200) { width = totalWidth - 200; }
				
		$('#div-left').css('width', width);

		$('#div-map').css('width', totalWidth - width);
	}

	function resizeTop(event, ui) {
	
		var totalHeight = $(window).height() - $('#div-entered').height() - 8	;
		
		var height = $('#div-left-top').height();
				
		if (height < 200) { height = 200; }
		if (height > totalHeight - 200) { height = totalHeight - 200; }
				
		$('#div-left-top').css('height', height);

		$('#div-left-bottom').css('height', totalHeight - height);
	}
	
	function resizeHeights() {
	
		var height = $(window).height() - $('#div-entered').height() - 4;
		$('#div-left').css('height', height);
		$('#div-map').css('height', height);

		google.maps.event.trigger(map, 'resize');
	}
	
	var markerImageBaseURL = '${baseURL}/img/markers/marker_green';
	var markerImageBaseSize = new google.maps.Size(22, 40);	
	var markerImageSuffix = '.png';	
	var markerImageSize = markerImageBaseSize;
	
	if (window.devicePixelRatio > 1.5) { // Retina display
		markerImageSuffix = '@2x.png';
		markerImageSize = new google.maps.Size(44, 80);
	}

	var markerImageBaseOrigin = new google.maps.Point(0, 0);
	var markerImageBaseAnchor = new google.maps.Point(11, 40);	
	var markerAnchorPoint = new google.maps.Point(5, -28); // For infoWindows
	
	// 3. INTERACTION
	
	var map;
	var infoWindow = null;
	var swallowNextClick = false;

	function onfocusPoi(poiId) {

		swallowNextClick = false;

		for (var i = 0; i < pois.length; ++i) {
			
			var poi = pois[i];
			
			if (poi.id == poiId) {
		
				selectPoi(poi);
							
				var newCenter = new google.maps.LatLng(poi.lat, poi.lng);
				
				if (newCenter.equals(map.getCenter())) {

					if (infoWindow == null || infoWindow.poi.id != poiId) {
				
						swallowNextClick = true; // Do not toggle
					}
				}
				
				openInfoWindow(poi);	
				
				break;
			}
		}
	}
	
	function onclickPoi(poiId) {

		for (var i = 0; i < pois.length; ++i) {
			
			var poi = pois[i];
			
			if (poi.id == poiId) {
		
				selectPoi(poi);
			
				var newCenter = new google.maps.LatLng(poi.lat, poi.lng);
				
				if (newCenter.equals(map.getCenter())) {

					if (infoWindow != null && infoWindow.poi.id == poiId) {
				
						if (!swallowNextClick) {
						
							infoWindow.close(); // Toggle infoWindow
					
							infoWindow = null;
						}

					} else {
				
						openInfoWindow(poi);	
					}
				
				} else {
				
					map.setCenter(newCenter);
				
					openInfoWindow(poi);	
				}
				
				break;
			}
		}
		
		swallowNextClick = false;
	}

	/**
	 *	select the corresponding POI within the list.
	 */
	function selectPoi(poi) {
	
		var liId = 'li-poi-' + poi.id;
		
		$('li.poi').each(function() {			
			$(this).toggleClass('selected', $(this).attr('id') == liId);
		});
		
		$('#div-details').removeClass('hidden');
		
		$('#div-details div.name').html(poi.name);
		
		$('#div-details div.field.active span.id').html(poi.id);
		
		setDetailsField('floor',        poi.floor);
		setDetailsField('openingHours', poi.openingHours);
		setDetailsField('phone',        poi.phone);
		setDetailsField('address',      poi.address);
		setDetailsField('email',        poi.email);
		setDetailsField('itinerary',    poi.itinerary);
		setDetailsField('url',          poi.url);
		setDetailsField('coordinates',  poi.lat + ',' + poi.lng);
	}
	
	function setDetailsField(fieldName, content) {
	
		$('#div-details div.field.' + fieldName)
			.toggleClass('hidden', content == null)
			.children('div.content').html(content);
	}
	
	/**
	 *	open the corresponding infoWindow on the map.
	 */
	function openInfoWindow(poi) {
	
		if (infoWindow != null) {
			
			if (infoWindow.poi.id == poi.id) return;
			
			infoWindow.close();
		}
		
		var div = $('#div-hidden div.infoWindow').clone();
		
		div.children('span.name').html(poi.name);
		div.children('span.address').html(poi.address);
		
		var imgDiv = div.children('div');
				
		if (poi.imageUrl == null) {
			imgDiv.addClass('hidden');
		} else {
			imgDiv.removeClass('hidden');
			var img = imgDiv.children('img');
			img.attr('src', poi.imageUrl);
			if (poi.imageWidth < poi.imageHeight) {
				img.removeClass('height100').addClass('width100');				
			} else {
				img.removeClass('width100').addClass('height100');
			}
		}
		
		div = div[0];
		
		infoWindow = new google.maps.InfoWindow({
			content: div
		});
		
		infoWindow.poi = poi;

		google.maps.event.addListener(infoWindow, 'closeclick', function() {
			infoWindow = null;
		});
		
		infoWindow.open(map, poi.marker);
	}
	
	// 8. INIT
	
	function initialize() {
	
		// 1. MAIN COMPONENTS
		
		var mapOptions = {
			center: new google.maps.LatLng(${map.center}),
			zoom: ${map.zoom}
		};
		
		map = new google.maps.Map(
			document.getElementById('div-map'), mapOptions
		);
	
		$('#div-left-top-tabs').tabs();
		$('#div-left-bottom-tabs').tabs();

		// 2. MAIN COMPONENTS BEHAVIOUR
		
		$('#div-left').resizable({
			handles: 'e',
			minWidth: 200,
			resize: resizeLeft,
			stop: function(event, ui) {			
			
				resizeHeights();
			}
		});
	
		$('#div-left-top').resizable({
			handles: 's',
			resize: resizeTop,
			stop: function(event, ui) {			
			
				resizeHeights();
			} 
		});

		$('#div-left-top-tabs').tabs('option', 'disabled', [1, 2, 3]);
		// $('#div-left-top-tabs').tabs('option', 'disabled', [2]);

		$('#div-left-bottom-tabs').tabs('option', 'disabled', [1]);

		// 3. MAIN COMPONENTS LAYOUT
		
		resizeHeights();
		resizeLeft();		
		resizeTop();
		
		// 4. GOOGLE MAP MARKERS
		
		for (var i = 0; i < pois.length; ++i) {
		
			var poi = pois[i];
			var lat = poi.lat;
			var lng = poi.lng;
			var name = poi.name;

			var markerImage = {
				url: markerImageBaseURL + poi.markerIndex + markerImageSuffix,
				size: markerImageSize,
				scaledSize: markerImageBaseSize,
				origin: markerImageBaseOrigin,
				anchor: markerImageBaseAnchor
			};

			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(lat, lng),
				map: map,
				title: name,
				icon: markerImage,
				anchorPoint: markerAnchorPoint
			});
			
			poi.marker = marker;
			
			google.maps.event.addListener(marker, 'click', 
				// Use a closure to create a safe scope for (poi, marker)
				(function(poi) { return function() {
					
					selectPoi(poi);
					
					if (infoWindow != null && infoWindow.poi.id == poi.id) {
					
						infoWindow.close(); // Toggle infoWindow
						
						infoWindow = null;
						
					} else {
					
						openInfoWindow(poi);
					}
					
				}})(poi)
			);
		}
		
		// 9. END
	}
	
	google.maps.event.addDomListener(window, 'load', initialize);
	
</script>
</head>
<body id="body-pois" class="entered">

<jsp:include page="div-entered.h.jsp"/>

<div class="xbody">
<form>

<div id="div-left">

<div id="div-left-top">

	<div id="div-left-top-tabs" class="pois">
	<ul>
		<li><a href="#div-left-top-tabs-1">Tous</a></li>
    	<li><a href="#div-left-top-tabs-3">Université</a></li>
	    <li><a href="#div-left-top-tabs-2">Favoris</a></li>
	    <li><a href="#div-left-top-tabs-4">Recherche</a></li>
	</ul>
	
	<div id="div-left-top-tabs-1">
	
	<!--
	<h1 title="Build ${buildInfo.buildDisplayName}
	${buildInfo.buildId}
	${buildInfo.gitCommitId}">
	Administration d’UnivMobile
	</h1>
	
	<h2>POIs</h2>
	-->

	<ul id="ul-pois">
	<c:forEach var="category" items="${pois}">
	<li>
	<h2>${category.name}</h2>
	<ul>
	<c:forEach var="poi" items="${category.pois}">
		<li class="poi ${poi.markerIndex}"	
			onclick="onclickPoi(${poi.id});"	
			id="li-poi-${poi.id}">
		<a href="#"
			id="link-poi-${poi.id}"
			onfocus="onfocusPoi(${poi.id});">
		${poi.name}
		<c:if test="${poi.address != null}">
			<br>
			<span class="address">
				${poi.address}
			</span>
		</c:if>
		</a>
	</c:forEach>
	</ul>
	</c:forEach>
	</ul>
	
	</div> <!-- end of #div-left-top-tabs-1 -->

	<div id="div-left-top-tabs-2">
	
		Favoris
		
	</div> <!-- end of #div-left-top-tabs-2 -->

	<div id="div-left-top-tabs-3">
	
		Université
		
	</div> <!-- end of #div-left-top-tabs-3 -->

	<div id="div-left-top-tabs-4">
	
		Recherche
		
	</div> <!-- end of #div-left-top-tabs-4 -->
	
	</div> <!-- end of #div-left-top-tabs -->
	
</div> <!-- end of #div-left-top -->

<div id="div-left-bottom">

	<div id="div-left-bottom-tabs">
	<ul>
		<li><a href="#div-left-bottom-tabs-1">Détails</a></li>
	    <li><a href="#div-left-bottom-tabs-2">Commentaires</a></li>
	</ul>
	
	<div id="div-left-bottom-tabs-1">

	<div id="div-details" class="hidden">
	<div class="field name">
	</div>
	<div class="field active">
		<div class="title">Statut</div>
		<div class="content">
			<span>POI </span><span class="id"></span>
			<label for="radio-active-yes">Actif</label>
			<input type="radio" name="active" value="yes" checked
				id="radio-active-yes" disabled>
			<label for="radio-active-no">Inactif</label>
			<input type="radio" name="active" value="no"
				id="radio-active-no" disabled>
		</div>
	</div>	
	<div class="field floor">
		<div class="title">Emplacement</div>
		<div class="content"></div>
	</div>
	<div class="field openingHours">
		<div class="title">Horaires</div>
		<div class="content"></div>
	</div>	
	<div class="field phone">
		<div class="title">Téléphone</div>
		<div class="content"></div>
	</div>	
	<div class="field address">
		<div class="title">Adresse</div>
		<div class="content"></div>
	</div>	
	<div class="field email">
		<div class="title">E-mail</div>
		<div class="content"></div>
	</div>	
	<div class="field itinerary">
		<div class="title">Accès</div>
		<div class="content"></div>
	</div>	
	<div class="field url">
		<div class="title">Site web</div>
		<div class="content"></div>
	</div>	
	<div class="field coordinates">
		<div class="title">Coordonnées Lat/Lng</div>
		<div class="content"></div>
	</div>	

	<div id="div-details-buttons">
	
	<button id="button-edit" disabled>
		Modifier…
	</button>

	<!--
	<button id="button-save">
		Annuler
	</button>

	<button id="button-save">
		Enregistrer
	</button>

	<button id="button-apply">
		Appliquer
	</button>
	-->
	
	</div> <!-- end of #div-details-buttons -->
	
	</div> <!-- end of #div-details -->
	
	</div> <!-- end of #div-left-bottom-tabs-1 -->
	
	<div id="div-left-bottom-tabs-2">

	Commentaires

	</div> <!-- end of #div-left-bottom-tabs-2 -->

	</div> <!-- end of #div-left-bottom-tabs -->
	
</div> <!-- end of #div-left-bottom -->

</div> <!-- end of #div-left -->

<div id="div-right">

	<div id="div-map"></div>

</div> <!-- end of #div-right -->

</form>
</div> <!-- end of div.body -->

<div id="div-hidden" style="display: none;">
<div class="infoWindow">
<div class="img">
	<img>
</div>
<span class="name">
</span>
<br>
<span class="address">
</span>
</div> <!-- end of #div-infoWindow -->
</div> <!-- end of div[@style = 'display: none;'] -->

</body>
</html>