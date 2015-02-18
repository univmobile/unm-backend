<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Catégories de pois</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">

<style type="text/css">
td.id,
td.name {
	cursor: pointer;
}

td.id:active a,
td.name:active a,
td a:active {
	color: #f00;
}

th.id,
td.id {
	display: none;
}
</style>

<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-poicategories" class="entered results">

<script>
function myFunction() {
    var x = document.getElementById("text-query").value;
    window.location = "/unm-backend/poicategories/" + x;
}
</script>

<jsp:include page="div-entered.h.jsp"/>

<div class="body results">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-poicategories">

<h2>
   Catégories de pois : ${poiCategoriesInfo.count}
</h2>

<div id="div-query">
   <form action="javascript: myFunction()" method="GET">
   <input id="text-query" name="q">
   
      <button id="button-search">
         Rechercher
      </button>
      
   </form>
</div>

<div id="div-resultInfo">
<span>
   →
   <c:choose>
      
   <c:when test="${not empty poiCategoriesInfo.context}">
      <c:out value="${poiCategoriesInfo.context}"/>
      <c:if test="${not empty poiCategoriesInfo.resultCount}">
         (${poiCategoriesInfo.resultCount})
      </c:if>
   </c:when>
   	  
   <c:when test="${empty poiCategoriesInfo.resultCount || poiCategoriesInfo.resultCount == 0}">
      Aucun résultat
   </c:when>
   	
   <c:when test="${poiCategoriesInfo.resultCount == 1}">
      Un résultat
   </c:when>
   	  
   <c:otherwise>
      ${poiCategoriesInfo.resultCount} résultats
   </c:otherwise>
   	
   </c:choose>
</span>

</div>

<table>
<thead>
   <tr>
      <th class="none"></th>
      <th class="cId">Id</th>
      <th class="nom">Nom</th>
      <th class="description">Description</th>
      <th class="active">Active</th>
      <th class="edit">Action</th>
      <th class="none"></th>
   </tr>
</thead>

<tbody>
   <c:forEach var="pc" items="${poicategories}">
   
      <tr>
      
      <td class="none">
         <c:choose>
         
            <c:when test="${user.id == u.id}">
               <div class="principal
                  <c:if test="${delegationUser.id == u.id}">
                     delegation
                  </c:if>
                  " title="Principal : ${user.id}">1</div>
            </c:when>
            
            <c:when test="${delegationUser.id == u.id}">
               <div class="delegation" title="Délégation : ${delegationUser.id}">2</div>
            </c:when>
         
         </c:choose>
      </td>

      <td class="cId">
         ${pc.id}
      </td>
      
      <td class="nom">
         ${pc.name}
      </td>
      
      <td class="description">
         ${pc.description}
      </td>
      
      <td class="active">
         <c:choose>
         
            <c:when test="${pc.active}">
            	Active
            </c:when>
            
            <c:otherwise>
            	Inactive    
            </c:otherwise>
         
         </c:choose>
      </td>
      
      <td class="edit">
         <a id="link-modify_${pc.id}" href="${baseURL}/poicategoriesmodify/${pc.id}">Modifier…</a><br/>
         <a id="link-sons_${pc.id}" href="${baseURL}/poicategories/${pc.id}">Sous-catégories…</a><br/>
         <a id="link-icons_${pc.id}" href="${baseURL}/api/categories/manage/${pc.id}/icons">Icônes…</a>
      </td>
      
      <td class="none">
      </td>
      
      </tr>
      
   </c:forEach>
</tbody>
   
</table>

<div class="table bottom">
   <c:if test="${not empty father}">
      <a id="link-father_${father.parent.id}" href="${baseURL}/poicategories/${father.parent.id}">Catégorie parente…</a>
   </c:if>
</div>

<div class="table bottom">
   <c:choose>
       <c:when test="${not empty father}">
           <a id="link-poicategoryadd" href="${baseURL}/poicategoriesadd/${father.id}">Ajouter une catégorie…</a>
       </c:when>
       <c:otherwise>
           <a id="link-poicategoryadd" href="${baseURL}/poicategoriesadd">Ajouter une catégorie…</a>   
       </c:otherwise>
   </c:choose>
</div>

</div> <!-- end of #div-users -->
   
</div> <!-- end of div.body -->

</body>
</html>
