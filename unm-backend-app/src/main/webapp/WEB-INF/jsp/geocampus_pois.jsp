<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Géocampus</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<!--
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.min.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.structure.min.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.theme.min.css">
<link type="text/css" rel="stylesheet" href="http://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css">
-->
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
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
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<!--
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.custom/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
-->
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
		commentsUrl: "${poi.commentsUrl}",
		id: ${poi.id}
	},</c:forEach></c:forEach>];

	// 1. DATA
	
	/**
	 *	return the POI with the given id, or null.
	 */
	function getPoiById(poiId) {
	
		for (var i = 0; i < pois.length; ++i) {
			
			var poi = pois[i];
			
			if (poi.id == poiId) {
			
				return poi;
			}
		}
		
		return null;
	}
	
	/**
	 *	return the POI currently selected, or null.
	 */
	function getSelectedPoi() {
	
		var poiId = $('#div-details div.field.active span.id').html();
		
		return getPoiById(poiId);
	}
	
	// 2. LAYOUT
	
	function resizeLeft(event, ui) {
	
		var totalWidth = $('#body-geocampus').width() - 4;
		
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

		var leftTopHeight = $('#div-left-top').height();
		var leftBottomHeight = height - leftTopHeight - 5;
		console.log('leftBottomHeight: ' + leftBottomHeight);
		$('#div-left-bottom').css('height', leftBottomHeight); 
		
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

		var poi = getPoiById(poiId);
		
		if (poi != null) {
	
			selectPoi(poi);
						
			var newCenter = new google.maps.LatLng(poi.lat, poi.lng);
			
			if (newCenter.equals(map.getCenter())) {

				if (infoWindow == null || infoWindow.poi.id != poiId) {
			
					swallowNextClick = true; // Do not toggle
				}
			}
			
			openInfoWindow(poi);	
		}
	}
	
	function onclickPoi(poiId) {

		var poi = getPoiById(poiId); 
			
		if (poi != null) {
		
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
		
		$('div.noPoiSelected').addClass('hidden');
				
		$('#div-details').removeClass('hidden');
		
		$('#div-comments').removeClass('hidden');
		
		// $('#div-details div.name').html(poi.name);
		
		$('div.field.name').html(poi.name);
		
		$('#div-details div.field.active span.id').html(poi.id);
		
		setDetailsField('floor',        poi.floor);
		setDetailsField('openingHours', poi.openingHours);
		setDetailsField('phone',        poi.phone);
		setDetailsField('address',      poi.address);
		setDetailsField('email',        poi.email);
		setDetailsField('itinerary',    poi.itinerary);
		setDetailsField('url',          poi.url);
		setDetailsField('coordinates',  poi.lat + ',' + poi.lng);
		
		var active = $('#div-left-bottom-tabs').tabs('option', 'active');
		
		if (active == 1) {
		
			$('div-comments-timeline').html(''); // Empty the timeline
				
			loadPoiComments(poi);
		}
	}
	
	function setDetailsField(fieldName, content) {
	
		$('#div-details div.field.' + fieldName)
			.toggleClass('hidden', content == null)
			.children('div.content').html(content);
	}
	
	function loadPoiComments(poi) {
	
		var timeline = $('#div-comments-timeline');
		
		if (poi == null) {
		
			timeline.html('');
			
			return;
		}
		
		$.ajax({
			url: poi.commentsUrl,
			dataType: 'json',
			error: function(jqXHR, textStatus, errorThrown) {

				var divError = $(document.createElement('div'));
				var divErrorThrown = $(document.createElement('div'));
					
				divError.addClass('error').html(
					'Une erreur s’est produite lors de la récupération des commentaires.'
				);
				
				divError.append(divErrorThrown);
				
				divErrorThrown.addClass('errorThrown').html(
					textStatus + ': ' + errorThrown
				);
				
				timeline.html(divError);
			},
			success: function(json) {
				
				var comments = json.comments;
				
				// console.log('setComments');
			
				var ul = $(document.createElement('ul'));
				
				for (var i = 0; i < comments.length; ++i) {
				
					var comment = comments[i];
					
					var li = $(document.createElement('li'));
					
					ul.append(li);
					
					var imgProfileImage = $(document.createElement('img'));
					var divDisplayName = $(document.createElement('div'));
					var divUsername = $(document.createElement('div'));
					var divText = $(document.createElement('div'));
					var divTimestamp = $(document.createElement('div'));
					var anchorTimestamp = $(document.createElement('a'));
					
					li.append(imgProfileImage);
					li.append(divDisplayName);
					li.append(divUsername);
					li.append(divText);
					li.append(divTimestamp);
					
					imgProfileImage.addClass('profileImage');
					if (comment.author.profileImage != null) {
						imgProfileImage.attr('src', comment.author.profileImage.url);
					}
					divDisplayName.addClass('displayName')
						.html(comment.author.displayName);
					divUsername.addClass('username')
						.html('@' + comment.author.username);
					divTimestamp.addClass('timestamp')
						.append(anchorTimestamp);
					anchorTimestamp.attr('href', comment.url)
						.attr('title', comment.postedAt)
						.html(comment.displayPostedAt);
						
					var message = comment.text;
					
					divText.addClass('text').html(message);
				}
				
				timeline.html(ul); // Erase previous content
			}
		});
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
	
	/**
	 * open a dialog to add a comment under the selected POI. 
	 */
	function openAddCommentDialog() {
		
		var poi = getSelectedPoi();
		
		if (poi == null) {
			return;
		}
		
		$('#div-addCommentDialog').dialog('option', 'title', poi.name);
		
		$('#div-addCommentDialog #text-message').val('');
		
		$('#div-addCommentDialog').dialog('open');
		
		var postCommentUrl = '${postCommentUrl}';
		
		$.ajax({
			url: postCommentUrl,
			error: function(jqXHR, textStatus, errorThrown) {
				
				console.log(textStatus + ': ' + errorThrown);
				
				$('#div-addCommentDialog').dialog('close');
				
				alert('ERREUR : '
					+ 'impossible de contacter le serveur pour l’envoi de commentaires.'
					+ '\r\n\r\n' + textStatus + ': ' + errorThrown
					+ '\r\n\r\n' + 'url: ' + postCommentUrl  
					+ '\r\n\r\n' + jqXHR.responseText
				);
			},
		});		
	}

	function closeAddCommentDialog() {
	
		$('#div-addCommentDialog').dialog('close');		
	}
	
	/**
	 *	send a comment to the backend server.	 
	 */
	function postComment() {
		
		var poi = getSelectedPoi();
		
		if (poi == null) {
			return;
		}
		
		var postCommentUrl = '${postCommentUrl}';
		
		$.ajax({
			type: 'POST',
			url: postCommentUrl,
			data: {
				username: '${user.uid}',
				poi_id: poi.id,
				message: $('#text-message').val()
			},
			error: function(jqXHR, textStatus, errorThrown) {
				
				console.log(textStatus + ': ' + errorThrown);
				
				alert('ERREUR : '
					+ 'impossible d’envoyer le commentaire.'
					+ '\r\n\r\n' + textStatus + ': ' + errorThrown
					+ '\r\n\r\n' + 'url: ' + postCommentUrl  
					+ '\r\n\r\n' + jqXHR.responseText
				);
			},
			success: function(data) {
				
				// var comments = json.comments;
				
				console.log('postComment: OK');
			
				//var ul = $(document.createElement('ul'));
			}
		});
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
		
		$('#div-left-bottom-tabs').tabs({
			beforeActivate: function(event, ui) {
				var li = ui.newTab[0];
				if (li.id == 'li-left-bottom-tabs-comments') {
					$('div-comments-timeline').html(''); // Empty the timeline
				}
			},
			activate: function(event, ui) {
				var li = ui.newTab[0];
				if (li.id == 'li-left-bottom-tabs-comments') {	
					var poi = getSelectedPoi();
					if (poi != null) {
						loadPoiComments(poi);
					}
				}
			}
		});

		$('#div-addCommentDialog').dialog({
			autoOpen: false,
			resizable: false,
			width: 400,
			height: 200,
			modal: true
		});
		
		$('#div-addCommentDialog img.profileImage')
			.attr('src', '${delegationUser.profileImageUrl}'); 
		$('#div-addCommentDialog div.displayName')
			.html('${delegationUser.displayName}');
		$('#div-addCommentDialog div.username')
			.html('@${delegationUser.uid}');
		
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
			minHeight: 200,
			resize: resizeTop,
			stop: function(event, ui) {			
			
				resizeHeights();
			} 
		});

		$('#div-left-top-tabs').tabs('option', 'disabled', [1, 2, 3]);
		// $('#div-left-top-tabs').tabs('option', 'disabled', [2]);

		// $('#div-left-bottom-tabs').tabs('option', 'disabled', [1]);

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
		
		<c:if test="${not empty selectedPoiId}"> <!-- used in devel tests -->
		selectPoi(getPoiById(${selectedPoiId}));
		</c:if>
		<c:if test="${fn:contains(mode, 'comments')}"> <!-- used in devel tests -->
		$('#div-left-bottom-tabs').tabs('option', 'active', 1);
		</c:if>
		<c:if test="${fn:contains(mode, 'addComment')}"> <!-- used in devel tests -->
		openAddCommentDialog();
		</c:if>
	}
	
	//google.maps.event.addDomListener(window, 'load', initialize);
	$(document).ready(initialize);
	
	$(window).resize(resizeHeights);
	
</script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-geocampus" class="entered">

<jsp:include page="div-entered.h.jsp"/>

<div> <!-- Not <div class="body"> -->
<form>

<div id="div-left">

<div id="div-left-top">

	<div id="div-left-top-tabs" class="pois">
	<ul>
		<li id="li-left-top-tabs-all">
			<a href="#div-left-top-tabs-all">Tous</a>
    	<li id="li-left-top-tabs-university">
    		<a href="#div-left-top-tabs-university">Université</a>
	    <li id="li-left-top-tabs-favorites">
	    	<a href="#div-left-top-tabs-favorites">Favoris</a>
	    <li id="li-left-top-tabs-search">
	    	<a href="#div-left-top-tabs-search">Recherche</a>
	</ul>
	
	<div id="div-left-top-tabs-all">
	
	<!--
	<h1 title="Version ${buildInfo.appVersion}
	Build ${buildInfo.buildDisplayName}
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
	
	</div> <!-- end of #div-left-top-tabs-all -->

	<div id="div-left-top-tabs-favorites">
	
		Favoris
		
	</div> <!-- end of #div-left-top-tabs-favorites -->

	<div id="div-left-top-tabs-university">
	
		Université
		
	</div> <!-- end of #div-left-top-tabs-university -->

	<div id="div-left-top-tabs-search">
	
		Recherche
		
	</div> <!-- end of #div-left-top-tabs-search -->
	
	</div> <!-- end of #div-left-top-tabs -->
	
</div> <!-- end of #div-left-top -->

<div id="div-left-bottom">

	<div id="div-left-bottom-tabs">
	<ul>
		<li id="li-left-bottom-tabs-details">
			<a href="#div-left-bottom-tabs-details">Détails</a>
	    <li id="li-left-bottom-tabs-comments">
	    	<a href="#div-left-bottom-tabs-comments">Commentaires</a>
	</ul>
	
	<div id="div-left-bottom-tabs-details">

	<div class="noPoiSelected">
		Sélectionnez un POI dans la liste ou sur la carte
	</div>

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
	
	</div> <!-- end of #div-left-bottom-tabs-details -->
	
	<div id="div-left-bottom-tabs-comments">

	<div class="noPoiSelected">
		Sélectionnez un POI dans la liste ou sur la carte
	</div>

	<div id="div-comments" class="hidden">

	<div class="field name"></div>
	
	<div id="div-comments-timeline"></div>
	
	<div id="div-comments-buttons">
	
	<button id="button-addComment" onclick="openAddCommentDialog(); return false;">
		Ajouter un commentaire…
	</button>
	
	</div> <!-- end of #div-comments-buttons -->
	
	</div> <!-- end of #div-comments -->

	</div> <!-- end of #div-left-bottom-tabs-comments -->

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

<div id="div-addCommentDialog" title="Ajouter un commentaire" class="hidden">
<img class="profileImage">
<div class="displayName"></div>
<div class="username"></div>
<div class="message">
	<textarea id="text-message" name="message"></textarea>
</div>
<div class="timestamp">
	<!-- TODO: Request backend, reserve a new commentId, display server time -->
</div>
<div id="div-addCommentDialog-buttons">
	<button id="button-cancel"
			onclick="closeAddCommentDialog(); return false;">
		Annuler
	</button>
	<button id="button-postComment"
			onclick="postComment(); closeAddCommentDialog(); return false;">
		Envoyer le commentaire
	</button>
</div> <!-- end of #div-addCommentDialog-buttons -->
</div> <!-- end of #div-addCommentDialog -->

</body>
</html>