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
<style type="text/css">
td span.error {
	margin-left: 0.5em;
}
#div-secondaryUnivs {
	height: 8em;
	overflow-y: scroll;
	border: 2px inset #ccc;
}
#body-useradd #div-secondaryUnivs label {
	xfont-family: 'Lucida Grande';
	font-size: x-small;
}
#tr-secondaryUnivs th {
	vertical-align: top;
}
label.checkbox-secondaryUniv-region {
	font-weight: bold;
}
</style>
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
<script type="text/javascript">

$(function () {

	<c:forEach var="region" items="${regions}">

    $('#checkbox-secondaryUniv-region_${region.uid}').change(function () {
    	var checked = $(this).prop('checked');
        $('.checkbox.secondaryUniv.region_${region.uid}').prop('checked', checked);
    });

    $('.checkbox.secondaryUniv.region_${region.uid}').change(function () {
        if ($('.checkbox.secondaryUniv.region_${region.uid}:checked').length === 0) {
            $('#checkbox-secondaryUniv-region_${region.uid}').
                prop('indeterminate', false).
                prop('checked', false);
        } else if ($('.checkbox.secondaryUniv.region_${region.uid}:not(:checked)').length === 0) {
            $('#checkbox-secondaryUniv-region_${region.uid}').
                prop('indeterminate', false).
                prop('checked', true);
        } else {
            $('#checkbox-secondaryUniv-region_${region.uid}').
                prop('indeterminate', true);
        }
    });     

	</c:forEach>

/*    
    $('.select_one').change (function () {
        if ($('.select_one:checked').length === 0) {
            $('#select_all').
                prop("indeterminate", false).
                attr('checked', false);
        } else if ($('.select_one:not(:checked)').length === 0) {
            $('#select_all').
                prop("indeterminate", false).
                attr('checked', true);
        } else {
            $('#select_all').
                prop("indeterminate", true);
        }
    });     
    */
});

</script>
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
<c:if test="${err_incorrectFields}">
<div class="error">
	ERREUR — des champs sont incorrects
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
<tr class="type">
	<th title="Le profil de ce compte utilisateur">
		Type
	</th>
	<td>
	<input type="radio" id="radio-type-superadmin" name="type"
		value="superadmin"><label
		id="label-type-superadmin"
		for="radio-type-superadmin">Super Administrateur</label>
	<input type="radio" id="radio-type-admin" name="type"
		value="admin"><label
		id="label-type-admin" 
		for="radio-type-admin">Administrateur</label>
	<input type="radio" id="radio-type-student" name="type"
		value="student"><label
		id="label-type-student" 
		for="radio-type-student">Étudiant</label>
	<c:if test="${err_useradd_type_none}">
		<span class="error" title="Le type du compte utilisateur n’est pas renseigné">Incorrect</span>
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
	<th title="L’université de rattachment de cet utilisateur">
		Université de rattachement
	</th>
	<td>
		<select id="select-primaryUniv" name="primaryUniv">
		<option value="unknown"></option>
		<c:forEach var="region" items="${regions}">
			<option disabled value="region_${region.uid}">——— Région : ${region.label}</option>
			<c:forEach var="university" items="${region.universities}">
				<option value="${university.id}" title="id=${university.id}">
					&nbsp;&nbsp;&nbsp;${university.title}
				</option>
			</c:forEach>
		</c:forEach>
		</select>
	</td>
</tr>
<tr id="tr-secondaryUnivs">
	<th title="De quelles autres universités l’utilisateur ira-t-il consulter les informations">
		Autres universités d’intérêt
	</th>
	<td>
		<div id="div-secondaryUnivs">
		<c:forEach var="region" items="${regions}">
			<input type="checkbox" id="checkbox-secondaryUniv-region_${region.uid}"><label
				class="checkbox-secondaryUniv-region"
				for="checkbox-secondaryUniv-region_${region.uid}"> Région: ${region.label}</label>
			<br/>
			<c:forEach var="university" items="${region.universities}">
				&nbsp;&nbsp;&nbsp;
				<input type="checkbox" 
					title="id=${university.id}"
					class="checkbox secondaryUniv region_${region.uid}"
					id="checkbox-secondaryUniv-${university.id}"><label
					title="id=${university.id}"
					for="checkbox-secondaryUniv-${university.id}"> ${university.title}</label>
				<br/>
			</c:forEach>
		</c:forEach>
		</div>
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