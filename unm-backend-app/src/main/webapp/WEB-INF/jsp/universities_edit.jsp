<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile - Universités</title>
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
<form:form modelAttribute="universityForm" method="post" enctype="multipart/form-data">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div id="">

<h2>Universit&eacute;s - ${universityForm.title}</h2>

<form:errors element="div" cssClass="error" cssStyle="color: white;"/>

<table>
   
   <tbody>
         
      <tr>
         <th>Titre</th>
         <td>
         	<form:input type="text" path="title"/>
         	<form:errors cssClass="error" path="title" cssStyle="color: #ff0000;"/>
         </td>
      </tr>
      
      <tr>
         <th>Mobile Shibboleth Url</th>
         <td>
         	<form:input type="text" path="mobileShibbolethUrl" />
         </td>
      </tr>
      
      <tr>
         <th>Mod&eacute;r&eacute; Commentaires ?</th>
         <td>
         	<form:checkbox path="moderateComments" value="true" />
            <label for="checkbox-active"></label>
         </td>   
      </tr>

	  <c:if test="${not empty universityForm.logoUrl}">
      <tr>
         <th>Courant Logo</th>
         <td>
         	${universityForm.logoUrl}<br/>
         	<form:hidden id="logoUrl" path="logoUrl" />
         	<input type="checkbox" onclick="$('#logoUrl').val(this.checked ? '' : '${universityForm.logoUrl}')" /> clair
         </td>   
      </tr>
	  </c:if>
	  
      <tr>
         <th>Nouveau Logo</th>
         <td>
         	<input type="file" name="file" />
         </td>   
      </tr>
      
   </tbody>
   
   </table>
   
   <div class="table bottom">
      <button id="button-cancel" onclick="document.location.href = '${baseURL}/api/universities/manage/'; return false;">
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