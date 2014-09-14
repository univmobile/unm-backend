<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Commentaires</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<style type="text/css">
#div-poi {
	border: 1px solid #ccc;
	xbackground-color: #ff0;
	padding: 0em 1em;
	xdisplay: table;
	margin: 1em 0 0;
}
#div-poi table {
	width: 100%;
}
#div-poi tr {
	display: inline;
}
#div-poi th,
#div-poi tr.id span#text-id {
	display: none;
}
#div-poi td {
	padding-right: 2em;
}
#div-poi tr.universityIds li {
	display: inline;
}
#table-comments {
	width: 100%;
	border-collapse: collapse;
}
#table-comments tr,
#div-noComments {
	border-top: 1px solid #ccc;
	border-bottom: 1px solid #ccc;
}
#div-noComments {
	padding: 8px;
	text-align: center;
}
#table-comments td {
	vertical-align: baseline;
}
#table-comments td.date {
	font-size: 12px;
	white-space: nowrap;
	padding-right: 1em;
	xtext-align: right;
}
#table-comments td.author {
	font-size: 12px;
	white-space: nowrap;
}
#table-comments td.author .displayName {
	display: block;
	font-weight: bold;
}
#table-comments td.text {
	font-size: 12px;
}
#div-comments-buttons {
	margin-top: 2em;
	text-align: center;
}
#div-addComment div.author span.displayName {
	display: block;
	font-weight: bold;
}
#div-addComment div.author {
	font-size: 12px;
	float: left;
	color: #ccc;
}
#div-addComment {
	xborder-top: 1px solid #ccc;
	padding: 0.5em 2em;
	margin-bottom: 1em;
}
#div-addComment textarea {
	margin-left: 2em;
	margin-right: 1em;
	font-size: 12px;
	font-family: Arial, Helvetica, sans-serif;
	width: 450px;
	height: 4em;
}
</style>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-comments" class="admin entered">

<jsp:include page="div-entered.h.jsp"/>

<div class="body results poi">
<input type="hidden" name="poiId" value="${poi.id}">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<div id="div-comments">

<h2>
Commentaires
<!--
POI ${poi.id} : <c:out value="${poi.name}"/>
-->
</h2>

<div id="div-query">
<form action="${baseURL}/comments" method="GET">
<input id="text-query" name="query">
<button id="button-search">
	Rechercher
</button>
</form>
</div>

<div id="div-resultInfo">
<span>
	→
	<c:choose>
	<c:when test="${not empty commentsInfo.context}">
		<c:out value="${commentsInfo.context}"/>
		<c:if test="${not empty commentsInfo.resultCount}">
			(${commentsInfo.resultCount})
		</c:if>
	</c:when>
	<c:when test="${empty commentsInfo.resultCount || commentsInfo.resultCount == 0}">
		Aucun résultat
	</c:when>
	<c:when test="${commentsInfo.resultCount == 1}">
		Un résultat
	</c:when>
	<c:otherwise>
		${commentsInfo.resultCount} résultats
	</c:otherwise>
	</c:choose>
</span>
<button id="button-export" disabled>
	Export…
</button>
</div>

<div id="div-poi">
<table id="table-poi"> 
<tbody>
<tr class="name">
<th>
	Nom
</th>
<td>
	<a id="link-poi" href="${baseURL}/pois/${poi.id}">
	<c:out value="${poi.name}"/>
	</a>
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
	<span id="span-active-no"
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
</tbody>
</table>
</div>

<div id="div-addComment">
<form method="POST" action="${baseURL}/comments">
<div class="author">
<span class="displayName">${delegationUser.displayName}</span>
<span class="username">@${delegationUser.uid}</span>
</div>
<textarea id="text-message" name="message"></textarea>
<button id="button-send" disabled>
	Envoyer le commentaire
</button>
</form>
</div>

<c:choose>
<c:when test="${fn:length(comments) == 0}">
<div id="div-noComments" class="emptyArray">
	Aucun commentaire
</div>
</c:when>
<c:otherwise>
<table id="table-comments">
<tbody>
<c:forEach var="comment" items="${comments}">
<tr>
<td class="date">
	${comment.displayFullPostedAt}
</td>
<td class="author">
	<span class="displayName">
		${comment.authorDisplayName}
	</span>
	<span class="username">
		@${comment.authorUsername}
	</span>
</td>
<td class="text">
	${comment.text}
</td>
</tr>
</c:forEach>
</tbody>
</table> <!-- end of #table-comments -->
</c:otherwise>
</c:choose>

<div id="div-comments-buttons">
<button id="button-back"
		onclick="document.location.href = '${baseURL}/pois/${poi.id}'; return false">
	Retour au POI ${poi.id} : ${poi.name}
</button>
</div>

</div> <!-- end of #div-comments -->
	
</div> <!-- end of div.body -->

</body>
</html>