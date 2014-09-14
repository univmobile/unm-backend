<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — POI : <c:out
	value="${poi.name}"/></title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<style type="text/css">
input.text {
	width: 30em;
}
textarea {
	width: 30em;
	height: 4em;
}
input.text,
textarea {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
}
#div-poi-buttons {
	margin-top: 2em;
	text-align: center;
}
#button-cancel {
	margin-right: 2em;
}
</style>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-poi" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body poi">
<form method="POST" action="${baseURL}/pois">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<div id="div-poi">

<h2>
POI : <c:out value="${poi.name}"/>
</h2>

<table id="table-poi">
<tbody>
<tr class="name">
<th>
	Nom
</th>
<td>
	<input class="text" id="text-name" name="name" disabled
		value="<c:out value="${poi.name}"/>">
</td>
</tr>
<tr class="id">
<th>
	<!--
	ID
	-->
</th>
<td>
	<span id="text-id">POI ${poi.id}</span>
	<input type="hidden" name="id" value="${poi.id}">
	<span id="span-active-yes" class="selected">
	<label for="radio-active-yes">Actif</label>
	<input type="radio" name="active" value="yes" checked
		id="radio-active-yes" disabled>
	</span>
	<span id="span-active-no">
	<label for="radio-active-no">Inactif</label>
	<input type="radio" name="active" value="no"
		id="radio-active-no" disabled>
	</span>
</td>
</tr>
<tr class="universityIds">
<th>
	Universités
</th>
<td>
	<ul>
	<c:forEach var="universityId" items="${poi.universityIds}">
	<li> ${universityId}
	</c:forEach>
	</ul>
</td>
</tr>
<tr class="textarea floor">
<th>
	Emplacement
</th>
<td>
	<textarea id="text-floor" name="floor" disabled><c:out
		value="${poi.floor}"/></textarea>
</td>
</tr>
<tr class="textarea openingHours">
<th>
	Horaires
</th>
<td>
	<textarea id="text-floor" name="floor" disabled><c:out
		value="${poi.floor}"/></textarea>
</td>
</tr>
<tr class="phone">
<th>
	Téléphone
</th>
<td>
	<input class="text" id="text-phone" name="phone" disabled
		value="<c:out value="${poi.phone}"/>">
</td>
</tr>
<tr class="textarea address">
<th>
	Adresse
</th>
<td>
	<textarea id="text-address" name="address" disabled><c:out
		value="${poi.address}"/></textarea>
</td>
</tr>
<tr class="email">
<th>
	E-mail
</th>
<td>
	<input class="text" id="text-email" name="email" disabled
		value="<c:out value="${poi.email}"/>">
</td>
</tr>
<tr class="textarea itinerary">
<th>
	Accès
</th>
<td>
	<textarea id="text-itinerary" name="itinerary" disabled><c:out
		value="${poi.itinerary}"/></textarea>
</td>
</tr>
<tr class="url">
<th>
	Site web
</th>
<td>
	<input class="text" id="text-url" name="url" disabled
		value="<c:out value="${poi.url}"/>">
</td>
</tr>
<tr class="coordinates">
<th>
	Coordonnées Lat/Lng
</th>
<td>
	<input class="text" id="text-coordinates" name="coordinates" disabled
		value="<c:out value="${poi.coordinates}"/>">
</td>
</tr>
<tr class="comments">
<th>
	Commentaires
</th>
<td>
	<a href="${baseURL}/comments/poi${poi.id}" id="link-comments">
		<c:choose>
		<c:when test="${empty commentCount or commentCount == 0}">
			aucun commentaire
		</c:when>
		<c:when test="${commentCount == 1}">
			un commentaire
		</c:when>
		<c:otherwise>
			${commentCount} commentaires
		</c:otherwise>
		</c:choose>
	</a>
</td>
</tr>
</tbody>
</table>

<div id="div-poi-buttons">
<button id="button-cancel"
		onclick="document.location.href = '${baseURL}/pois'; return false">
	Annuler
</button>
<button id="button-save" disabled>
	Enregistrer
</button>
</div>

</div> <!-- end of #div-poi -->
	
</div> <!-- end of div.body -->

</body>
</html>