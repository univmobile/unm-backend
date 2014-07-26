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

Bonjour !

<p>

Principal : ${user.uid}

<p>

Delegation : ${delegationUser.uid}

</div>
</body>
</html>