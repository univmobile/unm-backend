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
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-admin" class="admin entered">
<div id="div-entered">
<jsp:include page="ul-adminMenu.h.jsp"/>
<ul id="ul-adminUser">
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li id="li-logout"> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>

<div class="body">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<h2 class="homeMenu"><a id="link-data" href="${baseURL}/data/">Données</a></h2>

<div id="div-query">
<form action="${baseURL}/" method="GET">
<input id="text-query" name="q">
<button id="button-search">
	Rechercher
</button>
</form>
</div>

<ul class="homeMenu" id="ul-home-data">
	<li class="flaticon icon-search8">
		<a id="link-search" href="${baseURL}/search/">Recherche avancée</a>
	<li class="flaticon icon-location14">
		<a id="link-pois" href="${baseURL}/pois/">Points of Interest (POIs)</a>
	<li class="flaticon icon-chat2">
		<a id="link-comments" href="${baseURL}/comments/">Commentaires</a>
	<li class="flaticon icon-website1">
		<a id="link-regions" href="${baseURL}/regions/">Régions, universités</a>
	<li class="clear">
	<li class="flaticon icon-compass6">
		<a id="link-geocampus" href="${baseURL}/geocampus/admin/">Géocampus</a>
	<li class="flaticon icon-rss9">
		<a id="link-news" href="${baseURL}/news/">Fils d’actualités des universités</a>
	<li class="flaticon icon-address7">
		<a id="link-users" href="${baseURL}/users/">Utilisateurs</a>
	<li class="flaticon icon-address7">
		<a id="link-categories" href="${baseURL}/poicategories/">Catégories</a>
</ul>

<h2 class="homeMenu"><a id="link-system" href="${baseURL}/system/">Système</a></h2>

<ul class="homeMenu" id="ul-home-system">
	<li class="flaticon icon-file6">
		<a id="link-history" href="${baseURL}/history/">Historique<br>des actions</a>
	<li class="flaticon icon-graph5">
		<a id="link-stats" href="${baseURL}/stats/">Statistiques</a>
	<li class="flaticon icon-check6">
		<a id="link-monitoring" href="${baseURL}/monitoring/">Monitoring</a>
	<li class="flaticon icon-black56">
		<a id="link-logs" href="${baseURL}/logs/">Logs techniques</a>
	<li class="flaticon icon-down9">
		<a id="link-backups" href="${baseURL}/backups/">Sauvegardes (backups)</a>
</ul>

<h2 class="homeMenu">Général</h2>

<ul class="homeMenu" id="ul-home-others">
	<li class="flaticon icon-question5">
		<a id="link-help" href="${baseURL}/help/">Aide</a>
	<li class="flaticon icon-person9">
		<a id="link-profile" href="${baseURL}/profile/">Mon profil&#160;:
			${user.uid}
			<c:if test="${user.uid != delegationUser.uid}">
				(${delegationUser.uid})
			</c:if>
	</a>
	<li class="flaticon icon-go3">
		<a id="link-home-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>

</div> <!-- end of div.body -->
</body>
</html>