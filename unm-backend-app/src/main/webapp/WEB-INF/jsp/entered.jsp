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
</head>
<body id="body-entered" class="entered">
<div id="div-entered">
<ul>
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li> <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>

<div class="body">
<form action="${baseURL}/" method="POST">

<h1 title="Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<h2>Utilisateurs : ${fn:length(users)}</h2>

<table>
<thead>
<tr>
<th class="none"></th>
<th>uid</th>
<th>mail</th>
<th></th>
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
<td>
${u.uid}
</td>
<td>
${u.mail}
</td>
<td class="edit">
<!--
<a id="link-edit_xxx" href="${baseURL}?user=${u.uid}&amp;edit">Modifier…</a>
-->
<div class="disabled">Modifier…</a>
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

<div id="div-regions">

<h2>Régions : ${fn:length(regions)}</h2>

<table>
<thead>
<tr>
<th class="none"></th>
<th class="uid">id</th>
<th class="label">label</th>
<th class="universityCount">universités</th>
<th class="none"></th>
</tr>
</thead>
<tbody>
<c:forEach var="r" items="${regions}">
<tr>
<td class="none">
</td>
<td class="uid">
${r.uid}
</td>
<td class="label">
<input type="text" id="text-region_${r.uid}" name="region_${r.uid}"
	value="${r.label}"/>
</td>
<td class="universityCount">
${r.universityCount}
</td>
<td class="none">
</td>
</tr>
</c:forEach>
</tbody>
</table>

<div class="table bottom">
<button id="button-cancel"
 onclick="document.location.href = '${baseURL}'; return false;">
	Annuler
</button>
<button id="button-submit" onclick="submit()">
	Enregistrer
</button>
</div>

<div class="table bottom">
JSON :
<a id="link-json" href="https://univmobile-dev.univ-paris1.fr/json/regions">
https://univmobile-dev.univ-paris1.fr/json/regions
</a>
</div>

</div> <!-- end of #div-regions -->

<div id="div-pois">

<h2>
<a href="${baseURL}/pois" id="link-pois">POIs : ${pois.count}</a>
</h2>

<table>
<tbody>
<c:forEach var="r" items="${pois.regions}">
<tr>
<th class="region" colspan="2">
	Région : ${r.label}
</th>	
<th class="poiCount">POIs</th>
</tr>
<c:forEach var="u" items="${r.universities}">
<tr>
<td class="id">
${u.id}
</td>
<td class="title">
${u.title}
</td>
<td class="poiCount">
<a href="${baseURL}/pois/?univ=${u.id}">
${u.poiCount}
</a>
</td>
</tr>
</c:forEach>
</c:forEach>
</tbody>
</table>

</div> <!-- end of #div-pois -->

</form>
</div> <!-- end of div.body -->
</body>
</html>