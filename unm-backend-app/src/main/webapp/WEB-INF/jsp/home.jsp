<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Administration d’UnivMobile</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/styles.css">
</head>
<body id="body-home">
<div class="body">

<h1>Administration d’UnivMobile</h1>

<div id="div-myself">
<form action="${baseURL}/" method="POST">

<h2>Compte principal</h2>

Votre connexion est authentifiée en tant que
${displayName}
(uid = ${uid})

<p>

<input type="hidden" name="myself">

<button id="button-myself" onclik="submit()">
	Entrer avec votre compte principal
		(${uid})
</button>

</form>
</div>

<div id="div-delegation">
<c:if test="${errorUnknownDelegationUid}">
<div class="error">
	ERREUR — 
	<span class="message">
	le compte uid=${delegationUid} n’existe pas en base de données
	</span>
</div>
</c:if>
<div class="layout">

<form action="${baseURL}/" method="POST">

	<h2>Compte délégué</h2>
	
	Entrer en tant que uid = 
	<input type="text" id="text-delegationUid" name="delegationUid"
		value="${delegationUid}"/>

	<p>

	Mot de passe de délégation
	<input type="password" id="password-delegationPassword" name="delegationPassword"/>

	<p>
		
	<button id="button-delegation" onclik="submit()">
		Entrer en tant que délégué
	</button>
	 
</form>
</div>
</div>

</div>
</body>
</html>