<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile — Logs techniques</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-logs" class="entered">

<jsp:include page="div-entered.h.jsp"/>

<div class="body">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile — Logs techniques
</h1>

<div id="div-logs-form">
<form action="${baseURL}/logs" method="GET">
Limit:
<input type="text" id="text-limit" name="limit" value="${logsInfo.limit}">
<input type="hidden" id="hidden-undesc" name="u">
<input type="checkbox" name="desc" value=""
	<c:if test="${logsInfo.desc}">checked</c:if>
	id="checkbox-mostRecentFirst">
<label for="checkbox-mostRecentFirst">Les plus récents en premier</label>
<button id="button-refresh">
	Rafraîchir
</button>
</form>
</div>

<hr>

<div id="div-logsInfo">
Total lines: ${logsInfo.total} — ${logsInfo.logFile} — ${logsInfo.logFileLength}
</div>

<div id="div-logs">

<pre><c:if test="${logsInfo.total gt logsInfo.lineCount and not logsInfo.desc}">...
</c:if><c:out value="${logs}"/><c:if test="${logsInfo.total gt logsInfo.lineCount and logsInfo.desc}">
...</c:if></pre>

</div> <!-- end of #div-logs -->

</div> <!-- end of div.body -->
</body>
</html>