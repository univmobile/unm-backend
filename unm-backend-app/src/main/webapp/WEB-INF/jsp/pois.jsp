<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — POIs</title>

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

td.universityIds ul {
	margin: 0;
	padding: 0;
	list-style-type: none;
}

th.id,
td.id {
	display: none;
}

</style>

<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-pois" class="entered results">

<jsp:include page="div-entered.h.jsp"/>

<div class="body results">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-pois">

<h2>
   POIs : ${poisInfo.count}
</h2>

<div id="div-query">
<form action="${baseURL}/pois" method="GET">
   
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
   
      <c:when test="${not empty poisInfo.context}">
   	     ${poisInfo.context}
   	     <c:if test="${not empty poisInfo.resultCount}">
   		    (${poisInfo.resultCount})
   	     </c:if>
      </c:when>
      
      <c:when test="${empty poisInfo.resultCount || poisInfo.resultCount == 0}">
   	     Aucun résultat
      </c:when>
      
      <c:when test="${poisInfo.resultCount == 1}">
   	     Un résultat
      </c:when>
      
      <c:otherwise>
   	     ${poisInfo.resultCount} résultats
      </c:otherwise>
      
   </c:choose>
   
</span>

</div>

<div class="table bottom">
   <c:if test="${user.role eq 'superadmin'}">
      <a id="link-poiadd" href="${baseURL}/poisadd">Ajouter un poi…</a>
   </c:if>
</div>

<c:forEach var="poiGroup" items="${poiGroups}">

<h3>
	${poiGroup.region.name}
</h3>

<c:choose>

<c:when test="${fn:length(poiGroup.pois) == 0}">
   
   <div class="emptyArray">
	  Aucun POI
   </div>
   
</c:when>

<c:otherwise>
   <table>
   <thead>
   
      <tr>
         <th class="university-title">Universités</th>
         <th class="id">Id</th>
         <th class="name">POI</th>
         <th class="address">Adresse</th>
         <th class="edit">Action</th>
      </tr>
      
   </thead>
   <tbody>
   
   <c:forEach var="poi" items="${poiGroup.pois}">
   
      <c:set var="href" value="${baseURL}/pois/${poi.id}"/>
      
      <tr>
      
         <td class="university-title">
            ${poi.university.title}
         </td>
         
         <td class="id" onclick="document.location.href = '${href}'">
            <a href="${href}" id="link-poi-${poi.id}-id">
               ${poi.id}
            </a>
         </td>
         
         <td class="name" onclick="document.location.href = '${href}'">
            <a href="${href}" id="link-poi-${poi.id}-name">
               ${poi.name}
      	    </a>
         </td>
         
         <td class="address">
            ${poi.address}
         </td>
         
         <td class="edit">
            <a id="link-edit_${poi.id}" href="${baseURL}/poismodify/${poi.id}">Modifier…</a>
         </td>
         
      </tr>
      </c:forEach>
      </tbody>
      </table>
   </c:otherwise>
   </c:choose>

</c:forEach>

<div class="table bottom">
   <c:if test="${user.role eq 'superadmin'}">
      <a id="link-poiadd" href="${baseURL}/poisadd">Ajouter un poi…</a>
   </c:if>
</div>

</div> <!-- end of #div-pois -->
	
</div> <!-- end of div.body -->

</body>
</html>