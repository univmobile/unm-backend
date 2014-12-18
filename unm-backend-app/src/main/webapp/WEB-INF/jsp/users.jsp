<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Utilisateurs</title>
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
	/*display: none;*/
}

body.results td.roles {
	text-align: center;
	font-weight: bold;
	color: #ffc;
	width: 1em;
	padding-left: 0;
	padding-right: 0;
	background-color: #cc0;
	padding-top: 3px;
	padding-bottom: 1px;
}

body.results th.roles {
	padding-left: 0;
	padding-right: 0;
}

body.results td.roles.student {
	background-color: #090;
}

body.results td.roles.admin {
	background-color: #f60;
}

body.results td.roles.superadmin {
	background-color: #f00;
	xcolor: #f00;
}
</style>

<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-users" class="entered results">

<jsp:include page="div-entered.h.jsp"/>

<div class="body results">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-users">

<h2>
   Utilisateurs : ${usersInfo.count}
</h2>

<div id="div-query">
   <form action="${baseURL}/users" method="GET">
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
	
   <c:when test="${not empty usersInfo.context}">
      <c:out value="${usersInfo.context}"/>
	  <c:if test="${not empty usersInfo.resultCount}">
	     (${usersInfo.resultCount})
	  </c:if>
   </c:when>
   
   <c:when test="${empty usersInfo.resultCount || usersInfo.resultCount == 0}">
      Aucun résultat
   </c:when>
	
   <c:when test="${usersInfo.resultCount == 1}">
	  Un résultat
   </c:when>
   
   <c:otherwise>
	  ${usersInfo.resultCount} résultats
   </c:otherwise>
	
   </c:choose>
</span>

<button id="button-export" disabled>
	Export…
</button>

</div>

<table>

<thead>
   <tr>
      <th class="none"></th>
      <th class="id">id</th>
      <th class="roles">roles</th>
      <th class="email">email</th>
      <th class="edit"></th>
      <th class="none"></th>
   </tr>
</thead>

<tbody>
<c:forEach var="u" items="${users}">
<tr>

   <td class="none">
      <c:choose>
      
      <c:when test="${user.id == u.id}">
         <div class='principal 
         <c:if test="${delegationUser.id == u.id}">
            delegation
         </c:if>'
         title="Principal : ${user.username}">1</div>
      </c:when>
      
      <c:when test="${delegationUser.id == u.id}">
         <div class="delegation" title="Délégation : ${delegationUser.username}">2</div>
      </c:when>
      
      </c:choose>
   </td>
   
   <td class="id">
      ${u.username}
   </td>
   
   <c:choose>
      <c:when test="${u.role eq 'superadmin'}">
         <td class="roles superadmin" title="${u.id} : Super Administrateur">S</td>
      </c:when>
      
      <c:when test="${u.role eq 'admin'}">
         <td class="roles admin" title="${u.id} : Administrateur">A</td>
      </c:when>
      
      <c:when test="${u.role eq 'student'}">
         <td class="roles student" title="${u.id} : Étudiant">É</td>
      </c:when>
      
      <c:otherwise>
         <td class="roles unknown" title="(${u.id} : Type inconnu)">-</td>
      </c:otherwise>
   </c:choose>
   
   <td class="email">
      ${u.email}
   </td>
   
   <td class="edit">
      <c:choose>
      
         <c:when test="${role eq 'student'}">
            <a>Modifier…</a>
         </c:when>
         
         <c:otherwise>
            <a id="link-edit_${u.id}" href="${baseURL}/usermodify/${u.id}">Modifier…</a>
         </c:otherwise>
      
      </c:choose>
   </td>
   
   <td class="none">
   </td>
   
</tr>
</c:forEach>
</tbody>
</table>

<div class="table bottom">
   <c:choose>
      
      <c:when test="${role eq 'student'}">
         <a>Ajouter un utilisateur…</a>
      </c:when>
      
      <c:otherwise>
         <a id="link-useradd" href="${baseURL}/useradd">Ajouter un utilisateur…</a>
      </c:otherwise>
   
   </c:choose>
</div>

</div> <!-- end of #div-users -->
	
</div> <!-- end of div.body -->

</body>
</html>