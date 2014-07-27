<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Administration d’UnivMobile</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/styles.css">
</head>
<body id="body-entered" class="entered">
<div id="div-entered">
<ul>
<li> Principal : ${user.uid}
<c:if test="${user.uid != delegationUser.uid}">
<li> Délégation : ${delegationUser.uid}
</c:if>
<li> <a href="${baseURL}/?logout">Déconnexion</a>
</ul>
</div>

<div class="body">

<h1>Administration d’UnivMobile</h1>

<h2>Utilisateurs : ${fn:length(users)}</h2>

<table>
<thead>
<tr>
<th class="none">
<th>uid</th>
<th>mail</th>
<th></th>
<th class="none">
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
<a href="${baseURL}?user=${u.uid}&amp;edit">Modifier…</a>
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
<a href="${baseURL}/useradd">Ajouter un utilisateur…</a>
</div>

</div>
</body>
</html>