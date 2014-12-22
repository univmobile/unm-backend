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

#div-secondaryUniversity {
	height: 8em;
	overflow-y: scroll;
	border: 2px inset #ccc;
}

#body-useradd #div-secondaryUniversity label {
	xfont-family: 'Lucida Grande';
	font-size: x-small;
}

#tr-secondaryUniversity th {
	vertical-align: top;
}

label.checkbox-secondaryUniversity-region {
	font-weight: bold;
}
</style>

<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

<script type="text/javascript">

$(function () {

	<c:forEach var="region" items="${regions}">

    $('#checkbox-secondaryUniversity-region_${region.id}').change(function () {
    	var checked = $(this).prop('checked');
        $('.checkbox.secondaryUniversity.region_${region.id}').prop('checked', checked);
    });

    $('.checkbox.secondaryUniversity.region_${region.id}').change(function () {
        if ($('.checkbox.secondaryUniversity.region_${region.id}:checked').length === 0) {
            $('#checkbox-secondaryUniversity-region_${region.id}').
                prop('indeterminate', false).
                prop('checked', false);
        } else if ($('.checkbox.secondaryUniversity.region_${region.id}:not(:checked)').length === 0) {
            $('#checkbox-secondaryUniversity-region_${region.id}').
                prop('indeterminate', false).
                prop('checked', true);
        } else {
            $('#checkbox-secondaryUniversity-region_${region.id}').
                prop('indeterminate', true);
        }
    });     

	</c:forEach>

});

</script>

</head>
<body id="body-useradd" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body">
<form action="${baseURL}/useradd" method="POST">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div class="div-useradd">

<c:if test="${err_incorrectFields}">
   <div class="error">
      ERREUR — des champs sont incorrects
   </div>
</c:if>

<c:if test="${err_duplicateRemoteUser}">
   <div class="error">
      ERREUR — un utilisateur avec ce REMOTE_USER = ${useradd.remoteUser}
      existe déjà en base
   </div>
</c:if>

<c:if test="${err_duplicateUsername}">
   <div class="error">
      ERREUR — un utilisateur avec ce USERNAME = ${useradd.username}
      existe déjà en base
   </div>
</c:if>

<h2>Ajout d’un utilisateur</h2>

<table>
<tbody>

<tr class="role">
   <th title="Le profil de ce compte utilisateur">
      Role
   </th>
   <td>
   
      <c:choose>
         <c:when test="${role eq 'admin'}">
<<<<<<< HEAD
            <input disabled type="radio" id="radio-type-superadmin" name="role" value="superadmin">
            <label for="radio-type-superadmin">Super Administrateur</label>
            <input disabled type="radio" id="radio-type-admin" name="role" value="admin">
            <label for="radio-type-admin">Administrateur</label>
            <input type="radio" id="radio-type-student" name="role" value="student" class="selected" checked>
=======
            <input disabled type="radio" id="radio-type-superadmin" name="type" value="superadmin">
            <label for="radio-type-superadmin">Super Administrateur</label>
            <input disabled type="radio" id="radio-type-admin" name="type" value="admin">
            <label for="radio-type-admin">Administrateur</label>
            <input type="radio" id="radio-type-student" name="type" value="student" class="selected" checked>
>>>>>>> Adjustments in screens of creation and modification of users
            <label for="radio-type-student">Étudiant</label>
         </c:when>
         
         <c:otherwise>
<<<<<<< HEAD
            <input type="radio" id="radio-type-superadmin" name="role" value="superadmin">
            <label for="radio-type-superadmin">Super Administrateur</label>
            <input type="radio" id="radio-type-admin" name="role" value="admin">
            <label for="radio-type-admin">Administrateur</label>
            <input type="radio" id="radio-type-student" name="role" value="student" class="selected" checked>
=======
            <input type="radio" id="radio-type-superadmin" name="type" value="superadmin">
            <label for="radio-type-superadmin">Super Administrateur</label>
            <input type="radio" id="radio-type-admin" name="type" value="admin">
            <label for="radio-type-admin">Administrateur</label>
            <input type="radio" id="radio-type-student" name="type" value="student" class="selected" checked>
>>>>>>> Adjustments in screens of creation and modification of users
            <label for="radio-type-student">Étudiant</label>
         </c:otherwise>
      </c:choose>
      
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
      <select id="select-titleCivilite" name="titleCivilite">
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
      <input class="text" type="text" id="text-email" name="email" value="${useradd.email}">
   </td>
</tr>

<tr>
   <th>Nom d'utilisateur</th>
   <td>
      <input class="text" type="text" id="text-username" name="username" value="${useradd.username}">
     <c:if test="${err_useradd_username}">
         <span class="error" title="Le champ est mal formé">Incorrect</span>
     </c:if>
   </td>
</tr>

<tr>
   <th title="Le mot de passe à utiliser lorsque Shibboleth n’est pas disponible">
      Mot de passe
   </th>
   <td>
      <input class="text" type="password" id="text-password" name="password" value="${useradd_moreInfo.password}">
	  <input class="checkbox" type="checkbox" id="checkbox-classicLoginAllowed" name="classicLoginAllowed" value="yes">
	  <label for="checkbox-classicLoginAllowed">
         Activé
      </label>
   </td>
</tr>

<tr>
   <th title="L’université de rattachment de cet utilisateur">
      Université de rattachement
   </th>
   <td>
      <select id="select-primaryUniversity" name="primaryUniversity">
         <c:forEach var="r" items="${regionsData}">
            <optgroup label="${r.label}">
               <c:forEach var="u" items="${r.universities}">
                  <c:choose>
                     <c:when test="${role eq 'admin'}">
                        <c:if test="${userUnivId eq u.id}">
                           <option value="${u.id}">${u.title}</option>
                        </c:if>
                     </c:when>
                     <c:otherwise>
                        <option value="${u.id}">${u.title}</option>   
                     </c:otherwise>
                  </c:choose>
               </c:forEach>
            </optgroup>
         </c:forEach>
      </select>
   </td>
</tr>

<tr>
   <th title="De quelles autres universités l’utilisateur ira-t-il consulter les informations">
      Autres universités d’intérêt
   </th>
   <td>
   	  <select id="select-secondaryUniversity" name="secondaryUniversity">
            <c:forEach var="r" items="${regionsData}">
               <optgroup label="${r.label}">
                  <c:forEach var="u" items="${r.universities}">
                     <c:choose>
                        <c:when test="${role eq 'admin'}">
                           <c:if test="${userUnivId eq u.id}">
                              <option value="${u.id}">${u.title}</option>
                           </c:if>
                        </c:when>
                        <c:otherwise>
                           <option value="${u.id}">${u.title}</option>   
                        </c:otherwise>
                     </c:choose>
                  </c:forEach>
               </optgroup>
            </c:forEach>
      </select>
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

<tr>
   <th>Description</th>
   <td>
      <input class="text" type="text" id="text-description" name="description" value="${useradd.description}">
   </td>
</tr>

</tbody>
</table>

<div class="table bottom">
   <button id="button-cancel" onclick="document.location.href = '${baseURL}/users'; return false;">
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