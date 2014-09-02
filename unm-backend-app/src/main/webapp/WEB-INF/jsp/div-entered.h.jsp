<%@ page pageEncoding="UTF-8"%> 

<div id="div-entered">
<ul>
<li title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
<a href="${baseURL}/">
 UnivMobile : administration
</a>
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>
