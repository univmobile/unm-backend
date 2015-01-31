<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Universités</title>
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
<body id="body-universities" class="entered results">

<jsp:include page="div-entered.h.jsp"/>

<div class="body results">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-universities">

<h2>
   Universités
</h2>

<table>
<thead>
   <tr>
      <th class="id">Id</th>
      <th class="title">Titre</th>
   </tr>
</thead>

<tbody>
   <c:forEach var="u" items="${universities}">
   
      <tr>
      
      <td class="id">
         ${u.id}
      </td>
      
      <td class="title">
         ${u.title}
      </td>
      
   </c:forEach>
</tbody>
   
</table>

</div> <!-- end of #div-universities -->
   
</div> <!-- end of div.body -->

</body>
</html>
