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

<c:if test="${err_duplicateusername}">
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
      id
   </th>
   <td>
      <input readonly class="text" type="text" id="text-id" name="id" value="${usermodify.id}">
   </td>
</tr>

<tr class="role">
   <th title="Le profil de ce compte utilisateur">
      Role
   </th>
   
   <c:choose>
   
      <c:when test="${role eq 'superadmin'}">
         <c:choose>
         
         <c:when test="${usermodify.role eq 'superadmin'}">
            <td>
               <input type="radio" id="radio-type-superadmin" name="role" value="superadmin" checked>
               <label for="radio-type-superadmin">Super Administrateur</label>
               <input type="radio" id="radio-type-admin" name="role" value="admin">
               <label for="radio-type-admin">Administrateur</label>
              <input type="radio" id="radio-type-student" name="role" value="student">
               <label for="radio-type-student">Étudiant</label>
            </td>
         </c:when>
         
         <c:when test="${usermodify.role eq 'admin'}">
            <td>
               <input type="radio" id="radio-type-superadmin" name="role" value="superadmin">
               <label for="radio-type-superadmin">Super Administrateur</label>
               <input type="radio" id="radio-type-admin" name="role" value="admin" checked>
               <label for="radio-type-admin">Administrateur</label>
               <input type="radio" id="radio-type-student" name="role" value="student">
               <label for="radio-type-student">Étudiant</label>
            </td>
         </c:when>
         
         <c:when test="${usermodify.role eq 'student'}">
            <td>
               <input type="radio" id="radio-type-superadmin" name="role" value="superadmin">
               <label for="radio-type-superadmin">Super Administrateur</label>
               <input type="radio" id="radio-type-admin" name="role" value="admin">
               <label for="radio-type-admin">Administrateur</label>
               <input type="radio" id="radio-type-student" name="role" value="student" checked>
               <label for="radio-type-student">Étudiant</label>
            </td>
         </c:when>
         </c:choose>
      </c:when>
      
      <c:otherwise>
         <td>
            <input disabled type="radio" id="radio-type-superadmin" name="role" value="superadmin">
            <label for="radio-type-superadmin">Super Administrateur</label>
            <input disabled type="radio" id="radio-type-admin" name="role" value="admin">
            <label for="radio-type-admin">Administrateur</label>
            <input type="radio" id="radio-type-student" name="role" value="student" checked>
            <label for="radio-type-student">Étudiant</label>
         </td>
      </c:otherwise>
      
   </c:choose>
</tr>

<tr>
   <th title="La valeur de l’attribut REMOTE_USER de Shibboleth pour cet utilisateur">
      REMOTE_USER
   </th>
   <td>
      <input readonly class="text" type="text" id="text-remoteUser" name="remoteUser" value="${usermodify.remoteUser}">
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
   <th title="Le mot de passe à utiliser lorsque Shibboleth n’est pas disponible">
      Mot de passe
   </th>
   <td>
      <input class="text" type="password" id="text-password" name="password" value="${usermodify_moreInfo.password}">
	  <c:choose>
     
         <c:when test="${usermodify.passwordEnabled eq 'true'}">
            <input class="checkbox" type="checkbox" id="checkbox-passwordEnabled" name="passwordEnabled" value="yes" checked>
         </c:when>
         
         <c:otherwise>
            <input class="checkbox" type="checkbox" id="checkbox-passwordEnabled" name="passwordEnabled">
         </c:otherwise>
      
      </c:choose>
      <label for="checkbox-passwordEnabled">
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
                  
                     <c:when test="${u.id eq usermodify.primaryUniversity}">
                        <option value="${u.id}" selected>${u.title}</option>
                     </c:when>
                    
                     <c:otherwise>
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
                  <c:forEach var="u" items="${r.universities}">
                     <c:choose>
                        
                        <c:when test="${u.id eq usermodify.secondaryUniversity}">
                           <option value="${u.id}" selected>${u.title}</option>
                        </c:when>
                        
                        <c:otherwise>
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