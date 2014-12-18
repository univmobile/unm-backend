<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Modifier une catégorie</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<style type="text/css">
td span.error {
	margin-left: 0.5em;
}

#body-poicategorymodify label {
	xfont-family: 'Lucida Grande';
	font-size: x-small;
}

label.checkbox-parentId {
	font-weight: bold;
}
</style>

<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>

<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-poicategorymodify" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body">
<form action="${baseURL}/poicategoriesmodify/${poicategorymodify.id}" method="POST">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div class="div-poicategorymodify">

   <c:if test="${err_incorrectFields}">
   <div class="error">
   	ERREUR — des champs sont incorrects
   </div>
   </c:if>
   
   
   <h2>Modifier d'une catégorie</h2>
   
   <table>
   
   <tbody>
      <tr>
         <th>Id</th>
         <td>
            <input readonly class="text" type="text" id="text-id" name="id" value="${poicategorymodify.id}">
         <c:if test="${err_poicategorymodify_id}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
         </td>
      </tr>
      
      <tr>
         <th>Name</th>
         <td>
            <input class="text" type="text" id="text-name" name="name" value="${poicategorymodify.name}">
         <c:if test="${err_poicategorymodify_name}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
         </td>
      </tr>
      
      <tr>
         <th>Description</th>
         <td>
           <input class="text" type="text" id="text-description" name="description" value="${poicategorymodify.description}">
         <c:if test="${err_poicategorymodify_description}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
         </td>
      </tr>
      
      <tr>
         <th>Est-il actif?</th>
         <td>
            <c:choose>
               <c:when test="${poicategorymodify.active eq 'true'}">
                  <input class="checkbox" type="checkbox" id="checkbox-active" name="active" value="${poicategorymodify.active}" checked>
               </c:when>
               <c:otherwise>
                  <input class="checkbox" type="checkbox" id="checkbox-active" name="active" value="${poicategorymodify.active}">
               </c:otherwise>
            </c:choose>
            <label for="checkbox-active"></label>
         </td>   
      </tr>
      
      <tr>
         <th>ParentId</th>
         <td>
            <input readonly class="text" type="text" id="text-parentId" name="parentId" value="${poicategorymodify.parentId}">
         </td>
      </tr>

   </tbody>
   
   </table>
   
   <div class="table bottom">
      <button id="button-cancel" onclick="document.location.href = '${baseURL}/poicategories'; return false;">
      	Annuler
      </button>
      
      <button id="button-save" onclick="submit()">
      	Enregistrer
      </button>
   </div>

</div>

</form>
</div>
</body>
</html>