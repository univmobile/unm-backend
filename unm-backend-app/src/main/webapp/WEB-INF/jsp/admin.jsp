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
<li> Principal : <a href="${baseUrl}/unm-backend/usermodify/${user.id}">${user.username}</a>
<c:if test="${user.id != delegationUser.id}">
<li> Délégation : <a href="${baseUrl}/unm-backend/usermodify/${delegationUser.id}">${delegationUser.username}</a>
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

<ul class="homeMenu" id="ul-home-data">
<c:if test="${delegationUser.role != 'librarian'}">
	<li class="flaticon icon-search8">
		<a id="link-search" href="${baseURL}/api/app#/main">Recherche avancée</a>
	<li class="flaticon icon-location14">
		<a id="link-pois" href="${baseURL}/pois/">Points of Interest (POIs)</a>
	<li class="flaticon icon-chat2">
		<a id="link-comments" href="${baseURL}/api/app#/manage/comments">Commentaires</a>
	<li class="flaticon icon-address7">
		<a id="link-categories" href="${baseURL}/poicategories">Catégories</a>	
	<li class="flaticon icon-globe5">
		<a id="link-regions" href="${baseURL}/regions/">Régions</a>
	<li class="clear">
	<li class="flaticon icon-compass6">
		<a id="link-geocampus" href="${baseURL}/geocampus/admin/">Géocampus</a>
	<li class="flaticon icon-rss9">
		<a id="link-news" href="${baseURL}/api/app#/feeds">Fils d’actualités des universités</a>
	<li class="flaticon icon-address7">
		<a id="link-users" href="${baseURL}/api/app#/manage/users">Utilisateurs</a>
	<li class="flaticon icon-calendar7">
		<a id="link-universities" href="${baseURL}/api/app#/notifications">Notifications</a>
	</li>
	<li class="flaticon icon-website1">
		<a id="link-universities" href="${baseURL}/api/app#/menus">Menus</a>
	</li>
	<li class="clear">
	<li class="flaticon icon-globe6">
		<a id="link-universities" href="${baseURL}/api/universities/manage/">Universités</a>
	</li>
	<li class="flaticon icon-film63">
		<a id="link-universities" href="${baseURL}/api/app#/links">Mediathèques</a>
	</li>
	<li class="flaticon icon-center13">
		<a id="link-universities" href="${baseURL}/api/app#/university-libraries">Bibliothèques</a>
	</li>
</c:if>
<c:if test="${delegationUser.role == 'librarian'}">
	<li class="flaticon icon-compass6">
		<a id="link-geocampus" href="${baseURL}/geocampus/admin/">Géocampus</a>
	</li>
</c:if>		
</ul>

<c:if test="${delegationUser.role != 'librarian'}">
<h2 class="homeMenu"><a id="link-system" href="${baseURL}/system/">Système</a></h2>

<ul class="homeMenu" id="ul-home-system">
	<li class="flaticon icon-graph5">
		<a id="link-stats" href="${baseURL}/api/admin/stats/">Statistiques</a>
	<li class="flaticon icon-black56">
		<a id="link-logs" href="${baseURL}/logs/">Logs techniques</a>
</ul>
</c:if>

<h2 class="homeMenu">Général</h2>

<ul class="homeMenu" id="ul-home-others">
	<li class="flaticon icon-question5">
		<a id="link-help" href="${baseURL}/help/">Aide</a>
	<li class="flaticon icon-person9">
		<a id="link-profile" href="${baseURL}/usermodify/${user.id}">Mon profil</a>
	<li class="flaticon icon-go3">
		<a id="link-home-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>

</div> <!-- end of div.body -->
</body>
</html>