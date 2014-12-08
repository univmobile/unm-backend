<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Modification d'un POI</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<style type="text/css">

td span.error {
   margin-left: 0.5em;
}

#div-poimodify-buttons {
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
<body id="body-poimodify" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body-poimodify">

<form method="POST" action="${baseURL}/poismodify/${poimodify.uid}">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-poimodify">

<h2>
   Modification d'un "poi"
</h2>

<table id="table-poimodify">
   <tbody>
   
   <tr class="uid">
      <th>
         Uid
      </th>
      <td>
         <input readonly class="text" id="text-uid" name="uid" value="${poimodify.uid}">
         
         <c:choose>
            <c:when test="${poimodify.active eq 'true'}">
               <span id="span-active-yes" class="selected">
                  <label for="radio-active-yes">Actif</label>
                  <input type="radio" name="active" value="yes" checked id="radio-active-yes">
               </span>
               <span id="span-active-no">
                  <label for="radio-active-no">Inactif</label>
                  <input type="radio" name="active" value="no" id="radio-active-no">
               </span>
            </c:when>
            <c:otherwise>
               <span id="span-active-yes" class="selected">
                  <label for="radio-active-yes">Actif</label>
                  <input type="radio" name="active" value="yes" id="radio-active-yes">
               </span>
               <span id="span-active-no">
                  <label for="radio-active-no">Inactif</label>
                  <input type="radio" name="active" value="no" checked id="radio-active-no">
               </span>
            </c:otherwise>
         </c:choose>

      </td>
   </tr>
   
   <tr class="name">
      <th>
         Nom
      </th>
      <td>
         <input class="text" id="text-name" name="name" value="${poimodify.name}">
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
            <c:choose>
               <c:when test="${pc.uid eq poimodify.categoryId}">
                  <option value="${pc.uid}" selected>${pc.name}</option>
               </c:when>
               <c:otherwise>
                  <option value="${pc.uid}">${pc.name}</option>
               </c:otherwise>
            </c:choose>   
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
                     <c:choose>
                        <c:when test="${poimodify.universityIds[0] eq u.id}">
                           <option value="${u.id}" selected>${u.title}</option>
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
   
   <tr class="floor">
      <th>
         Emplacement
      </th>
      <td>
   	     <input id="text-floor" name="floor" value="${poimodify.addresses[0].floor}"/>
      </td>
   </tr>
   
   <tr class="openingHours">
      <th>
         Horaires
      </th>
      <td>
   	     <input id="text-openingHours" name="openingHours" value="${poimodify.addresses[0].openingHours}"/>
      </td>
   </tr>
   
   <tr class="phone">
      <th>
         Téléphone
      </th>
      <td>
   	     <input class="text" id="text-phone" name="phone" value="${poimodify.addresses[0].phone}">
      </td>
   </tr>
   
   <tr class="address">
      <th>
   	     Adresse
      </th>
      <td>
   	     <input id="text-address" name="address" value="${poimodify.addresses[0].fullAddress}"/>
      </td>
   </tr>
   
   <tr class="email">
      <th>
         E-mail
      </th>
      <td>
   	     <input class="text" id="text-email" name="email" value="${poimodify.emails[0]}">
      </td>
   </tr>
   
   <tr class="itinerary">
      <th>
   	     Accès
      </th>
      <td>
   	     <input id="text-itinerary" name="itinerary" value="${poimodify.addresses[0].itinerary}"/>
      </td>
   </tr>
   
   <tr class="url">
      <th>
   	     Site web
      </th>
      <td>
   	     <input class="text" id="text-url" name="url" value="${poimodify.urls[0]}">
      </td>
   </tr>
   
   <tr class="coordinates">
      <th>
   	     Coordonnées Lat/Lng
      </th>
      <td>
   	     <input class="text" id="text-coordinates" name="coordinates" value="${poimodify.coordinates}">
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
   	     <!-- <a href="${baseURL}/comments/poi${poimodify.id}" id="link-comments"> -->
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
         <!-- </a> -->
      </td>
   </tr>
   
   </tbody>
</table>

<div class="table bottom">
   <button id="button-cancel" onclick="document.location.href = '${baseURL}/pois'; return false">
      Annuler
   </button>
   <button id="button-save" onclick="submit()">
      Enregistrer
   </button>
</div>

</div> <!-- end of #div-poimodify -->
	
</form>
</div> <!-- end of div.body -->
</body>
</html>