<%@ page pageEncoding="UTF-8"%> 

<div id="div-entered">
<jsp:include page="ul-adminMenu.h.jsp"/>
<ul id="ul-adminUser">
<!--
<li title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
<a href="${baseURL}/">
 UnivMobile : administration
</a>
-->
<li> Principal : <a href="${baseUrl}/unm-backend/usermodify/${user.id}">${user.username}</a>
<c:if test="${user.id != delegationUser.id}">
<li> Délégation :  <a href="${baseUrl}/unm-backend/usermodify/${delegationUser.id}">${delegationUser.username}</a>
</c:if>
<li id="li-logout"> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>
