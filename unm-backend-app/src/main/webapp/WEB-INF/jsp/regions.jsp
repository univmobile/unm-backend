<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Régions</title>

<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>

</head>

<body id="body-regions" class="results entered">

<jsp:include page="div-entered.h.jsp"/>

<div class="body">
<form action="${baseURL}/regions/" method="POST">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="div-regions">

<h2>Régions : ${fn:length(regions)}</h2>

<table>
<thead>

<tr>
   <th class="none"></th>
   <th class="id">Id</th>
   <th class="label">Label</th>
   <th class="universityCount">Universités</th>
   <th class="none"></th>
</tr>

</thead>

<tbody>
<c:forEach var="r" items="${regions}">

<tr>

   <td class="none">
   </td>
   
   <td class="id">
      <a href="${baseURL}/api/universities/manage/?regionId=${r.id}">
         ${r.id}
      </a>
   </td>
   
   <td class="label">
      <input type="text" id="text-region_${r.id}" name="region_${r.id}" value="${r.label}"/>
   </td>
   
   <td class="universityCount">
      ${fn:length(r.universities)}
   </td>
   
   <td class="none">
   </td>

</tr>

</c:forEach>
</tbody>
</table>

<div class="table bottom">

   <button id="button-cancel" onclick="document.location.href = '${baseURL}/'; return false;">
      Annuler
   </button>
   
   <button id="button-submit" onclick="submit()">
      Enregistrer
   </button>
   
</div>

<div class="table bottom">
   JSON :
   <a id="link-json" href="${baseURL}/api/regions">
      ${baseURL}/api/regions
   </a>
</div>

</div> <!-- end of #div-regions -->

</form>
</div> <!-- end of div.body -->

</body>
</html>