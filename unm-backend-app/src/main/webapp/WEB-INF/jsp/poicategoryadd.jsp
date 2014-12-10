<!-- Author: Mauricio -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Ajouter une catégorie</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<style type="text/css">
td span.error {
	margin-left: 0.5em;
}

#body-poicategoryadd label {
	xfont-family: 'Lucida Grande';
	font-size: x-small;
}

label.checkbox-parentUid {
	font-weight: bold;
}
</style>

<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>

<jsp:include page="js-adminMenu.h.jsp"/>

</head>
<body id="body-poicategoryadd" class="entered">

<jsp:include page="div-entered_modal.h.jsp"/>

<div class="body">
<form action="${baseURL}/poicategoriesadd" method="POST">

<h1   title="Version ${buildInfo.appVersion}
      Build ${buildInfo.buildDisplayName}
      ${buildInfo.buildId}
      ${buildInfo.gitCommitId}">
      Administration d’UnivMobile
</h1>

<div class="div-poicategoryadd">

   <c:if test="${err_duplicateUid}">
   <div class="error">
   	ERREUR — une catégorie avec ce uid = ${poicategoryadd.uid} existe déjà en base
   </div>
   </c:if>
   <c:if test="${err_incorrectFields}">
   <div class="error">
   	ERREUR — des champs sont incorrects
   </div>
   </c:if>
   
   
   <h2>L'ajout d'une catégorie</h2>
   
   <table>
   
   <tbody>
      <tr>
         <th>Uid</th>
         <td>
            <input class="text" type="text" id="text-uid" name="uid" value="${poicategoryadd.uid}">
         <c:if test="${err_poicategoryadd_uid}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
         </td>
      </tr>
      
      <tr>
         <th>Nom</th>
         <td>
            <input class="text" type="text" id="text-name" name="name" value="${poicategoryadd.name}">
         <c:if test="${err_poicategoryadd_name}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
         </td>
      </tr>
      
      <tr>
         <th>Description</th>
         <td>
           <input class="text" type="text" id="text-description" name="description" value="${poicategoryadd.description}">
         </td>
      </tr>
      
      <tr>
         <th>Est-il actif?</th>
         <td>
            <input class="checkbox" type="checkbox" id="checkbox-active" name="active" value="yes">
            <label for="checkbox-active"></label>
         </td>   
      </tr>
      
      <tr>
         <th>ParentUid</th>
         <td>
         <select id="select-parentUid" name="parentUid">
            <option value="(aucune)">(aucune)</option>
            <c:forEach var="pc" items="${poicategories}">
               <option value="${pc.uid}">
                  ${pc.uid}
               </option>
            </c:forEach>
         </select>   
         </td>
      </tr>
      
      <tr>
         <th>ExternalUid</th>
         <td>
           <input class="text" type="text" id="text-externalUid" name="externalUid" value="${poicategoryadd.externalUid}">
         <c:if test="${err_poicategoryadd_externalUid}">
            <span class="error" title="Le champ est mal formé">Incorrect</span>
         </c:if>
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