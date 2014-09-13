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
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<script type="text/javascript">

	function initialize() {
	
		$('#ul-adminMenu-data').menu();
		$('#ul-adminMenu-system').menu();
		
		$('#ul-adminMenu li.top').bind('mouseover', function() {
			$(this).find('ul').css('visibility', 'visible');
		}).bind('mouseout', function() {
			$(this).find('ul').css('visibility', 'hidden');
		});
		
		$('#ul-adminMenu-data-regions').bind('mouseout', function() {
			$(this).css('visibility', 'hidden');
		});
	}
	
	$(document).ready(initialize);

</script>
</head>
<body id="body-entered" class="entered">
<div id="div-entered">
<ul id="ul-adminMenu">
	<li id="li-adminMenu-home" class="top">
		<a id="link-adminMenu-home" class="top" href="${baseURL}/admin/">Accueil</a>
	</li>
	<li class="top">
		<a id="link-adminMenu-data" class="top" href="${baseURL}/data/">Données<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-data">
		<li><a id="link-adminMenu-search" href="${baseURL}/data/search/">Recherche avancée</a><li>
		<li><a id="link-adminMenu-pois" href="${baseURL}/data/pois/">Points of Interest (POIs)</a><li>
		<li><a id="link-adminMenu-comments" href="${baseURL}/data/comments/">Commentaires</a><li>
		<li><a id="link-adminMenu-regions" href="${baseURL}/data/regions/">Régions — universités</a>
			<ul id="ul-adminMenu-data-regions">
			<li><a id="link-adminMenu-regions-bretagne" href="${baseURL}/data/regions/bretagne/">Bretagne — universités</a></li>
			<li><a id="link-adminMenu-regions-ile_de_france" href="${baseURL}/data/regions/ile_de_france/">Île de France — universités</a></li>
			<li><a id="link-adminMenu-regions-unrpcl" href="${baseURL}/data/regions/unrpcl/">Limousin/Poitou-Charentes — universités</a></li>
			</ul>
		<li>
		<li><a id="link-adminMenu-geocampus" href="${baseURL}/geocampus/">Géocampus</a><li>
		<li><a id="link-adminMenu-news" href="${baseURL}/data/news/">Flux d’actualités des universités</a><li>
		<li><a id="link-adminMenu-users" href="${baseURL}/data/users/">Utilisateurs</a><li>
		</ul>
	</li>
	<li class="top">
		<a id="link-adminMenu-system" class="top" href="${baseURL}/system/">Système<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-system">
		<li><a id="link-adminMenu-logqueue" href="${baseURL}/system/logqueue/">Historique des actions</a><li>
		<li><a id="link-adminMenu-stats" href="${baseURL}/system/stats/">Statistiques</a><li>
		<li><a id="link-adminMenu-monitoring" href="${baseURL}/system/monitoring/">Monitoring</a></li>
		<li><a id="link-adminMenu-logs" href="${baseURL}/system/logs/">Logs techniques</a><li>
		<li><a id="link-adminMenu-backups" href="${baseURL}/system/backups/">Sauvegardes (backups)</a><li>
		</ul>
	</li>
</ul>
<ul id="ul-adminUser">
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li id="li-logout"> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>

<div class="body">
<form action="${baseURL}/" method="POST">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<h2>Utilisateurs : ${fn:length(users)}</h2>

<table>
<thead>
<tr>
<th class="none"></th>
<th>uid</th>
<th>mail</th>
<th></th>
<th class="none"></th>
</tr>
</thead>
<tbody>
<c:forEach var="u" items="${users}">
<tr>
<td class="none">
<c:choose>
<c:when test="${user.uid == u.uid}">
	<div class="principal
		<c:if test="${delegationUser.uid == u.uid}">delegation</c:if>
		" title="Principal : ${user.uid}">1</div>
</c:when>
<c:when test="${delegationUser.uid == u.uid}">
	<div class="delegation" title="Délégation : ${delegationUser.uid}">2</div>
</c:when>
</c:choose>
</td>
<td>
${u.uid}
</td>
<td>
${u.mail}
</td>
<td class="edit">
<!--
<a id="link-edit_xxx" href="${baseURL}?user=${u.uid}&amp;edit">Modifier…</a>
-->
<div class="disabled">Modifier…</a>
</td>
<td class="none">
</td>
</tr>
</c:forEach>
</tbody>
</table>

<div class="table bottom">
<a id="link-useradd" href="${baseURL}/useradd">Ajouter un utilisateur…</a>
</div>

<div id="div-regions">

<h2>Régions : ${fn:length(regions)}</h2>

<table>
<thead>
<tr>
<th class="none"></th>
<th class="uid">id</th>
<th class="label">label</th>
<th class="universityCount">universités</th>
<th class="none"></th>
</tr>
</thead>
<tbody>
<c:forEach var="r" items="${regions}">
<tr>
<td class="none">
</td>
<td class="uid">
${r.uid}
</td>
<td class="label">
<input type="text" id="text-region_${r.uid}" name="region_${r.uid}"
	value="${r.label}"/>
</td>
<td class="universityCount">
${r.universityCount}
</td>
<td class="none">
</td>
</tr>
</c:forEach>
</tbody>
</table>

<div class="table bottom">
<button id="button-cancel"
 onclick="document.location.href = '${baseURL}'; return false;">
	Annuler
</button>
<button id="button-submit" onclick="submit()">
	Enregistrer
</button>
</div>

<div class="table bottom">
JSON :
<a id="link-json" href="${baseURL}/json/regions">
${baseURL}/json/regions
</a>
</div>

</div> <!-- end of #div-regions -->

<div id="div-pois">

<h2>
<a href="${baseURL}/geocampus" id="link-geocampus">Géocampus</a>
</h2>

<hr>

<h2>
<a href="${baseURL}/pois" id="link-pois">POIs : ${poisInfo.count}</a>
</h2>

<table>
<tbody>
<c:forEach var="r" items="${poisInfo.regions}">
<tr>
<th class="region" colspan="2">
	Région : ${r.label}
</th>	
<th class="poiCount">POIs</th>
</tr>
<c:forEach var="u" items="${r.universities}">
<tr>
<td class="id">
${u.id}
</td>
<td class="title">
${u.title}
</td>
<td class="poiCount">
<a href="${baseURL}/pois/?univ=${u.id}">
${u.poiCount}
</a>
</td>
</tr>
</c:forEach>
</c:forEach>
</tbody>
</table>

</div> <!-- end of #div-pois -->

</form>
</div> <!-- end of div.body -->
</body>
</html>