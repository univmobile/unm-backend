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

<form method="POST" action="${baseURL}/poismodify/${poimodify.id}">

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
   
   <c:choose>          
   
   <c:when test="${poimodify.active eq 'true'}">
      <tr class="id">
         <th>État</th>
         <td>
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
   </c:when>       
         
   <c:otherwise>
     <tr class="id">
         <th>État</th>
         <td>
            <span id="span-active-yes" class="selected">
               <label for="radio-active-yes">Actif</label>
               <input type="radio" name="active" value="yes" id="radio-active-yes">
            </span>
            <span id="span-active-no">
            	<label for="radio-active-no">Inactif</label>
            	<input type="radio" name="active" value="no" checked id="radio-active-no">
            </span>
         </td>
      </tr>   
   </c:otherwise>
      
   </c:choose>
   
   <tr class="name">
      <th>Nom</th>
      <td>
         <input readonly class="text" id="text-name" name="name" value="${poimodify.name}">
      </td>
   </tr>
   
    <tr class="category">
      <th>Catégories</th>
      <td>
         <select id="select-category" name="category">
         <option value="(aucune)">(aucune)</option>
         <c:forEach var="pc" items="${poiCategoriesData}">
            <c:choose>
               
               <c:when test="${pc.id eq poimodify.category.id}">
                  <option value="${pc.name}" selected>${pc.name}</option>
               </c:when>
               
               <c:otherwise>
                  <option value="${pc.name}">${pc.name}</option>
               </c:otherwise>
               
            </c:choose>   
         </c:forEach>
         </select>
      </td>
   </tr>
   
   <tr class="university">
      <th>Universités</th>
      <td>
         <select id="select-university" name="university">
            <c:forEach var="r" items="${regionsData}">
               <optgroup label="${r.label}">
                  <c:forEach var="u" items="${r.universities}">
                     <c:choose>
                        
                        <c:when test="${poimodify.university.id eq u.id}">
                           <option value="${u.title}" selected>${u.title}</option>
                        </c:when>
                        
                        <c:otherwise>
                           <option value="${u.title}">${u.title}</option>
                        </c:otherwise>
                     
                     </c:choose>
                  </c:forEach>
               </optgroup>
            </c:forEach>
         </select>
      </td>
   </tr>
   
   <tr class="floor">
      <th>Emplacement</th>
      <td>
   	     <input id="text-floor" name="floor" value="${poimodify.floor}"/>
      </td>
   </tr>

   <tr class="city">
      <th>Ville</th>
      <td>
         <input id="text-city" name="city" value="${poimodify.city}"/>
      </td>
   </tr>
   
   <tr class="country">
      <th>Pais</th>
      <td>
         <input id="text-country" name="country" value="${poimodify.country}"/>
      </td>
   </tr>
   
   <tr class="zipcode">
      <th>Code postal</th>
      <td>
         <input id="text-zipcode" name="zipcode" value="${poimodify.zipcode}"/>
      </td>
   </tr>
   
   <tr class="openingHours">
      <th>Horaires</th>
      <td>
   	     <input id="text-openingHours" name="openingHours" value="${poimodify.openingHours}"/>
      </td>
   </tr>
   
   <tr class="phones">
      <th>Téléphone</th>
      <td>
   	     <input class="text" id="text-phone" name="phone" value="${poimodify.phones}">
      </td>
   </tr>
   
   <tr class="address">
      <th>Adresse</th>
      <td>
   	     <input id="text-address" name="address" value="${poimodify.address}"/>
      </td>
   </tr>
   
   <tr class="email">
      <th>E-mail</th>
      <td>
   	     <input class="text" id="text-email" name="email" value="${poimodify.email}">
      </td>
   </tr>
   
   <tr class="itinerary">
      <th>Accès</th>
      <td>
   	     <input id="text-itinerary" name="itinerary" value="${poimodify.itinerary}"/>
      </td>
   </tr>
   
   <tr class="url">
      <th>Site web</th>
      <td>
   	     <input class="text" id="text-url" name="url" value="${poimodify.url}">
      </td>
   </tr>
   
   <tr class="lat">
      <th>Lat</th>
      <td>
   	     <input id="text-lat" name="lat" value="${poimodify.lat}">
      </td>
   </tr>
   
   <tr class="lng">
      <th>Lng</th>
      <td>
         <input id="text-lng" name="lng" value="${poimodify.lng}">
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