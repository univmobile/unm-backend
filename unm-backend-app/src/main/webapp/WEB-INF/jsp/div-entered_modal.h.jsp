<%@ page pageEncoding="UTF-8"%> 

<div id="div-entered">
<ul id="ul-adminMenu">
	<li id="li-adminMenu-home" class="top disabled">Accueil
	<li class="top disabled">Données
	<li class="top disabled">Système
	<li id="li-adminMenu-help" class="top disabled">Aide
</ul>
<ul id="ul-adminUser">
<li> Principal :  <a href="${baseUrl}/unm-backend/usermodify/${user.id}">${user.username}</a>
<c:if test="${user.id != delegationUser.id}">
<li> Délégation :  <a href="${baseUrl}/unm-backend/usermodify/${delegationUser.id}">${delegationUser.username}</a>
</c:if>
<li id="li-logout"> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>
