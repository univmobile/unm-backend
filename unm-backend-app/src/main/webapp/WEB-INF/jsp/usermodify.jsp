<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Modifier un utilisateur</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">

<style type="text/css">
td span.error {
	margin-left: 0.5em;
}

#body-usermodify label {
	xfont-family: 'Lucida Grande';
	font-size: x-small;
}
</style>

<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>

<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-usermodify" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body">
<form action="${baseURL}/usermodify/${usermodify.id}" method="POST">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div class="div-usermodify">

<c:if test="${err_incorrectFields}">
   <div class="error">
      ERREUR — des champs sont incorrects
   </div>
</c:if>

<c:if test="${err_duplicateUsername}">
   <div class="error">
      ERREUR — un utilisateur avec ce USERNAME = ${usermodify.username}
      existe déjà en base
   </div>
</c:if>


<h2>Modifier un utilisateur</h2>

<table>
<tbody>

<tr>
   <th title="L’identifiant interne à UnivMobile pour le compte utilisateur">
      Id
   </th>
   <td>
      <input readonly class="text" type="text" id="text-id" name="id" value="${usermodify.id}">
   </td>
</tr>

<tr>
   <th>Username</th>
   <td>
      <input readonly class="text" type="text" id="text-username" name="username" value="${usermodify.username}">
   </td>
</tr>

<c:choose>
	<c:when test="${role eq 'superadmin'}">

<tr class="role">
   <th>Role</th>
   <td>
      <input type="radio" id="radio-type-superadmin" name="role" value="superadmin" <c:if test="${usermodify.role eq 'superadmin'}">checked</c:if>>
      <label for="radio-type-superadmin">Super Administrateur</label>
      <input type="radio" id="radio-type-admin" name="role" value="admin" <c:if test="${usermodify.role eq 'admin'}">checked</c:if>>
      <label for="radio-type-admin">Administrateur</label>
      <input type="radio" id="radio-type-student" name="role" value="student" <c:if test="${usermodify.role eq 'student'}">checked</c:if>>
      <label for="radio-type-student">Étudiant</label>
      <input type="radio" id="radio-type-librarian" name="role" value="librarian" <c:if test="${usermodify.role eq 'librarian'}">checked</c:if>>
<label for="radio-type-student">Biblioth&eacute;caire</label>
   </td>
</tr>
	</c:when>
	
	<c:otherwise>
		 <input type="hidden" id="radio-type-librarian" name="role" value="${usermodify.role}">
	</c:otherwise>

</c:choose>

<tr>
   <th>REMOTE_USER</th>
   <td>
      <input <c:if test="${role ne 'superadmin'}">readonly</c:if> class="text" type="text" id="text-remoteUser" name="remoteUser" value="${usermodify.remoteUser}">
      <c:if test="${err_useradd_remoteUser}">
	     <span class="error" title="Le champ est mal formé">Incorrect</span>
	  </c:if>
   </td>
</tr>

<tr>
   <th>Civilité</th>
   <td>
      <select id="select-titleCivilite" name="titleCivilite">
         <c:choose>
         
            <c:when test="${usermodify.title eq 'aucune'}">
               <option value="aucune" selected>(aucune)</option>
               <option value="Mme">Mᵐᵉ</option>
               <option value="M.">M.</option>
            </c:when>
            
            <c:when test="${usermodify.title eq 'Mme'}">
               <option value="aucune">(aucune)</option>
               <option value="Mme" selected>Mᵐᵉ</option>
               <option value="M.">M.</option>
            </c:when>
            
            <c:otherwise>
               <option value="aucune">(aucune)</option>
               <option value="Mme">Mᵐᵉ</option>
               <option value="M." selected>M.</option>
            </c:otherwise>
         
         </c:choose>
      </select>
   </td>
</tr>

<tr>
   <th>Nom complet</th>
   <td>
      <input class="text" type="text" id="text-displayName" name="displayName" value="${usermodify.displayName}">
	  <c:if test="${err_usermodify_displayName}">
         <span class="error" title="Le champ est mal formé">Incorrect</span>
	  </c:if>
   </td>
</tr>

<tr>
   <th>E-mail</th>
   <td>
      <input class="text" type="text" id="text-email" name="email" value="${usermodify.email}">
	</td>
</tr>

<tr>
   <th>Mot de passe</th>
   <td>
      <input class="text" type="password" id="text-password" name="password" value="${usermodify.password}">
	  
     <c:choose>
     
         <c:when test="${usermodify.classicLoginAllowed eq 'true'}">
            <input class="checkbox" type="checkbox" id="checkbox-classicLoginAllowed" name="classicLoginAllowed" value="yes" checked>
         </c:when>
         
         <c:otherwise>
            <input class="checkbox" type="checkbox" id="checkbox-classicLoginAllowed" name="classicLoginAllowed">
         </c:otherwise>
      
      </c:choose>
      
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
      <select id="select-university" name="university">
         <c:forEach var="r" items="${regionsData}">
            <optgroup label="${r.label}">
               <c:forEach var="ru" items="${r.universities}">
                  <c:choose>
                  
                     <c:when test="${ru.id eq usermodify.university.id}">
                        <option value="${ru.id}"  <c:if test="${not ru.active}">class="univInactive"</c:if> selected>${ru.title}</option>
                     </c:when>
                    
                     <c:otherwise>
                        <c:choose>
                           
                           <c:when test="${role eq 'admin'}">
                              <c:if test="${userUnivId eq ru.id}">
                                 <option value="${ru.id}" <c:if test="${not ru.active}">class="univInactive"</c:if>>${ru.title}</option>
                              </c:if>
                           </c:when>
                           
                           <c:otherwise>
                              <option value="${ru.id}" <c:if test="${not ru.active}">class="univInactive"</c:if>>${ru.title}</option>   
                           </c:otherwise>
                        
                        </c:choose>
                     </c:otherwise>
                  
                  </c:choose> 
               </c:forEach>
            </optgroup>
         </c:forEach>
      </select>
   </td>
</tr>

<tr id="tr-secondaryUniversity">
   <th title="De quelles autres universités l’utilisateur ira-t-il consulter les informations">
      Autres universités d’intérêt
   </th>
   <td>
   	  <select id="select-secondaryUniversity" name="secondaryUniversity">
            <c:forEach var="r" items="${regionsData}">
               <optgroup label="${r.label}">
                  <c:forEach var="ru" items="${r.universities}">
                     <c:choose>
                        
                        <c:when test="${ru.id eq usermodify.secondaryUniversity.id}">
                           <option value="${ru.id}"  <c:if test="${not ru.active}">class="univInactive"</c:if> selected>${ru.title}</option>
                        </c:when>
                        
                        <c:otherwise>
                           <c:choose>
                           
                              <c:when test="${role eq 'admin'}">
                                 <c:if test="${userUnivId eq ru.id}">
                                    <option value="${ru.id}" <c:if test="${not ru.active}">class="univInactive"</c:if>>${ru.title}</option>
                                 </c:if>
                              </c:when>
                              
                              <c:otherwise>
                                 <option value="${ru.id}" <c:if test="${not ru.active}">class="univInactive"</c:if>>${ru.title}</option>   
                              </c:otherwise>
                           
                           </c:choose>
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
      <input class="text" type="text" id="text-twitter_screen_name" name="twitter_screen_name" value="${usermodify.twitterScreenName}">
   </td>
</tr>

</tbody>
</table>

<div class="table bottom">
   
   <c:choose>
   <c:when test="${role eq 'librarian'}">
	   <button id="button-cancel" onclick="document.location.href = '${baseURL}'; return false;">
	      Annuler
	   </button>
   </c:when>
   <c:otherwise>
	   <button id="button-cancel" onclick="document.location.href = '${baseURL}/api/app#/manage/users'; return false;">
	      Annuler
	   </button>
   </c:otherwise>
   </c:choose>
   
   <button id="button-save" onclick="submit()">
      Enregistrer
   </button>

</div>

</div>

</form>
</div>
</body>
</html>