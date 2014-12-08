<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Ajouter à poi</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<style type="text/css">

td span.error {
   margin-left: 0.5em;
}

#div-poiadd-buttons {
	margin-top: 2em;
	text-align: center;
}

#button-cancel {
	margin-right: 2em;

}

</style>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-poiadd" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body-poiadd">

<form method="POST" action="${baseURL}/poisadd">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-poiadd">

<c:if test="${err_duplicateUid}">
   <div class="error">
      ERREUR — un POI avec ce = ${poiadd.uid} existe déjà en base
   </div>
</c:if>
<c:if test="${err_incorrectFields}">
   <div class="error">
      ERREUR — des champs sont incorrects
   </div>
</c:if>

<h2>
   Ajouter à poi
</h2>

<table id="table-poiadd">
   <tbody>
   
   <tr class="uid">
      <th>
         Uid
      </th>
      <td>
         <input class="text" id="text-uid" name="uid">
         <span id="span-active-yes" class="selected">
            <label for="radio-active-yes">Actif</label>
            <input type="radio" name="active" value="yes" checked id="radio-active-yes">
         </span>
         <span id="span-active-no">
            <label for="radio-active-no">Inactif</label>
            <input type="radio" name="active" value="no" id="radio-active-no">
         </span>
      </td>
   </tr>
   
   <tr class="name">
      <th>
         Nom
      </th>
      <td>
         <input class="text" id="text-name" name="name">
      </td>
   </tr>
   
   <tr class="category">
      <th>
         Catégories
      </th>
      <td>
         <select id="select-poiCategory" name="poiCategory">
         <option value="(aucune)">(aucune)</option>
         <c:forEach var="pc" items="${poiCategoriesData}">
            <option value="${pc.uid}">${pc.name}</option>
         </c:forEach>
         </select>
      </td>
   </tr>
   
   <tr class="university">
      <th>
   	     Universités
      </th>
      <td>
         <select id="select-university" name="university">
         <c:forEach var="r" items="${regionsData}">
            <optgroup label="${r.label}">
               <c:forEach var="u" items="${r.universities}">
                  <option value="${u.id}">${u.title}</option>
               </c:forEach>
            </optgroup>
         </c:forEach>
         </select>
      </td>
   </tr>
   
   <tr class="floor">
      <th>
         Emplacement
      </th>
      <td>
   	     <input id="text-floor" name="floor"/>
      </td>
   </tr>
   
   <tr class="openingHours">
      <th>
         Horaires
      </th>
      <td>
   	     <input id="text-openingHours" name="openingHours"/>
      </td>
   </tr>
   
   <tr class="phone">
      <th>
         Téléphone
      </th>
      <td>
   	     <input class="text" id="text-phone" name="phone">
      </td>
   </tr>
   
   <tr class="address">
      <th>
   	     Adresse
      </th>
      <td>
   	     <input id="text-address" name="address"/>
      </td>
   </tr>
   
   <tr class="email">
      <th>
         E-mail
      </th>
      <td>
   	     <input class="text" id="text-email" name="email">
      </td>
   </tr>
   
   <tr class="itinerary">
      <th>
   	     Accès
      </th>
      <td>
   	     <input id="text-itinerary" name="itinerary"/>
      </td>
   </tr>
   
   <tr class="url">
      <th>
   	     Site web
      </th>
      <td>
   	     <input class="text" id="text-url" name="url">
      </td>
   </tr>
   
   <tr class="coordinates">
      <th>
   	     Coordonnées Lat/Lng
      </th>
      <td>
   	     <input class="text" id="text-coordinates" name="coordinates">
         <c:if test="${err_coord_not_valid}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
      </td>
   </tr>
   
   <tr class="comments">
      <th>
   	     Commentaires
      </th>
      <td>
   	     <a href="${baseURL}/comments/poi${poiadd.id}" id="link-comments">
   		 <c:choose>
   		 <c:when test="${empty commentCount or commentCount == 0}">
   		    aucun commentaire
   		 </c:when>
   		 <c:when test="${commentCount == 1}">
            un commentaire
   		 </c:when>
   		 <c:otherwise>
   		    ${commentCount} commentaires
   		 </c:otherwise>
   		 </c:choose>
   	     </a>
      </td>
   </tr>
   
   </tbody>
</table>

<div id="div-poiadd-buttons">
<button id="button-cancel" onclick="document.location.href = '${baseURL}/pois'; return false">
	Annuler
</button>
<button id="button-save" onclick="submit()">
	Enregistrer
</button>
</div>

</div> <!-- end of #div-poiadd -->
	
</form>
</div> <!-- end of div.body -->
</body>
</html>