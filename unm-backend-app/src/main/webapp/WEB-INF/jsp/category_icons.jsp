<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile - Cat&eacute;gories</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>

<jsp:include page="js-adminMenu.h.jsp"/>
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

<body id="body-regions" class="entered">

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
<form:form modelAttribute="categoryForm" method="post" enctype="multipart/form-data">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="">

<h2>Cat&eacute;gories - ${categoryForm.name} - Icônes</h2>
<form:hidden path="name"/>

<form:errors element="div" cssClass="error" cssStyle="color: white;"/>

<table>
   
   <tbody>
         
	  <c:if test="${not empty categoryForm.activeIconUrl}">
      <tr>
         <th>Icône active courante</th>
         <td>
         	${categoryForm.activeIconUrl}<br/>
         	<form:hidden id="activeIconUrl" path="activeIconUrl" />
         	<input type="checkbox" onclick="$('#activeIconUrl').val(this.checked ? '' : '${categoryForm.activeIconUrl}')" /> clair
         </td>   
      </tr>
	  </c:if>
	  
      <tr>
         <th>Choisir une nouvelle icône active</th>
         <td>
         	<input type="file" name="file" />
         </td>   
      </tr>
      
	  <c:if test="${not empty categoryForm.inactiveIconUrl}">
      <tr>
         <th>Icône inactive courante</th>
         <td>
         	${categoryForm.inactiveIconUrl}<br/>
         	<form:hidden id="inactiveIconUrl" path="inactiveIconUrl" />
         	<input type="checkbox" onclick="$('#inactiveIconUrl').val(this.checked ? '' : '${categoryForm.inactiveIconUrl}')" /> clair
         </td>   
      </tr>
	  </c:if>
	  
      <tr>
         <th>Chosir une nouvelle icône inactive</th>
         <td>
         	<input type="file" name="file2" />
         </td>   
      </tr>
      
	  <c:if test="${not empty categoryForm.markerIconUrl}">
      <tr>
         <th>Icône marqueur carte courante</th>
         <td>
         	${categoryForm.markerIconUrl}<br/>
         	<form:hidden id="markerIconUrl" path="markerIconUrl" />
         	<input type="checkbox" onclick="$('#markerIconUrl').val(this.checked ? '' : '${categoryForm.markerIconUrl}')" /> clair
         </td>   
      </tr>
	  </c:if>
	  
      <tr>
         <th>Choisir une nouvelle icône marqueur map</th>
         <td>
         	<input type="file" name="file3" />
         </td>   
      </tr>
      
   </tbody>
   
   </table>
   
   <div class="table bottom">
      <button id="button-cancel" onclick="document.location.href = '${baseURL}/poicategories/${parentId}'; return false;">
      	Annuler
      </button>
      
      <button id="button-save" type="submit">
      	Enregistrer
      </button>
   </div>

</div>

</form:form>

</div> <!-- end of div.body -->

</body>
</html>