<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Utilisateurs</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<style type="text/css">
td.id,
td.name {
	cursor: pointer;
}
td.id:active a,
td.name:active a,
td a:active {
	color: #f00;
}
th.id,
td.id {
	display: none;
}
body.results td.roles {
	text-align: center;
	font-weight: bold;
	color: #ffc;
	width: 1em;
	padding-left: 0;
	padding-right: 0;
	background-color: #cc0;
	padding-top: 3px;
	padding-bottom: 1px;
}
body.results th.roles {
	padding-left: 0;
	padding-right: 0;
}
body.results td.roles.student {
	background-color: #090;
}
body.results td.roles.admin {
	background-color: #f60;
}
body.results td.roles.superadmin {
	background-color: #f00;
	xcolor: #f00;
}
</style>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-users" class="entered results">

<jsp:include page="div-entered.h.jsp"/>

<div class="body results">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<div id="div-users">

<h2>
Utilisateurs : ${usersInfo.count}
</h2>

<div id="div-query">
<form action="${baseURL}/users" method="GET">
<input id="text-query" name="q">
<button id="button-search">
	Rechercher
</button>
</form>
</div>

<div id="div-resultInfo">
<span>
	→
	<c:choose>
	<c:when test="${not empty usersInfo.context}">
		<c:out value="${usersInfo.context}"/>
		<c:if test="${not empty usersInfo.resultCount}">
			(${usersInfo.resultCount})
		</c:if>
	</c:when>
	<c:when test="${empty usersInfo.resultCount || usersInfo.resultCount == 0}">
		Aucun résultat
	</c:when>
	<c:when test="${usersInfo.resultCount == 1}">
		Un résultat
	</c:when>
	<c:otherwise>
		${usersInfo.resultCount} résultats
	</c:otherwise>
	</c:choose>
</span>
<button id="button-export" disabled>
	Export…
</button>
</div>

<table>
<thead>
<tr>
<th class="none"></th>
<th class="uid">uid</th>
<th class="roles"></th>
<th class="mail">mail</th>
<th class="edit"></th>
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
<td class="uid">
<a href="${baseURL}/users/${u.uid}">
${u.uid}
</a>
</td>
<c:choose>
<c:when test="${u.superadmin}">
	<td class="roles superadmin" title="${u.uid} : Super Administrateur">S</td>
</c:when>
<c:when test="${u.admin}">
	<td class="roles admin" title="${u.uid} : Administrateur">A</td>
</c:when>
<c:when test="${u.student}">
	<td class="roles student" title="${u.uid} : Étudiant">É</td>
</c:when>
<c:otherwise>
	<td class="roles unknown" title="(${u.uid} : Type inconnu)">-</td>
</c:otherwise>
</c:choose>
<td class="mail">
${u.mail}
</td>
<td class="edit">
<c:choose>
<c:when test="${u.editable}">
	<a id="link-edit_${u.uid}" href="${baseURL}/users/${u.uid}?edit">Modifier…</a>
</c:when>
<c:otherwise>
	<!--
	<div class="disabled">Modifier…</a>
	-->
</c:otherwise>
</c:choose>
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

</div> <!-- end of #div-users -->
	
</div> <!-- end of div.body -->

</body>
</html>