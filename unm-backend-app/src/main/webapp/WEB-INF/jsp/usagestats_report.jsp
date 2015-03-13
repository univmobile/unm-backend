<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Statistiques de connexion</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>

<jsp:include page="js-adminMenu.h.jsp"/>
<script>
  $(function() {
    $("input[type='text']").datepicker({
    	dateFormat: 'dd-mm-yy'
    });
  });
</script>
<style>
#ui-datepicker-div td {
  border: 0;
  padding: 1px;
}
#ui-datepicker-div th {
  border: 0;
  background-color: inherit;
  padding: .7em .3em;
}
</style>
</head>

<body id="body-regions" class="results entered">

<div id="div-entered">

<jsp:include page="ul-adminMenu.h.jsp"/>

<ul id="ul-adminUser">
   <li> Principal : ${user.username}
      <c:if test="${user.id != delegationUser.id}">
         <li> Délégation : ${delegationUser.username}
      </c:if>
   <li id="li-logout">
      <a id="link-logout" href="${baseURL}/?logout">Déconnexion</a>
</ul>

</div>

<div class="body">
<form:form modelAttribute="statsForm" method="GET">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-regions">

<h2>Statistiques de connexion</h2>

<div>
	<c:if test="${fn:length(universities) gt 1}">
		Université: <form:select path="university" id="university" items="${universities}" itemValue="id" itemLabel="title" /><br/>
	</c:if>
	
	de: <form:input type="text" path="from"/>&nbsp;&nbsp; à: <form:input type="text" path="to"/>
		 
	<p style="text-align: center"><input type="submit" value="Envoyer"><p>
</div>

<c:if test="${fn:length(stats) eq 0}">
	<div style="text-align: center"><h3>Aucune statistique</h3></div>
</c:if>


<c:if test="${fn:length(stats) gt 0}">

<table>
<thead>

<tr>
   <th class="id">Origine</th>
   <th class="label">Nombre de connexions</th>
</tr>

</thead>

<tbody>

<c:forEach var="r" items="${stats}">

<tr>

   <td class="" style="text-align: left">
   	  <c:choose>
   	  <c:when test="${r.source eq 'A'}">
      Android
      </c:when>
   	  <c:when test="${r.source eq 'I'}">
      iOS
      </c:when>
   	  <c:when test="${r.source eq 'W'}">
      Web
      </c:when>
      <c:otherwise>
      ${r.source}
      </c:otherwise>
      </c:choose>
   </td>
   
   <td class="" style="text-align: right">
      ${r.count}
   </td>
   
</tr>

</c:forEach>

</tbody>
</table>

</c:if>


</div>

</form:form>

</div> <!-- end of div.body -->

</body>
</html>