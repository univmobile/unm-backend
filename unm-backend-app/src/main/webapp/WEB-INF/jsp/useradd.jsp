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
<body id="body-useradd" class="entered">
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
<form action="${baseURL}/useradd" method="POST">

<h1>Administration d’UnivMobile</h1>

<h2>Ajout d’un utilisateur</h2>

<table>
<tbody>
<tr>
	<th>uid</th>
	<td>
	<input type="text" id="text-uid" name="uid" value="${useradd.uid}">
	</td>
</tr>
<tr>
	<th>Civilité</th>
	<td>
	<select id="select-supannCivilite">
		<option value="Mme">Mᵐᵉ</option>
		<option value="M.">M.</option>
	</select>
	</td>
</tr>
<tr>
	<th>Nom complet</th>
	<td>
	<input type="text" id="text-displayName" name="displayName" value="${useradd.displayName}">
	</td>
</tr>
<tr>
	<th>E-mail</th>
	<td>
	<input type="text" id="text-email" name="email" value="${useradd.mail}">
	</td>
</tr>
</tbody>
</table>

<div class="table bottom">
<!--
<a href="${baseURL}/">Annuler</a>
-->
<button id="button-cancel"
 onclick="document.location.href = '${baseURL}'; return false;">
	Annuler
</button>
<button id="button-save" onclick="submit()">
	Enregistrer
</button>
</div>

</form>
</div>
</body>
</html>