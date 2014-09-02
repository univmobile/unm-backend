<%@ page pageEncoding="UTF-8"%> 

<div id="div-entered">
<ul>
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>
