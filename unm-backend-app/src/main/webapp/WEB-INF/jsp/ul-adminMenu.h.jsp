<%@ page pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul id="ul-adminMenu">
	<li id="li-adminMenu-home" class="top">
		<a id="link-adminMenu-home" class="top" href="${baseURL}/">Accueil</a>
	</li>
	<c:if test="${delegationUser.role != 'librarian'}">	
	<li class="top">
		<a id="link-adminMenu-data" class="top" href="${baseURL}/api/app#/main">Données<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-data">
		<li><a id="link-adminMenu-search" href="${baseURL}/api/app#/main">Recherche avancée</a>
		<li><a id="link-adminMenu-pois" href="${baseURL}/pois/">Points of Interest (POIs)</a>
		<li><a id="link-adminMenu-comments" href="${baseURL}/api/app#/manage/comments">Commentaires</a>
		<li><a id="link-adminMenu-regions" href="${baseURL}/regions/">Régions, universités</a>
		<li><a id="link-adminMenu-geocampus" href="${baseURL}/geocampus/admin/">Géocampus</a>
		<li><a id="link-adminMenu-news" href="${baseURL}/api/app#/feeds">Fils d’actualités des universités</a>
		<li><a id="link-adminMenu-users" href="${baseURL}/api/app#/manage/users">Utilisateurs</a>
		<li><a id="link-adminMenu-categories" href="${baseURL}/poicategories">Catégories</a>
		<li><a id="link-adminMenu-universities" href="${baseURL}/api/universities/manage/">Universités</a>
		<li><a id="link-universities" href="${baseURL}/api/app#/links">Mediathèques</a>
		<li><a id="link-universities" href="${baseURL}/api/app#/university-libraries">Bibliothèques</a>
		<li><a id="link-adminMenu-notifications" href="${baseURL}/api/app#/notifications">Notifications</a>
		<li><a id="link-adminMenu-menues" href="${baseURL}/api/app#/menus">Menus</a>

		</ul>
	</li>
	<li class="top">
		<a id="link-adminMenu-system" class="top" href="${baseURL}/api/admin/stats/">Système<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-system">
		<li><a id="link-adminMenu-stats" href="${baseURL}/api/admin/stats/">Statistiques</a>
		<li><a id="link-adminMenu-logs" href="${baseURL}/logs/">Logs techniques</a>
		</ul>
	</li>
	</c:if>
	<li id="li-adminMenu-help" class="top">
		<a id="link-adminMenu-help" class="top" href="${baseURL}/help/">Aide</a>
		</li>
</ul>
