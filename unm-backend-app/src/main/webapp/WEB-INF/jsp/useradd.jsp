<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Ajouter un utilisateur</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-useradd" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body">
<form action="${baseURL}/useradd" method="POST">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<div class="div-useradd">

<c:if test="${err_duplicateUid}">
<div class="error">
	ERREUR — un utilisateur avec cet uid = ${useradd.uid} existe déjà en base
</div>
</c:if>
<c:if test="${err_duplicateRemoteUser}">
<div class="error">
	ERREUR — un utilisateur avec ce REMOTE_USER = ${useradd.remoteUser} 
	existe déjà en base
</div>
</c:if>

<h2>Ajout d’un utilisateur</h2>

<table>
<tbody>
<tr>
	<th title="L’identifiant interne à UnivMobile pour le compte utilisateur">
		uid
	</th>
	<td>
	<input class="text" type="text" id="text-uid" name="uid" value="${useradd.uid}">
	<c:if test="${err_useradd_uid}">
		<span class="error" title="Le champ est mal formé">Incorrect</span>
	</c:if>
	</td>
</tr>
<tr>
	<th title="La valeur de l’attribut REMOTE_USER de Shibboleth pour cet utilisateur">
		REMOTE_USER
	</th>
	<td>
	<input class="text" type="text" id="text-remoteUser" name="remoteUser" value="${useradd.remoteUser}">
	<c:if test="${err_useradd_remoteUser}">
		<span class="error" title="Le champ est mal formé">Incorrect</span>
	</c:if>
	</td>
</tr>
<tr>
	<th>Civilité</th>
	<td>
	<select id="select-supannCivilite" name="supannCivilite">
		<option value="aucune">(aucune)</option>
		<option value="Mme" selected>Mᵐᵉ</option>
		<option value="M.">M.</option>
	</select>
	</td>
</tr>
<tr>
	<th>Nom complet</th>
	<td>
	<input class="text" type="text" id="text-displayName" name="displayName" value="${useradd.displayName}">
	<c:if test="${err_useradd_displayName}">
		<span class="error" title="Le champ est mal formé">Incorrect</span>
	</c:if>
	</td>
</tr>
<tr>
	<th>E-mail</th>
	<td>
	<input class="text" type="text" id="text-mail" name="mail" value="${useradd.mail}">
	<c:if test="${err_useradd_mail}">
		<span class="error" title="Le champ est mal formé">Incorrect</span>
	</c:if>
	</td>
</tr>
<tr>
	<th title="Le mot de passe à utiliser lorsque Shibboleth n’est pas disponible">
		Mot de passe
	</th>
	<td>
	<input class="text" type="text" id="text-password" name="password" value="${useradd_moreInfo.password}">
	<input class="checkbox" type="checkbox" id="checkbox-passwordEnabled" name="passwordEnabled" value="yes">
	<label for="checkbox-passwordEnabled"> Activé</label>
	</td>
</tr>
<tr>
	<th title="(Optionnel) L’identifiant Twitter de l’utilisateur">
		Twitter screen_name
	</th>
	<td>
	<input class="text" type="text" id="text-twitter_screen_name" name="twitter_screen_name" value="${useradd.twitterScreenName}">
	</td>
</tr>
</tbody>
</table>

<div class="table bottom">
<!--
<a id="link-cancel" href="${baseURL}/">Annuler</a>
-->
<button id="button-cancel"
 onclick="document.location.href = '${baseURL}/users'; return false;">
	Annuler
</button>
<button id="button-save" onclick="submit()">
	Enregistrer
</button>
</div>

</div>

</form>
</div>
</body>
</html>