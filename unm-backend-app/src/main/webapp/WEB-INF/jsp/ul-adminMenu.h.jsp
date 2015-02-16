<%@ page pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul id="ul-adminMenu">
	<li id="li-adminMenu-home" class="top">
		<a id="link-adminMenu-home" class="top" href="${baseURL}/">Accueil</a>
	<c:if test="${delegationUser.role != 'librarian'}">	
	<li class="top">
		<a id="link-adminMenu-data" class="top" href="${baseURL}/data/">Données<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-data">
		<li><a id="link-adminMenu-search" href="${baseURL}/search/">Recherche avancée</a>
		<li><a id="link-adminMenu-pois" href="${baseURL}/pois/">Points of Interest (POIs)</a>
		<li><a id="link-adminMenu-comments" href="${baseURL}/comments/">Commentaires</a>
		<li><a id="link-adminMenu-regions" href="${baseURL}/regions/">Régions, universités</a>
			<ul id="ul-adminMenu-data-regions">
			<li><a id="link-adminMenu-regions-bretagne" href="${baseURL}/regions/bretagne/">Bretagne — universités</a>
			<li><a id="link-adminMenu-regions-ile_de_france" href="${baseURL}/regions/ile_de_france/">Île de France — universités</a>
			<li><a id="link-adminMenu-regions-unrpcl" href="${baseURL}/regions/unrpcl/">Limousin/Poitou-Charentes — universités</a>
			</ul>
		<li><a id="link-adminMenu-geocampus" href="${baseURL}/geocampus/">Géocampus</a>
		<li><a id="link-adminMenu-news" href="${baseURL}/news/">Fils d’actualités des universités</a>
		<li><a id="link-adminMenu-users" href="${baseURL}/users/">Utilisateurs</a>
		
		<li><a id="link-adminMenu-categories" href="${baseURL}/poicategories">Catégories</a>
		<li><a id="link-adminMenu-universities" href="${baseURL}/api/universities/manage/">Universités</a>
		<li><a id="link-adminMenu-notifications" href="${baseURL}/api/app#/notifications">Notifications</a>
		<li><a id="link-adminMenu-menues" href="${baseURL}/api/app#/menus">Menus</a>

		</ul>
	<li class="top">
		<a id="link-adminMenu-system" class="top" href="${baseURL}/system/">Système<span
			class="top ui-menu-icon ui-icon ui-icon-carat-1-s"></span></a>
		<ul id="ul-adminMenu-system">
		<li><a id="link-adminMenu-history" href="${baseURL}/history/">Historique des actions</a>
		<li><a id="link-adminMenu-stats" href="${baseURL}/stats/">Statistiques</a>
		<li><a id="link-adminMenu-monitoring" href="${baseURL}/monitoring/">Monitoring</a>
		<li><a id="link-adminMenu-logs" href="${baseURL}/logs/">Logs techniques</a>
		<li><a id="link-adminMenu-backups" href="${baseURL}/backups/">Sauvegardes (backups)</a>
		</ul>
	</c:if>
	<li id="li-adminMenu-help" class="top">
		<a id="link-adminMenu-help" class="top" href="${baseURL}/help/">Aide</a>
</ul>
