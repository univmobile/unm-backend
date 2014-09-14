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
<body id="body-admin" class="entered">
<div id="div-entered">
<ul id="ul-adminMenu">
	<li id="li-adminMenu-home" class="top">
		<a id="link-adminMenu-home" class="top" href="${baseURL}/admin/">Accueil</a>
	<li class="top">
		<a id="link-adminMenu-data" class="top" href="${baseURL}/data/">Données<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-data">
		<li><a id="link-adminMenu-search" href="${baseURL}/data/search/">Recherche avancée</a>
		<li><a id="link-adminMenu-pois" href="${baseURL}/data/pois/">Points of Interest (POIs)</a>
		<li><a id="link-adminMenu-comments" href="${baseURL}/data/comments/">Commentaires</a>
		<li><a id="link-adminMenu-regions" href="${baseURL}/data/regions/">Régions, universités</a>
			<ul id="ul-adminMenu-data-regions">
			<li><a id="link-adminMenu-regions-bretagne" href="${baseURL}/data/regions/bretagne/">Bretagne — universités</a>
			<li><a id="link-adminMenu-regions-ile_de_france" href="${baseURL}/data/regions/ile_de_france/">Île de France — universités</a>
			<li><a id="link-adminMenu-regions-unrpcl" href="${baseURL}/data/regions/unrpcl/">Limousin/Poitou-Charentes — universités</a>
			</ul>
		<li><a id="link-adminMenu-geocampus" href="${baseURL}/geocampus/">Géocampus</a>
		<li><a id="link-adminMenu-news" href="${baseURL}/data/news/">Fils d’actualités des universités</a>
		<li><a id="link-adminMenu-users" href="${baseURL}/data/users/">Utilisateurs</a>
		</ul>
	<li class="top">
		<a id="link-adminMenu-system" class="top" href="${baseURL}/system/">Système<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-system">
		<li><a id="link-adminMenu-logqueue" href="${baseURL}/system/logqueue/">Historique des actions</a>
		<li><a id="link-adminMenu-stats" href="${baseURL}/system/stats/">Statistiques</a>
		<li><a id="link-adminMenu-monitoring" href="${baseURL}/system/monitoring/">Monitoring</a>
		<li><a id="link-adminMenu-logs" href="${baseURL}/system/logs/">Logs techniques</a>
		<li><a id="link-adminMenu-backups" href="${baseURL}/system/backups/">Sauvegardes (backups)</a>
		</ul>
	<li id="li-adminMenu-help" class="top">
		<a id="link-adminMenu-help" class="top" href="${baseURL}/help/">Aide</a>
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
<form method="GET" action="${baseURL}/admin/">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<h2 class="homeMenu"><a id="link-home-data" href="${baseURL}/data/">Données</a></h2>

<div id="div-search">
<input id="text-query" name="q">
<button id="button-search">
	Rechercher
</button>
</div>

<ul class="homeMenu" id="ul-home-data">
	<li class="flaticon icon-search8">
		<a id="link-home-search" href="${baseURL}/data/search/">Recherche avancée</a>
	<li class="flaticon icon-location14">
		<a id="link-home-pois" href="${baseURL}/data/pois/">Points of Interest (POIs)</a>
	<li class="flaticon icon-chat2">
		<a id="link-home-comments" href="${baseURL}/data/comments/">Commentaires</a>
	<li class="flaticon icon-website1">
		<a id="link-home-regions" href="${baseURL}/data/regions/">Régions, universités</a>
	<li class="clear">
	<li class="flaticon icon-compass6">
		<a id="link-home-geocampus" href="${baseURL}/geocampus/">Géocampus</a>
	<li class="flaticon icon-rss9">
		<a id="link-home-news" href="${baseURL}/data/news/">Fils d’actualités des universités</a>
	<li class="flaticon icon-address7">
		<a id="link-home-users" href="${baseURL}/data/users/">Utilisateurs</a>
</ul>

<h2 class="homeMenu"><a id="link-home-system" href="${baseURL}/system/">Système</a></h2>

<ul class="homeMenu" id="ul-home-system">
	<li class="flaticon icon-file6">
		<a id="link-home-logqueue" href="${baseURL}/system/logqueue/">Historique<br>des actions</a>
	<li class="flaticon icon-graph5">
		<a id="link-home-stats" href="${baseURL}/system/stats/">Statistiques</a>
	<li class="flaticon icon-check6">
		<a id="link-home-monitoring" href="${baseURL}/system/monitoring/">Monitoring</a>
	<li class="flaticon icon-black56">
		<a id="link-home-logs" href="${baseURL}/system/logs/">Logs techniques</a>
	<li class="flaticon icon-down9">
		<a id="link-home-backups" href="${baseURL}/system/backups/">Sauvegardes (backups)</a>
</ul>

<h2 class="homeMenu">Autres</h2>

<ul class="homeMenu" id="ul-home-others">
	<li class="flaticon icon-question5">
		<a id="link-home-help" href="${baseURL}/help/">Aide</a>
	<li class="flaticon icon-person9">
		<a id="link-home-profile" href="${baseURL}/profile/">Mon profil&#160;:
			${user.uid}
			<c:if test="${user.uid != delegationUser.uid}">
				(${delegationUser.uid})
			</c:if>
	</a>
	<li class="flaticon icon-go3">
		<a id="link-home-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>

</form>
</div> <!-- end of div.body -->
</body>
</html>