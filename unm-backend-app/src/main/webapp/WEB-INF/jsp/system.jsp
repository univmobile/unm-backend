<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html lang="fr" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Language" content="en">
<title>Administration d’UnivMobile</title>
<link type="text/css" rel="stylesheet" href="${baseURL}/css/backend.css">
<link type="text/css" rel="stylesheet" href="${baseURL}/css/jquery-ui-1.11.1-smoothness.css">
<script type="text/javascript" src="${baseURL}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${baseURL}/js/jquery-ui-1.11.1.min.js"></script>
<jsp:include page="js-adminMenu.h.jsp"/>
</head>
<body id="body-system" class="admin entered">

<jsp:include page="div-entered.h.jsp"/>

<div class="body">

<h1 title="Version ${buildInfo.appVersion}
Build ${buildInfo.buildDisplayName}
${buildInfo.buildId}
${buildInfo.gitCommitId}">
Administration d’UnivMobile
</h1>

<h2 class="homeMenu">Système</h2>

<table id="table-systemInfo">
<tbody>
<tr class="odd version">
	<th>Version</th>
	<td>${buildInfo.appVersion}</td>
</tr>
<tr class="even build">
	<th>Build</th>
	<td>${buildInfo.buildDisplayName} — ${buildInfo.buildId}</td>
</tr>
<tr class="odd gitCommit">
	<th>Git Commit</th> 
	<td>${buildInfo.gitCommitId}</td>
</tr>
<tr class="even baseURL">
	<th>Base URL</th> 
	<td>${baseURL}</td>
</tr>
<tr class="odd optionalJsonBaseURLs">
	<th>Optional JSON Base URLs</th> 
	<td>
		<c:forEach var="optionalJsonBaseURL" items="${systemInfo.optionalJsonBaseURLs}">
			${optionalJsonBaseURL}
			<br/>
		</c:forEach>
	</td>
</tr>
<tr class="even memory">
	<th>Memory</th> 
	<td>
		Free: ${systemInfo.freeMemory} —
		Total: ${systemInfo.totalMemory} —
		Max: ${systemInfo.maxMemory}
	</td>
</tr>
<tr class="odd dataDir">
	<th>Data Directory</th> 
	<td>${systemInfo.dataDir}
		<c:choose>
		<c:when test="${not empty systemInfo.dataDirError}">
			<br>
			<span class="error">${systemInfo.dataDirError}</span>
		</c:when>
		<c:otherwise>
			
		</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr class="even logFile">
	<th>Log File</th> 
	<td>${systemInfo.logFile}
		<c:choose>
		<c:when test="${not empty systemInfo.logFileError}">
			<br>
			<span class="error">${systemInfo.logFileError}</span>
		</c:when>
		<c:otherwise>
			— ${systemInfo.logFileLength}
		</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr class="odd dbUrl">
	<th>DataBase URL</th> 
	<td>${systemInfo.dbUrl}</td>
</tr>
<tr class="even tablePrefix">
	<th>TableName Prefix</th> 
	<td>${systemInfo.tablePrefix}</td>
</tr>
<tr class="odd dbTables">
	<th>DataBase Tables</th> 
	<td>
		<c:forEach var="table" items="${systemInfo.dbTables}">
			${table.name} — 
			<c:choose>
			<c:when test="${not empty table.error}">
				<span class="error">${table.error}</span>
			</c:when>
			<c:when test="${table.rowCount == 0}">
				0 row
			</c:when>
			<c:when test="${table.rowCount == 1}">
				one row
			</c:when>
			<c:otherwise>
				${table.rowCount} rows
			</c:otherwise>
			</c:choose>
			<br/>
		</c:forEach>
</tr>
<tr class="even monitoringGraphs">
	<th>Monitoring Graphs</th> 
	<td>
		<c:forEach var="monitoringGraph" items="${systemInfo.monitoringGraphs}">
			${monitoringGraph.url}
			<br/>
		</c:forEach>
</tr>
</tbody>
</table>

<ul class="homeMenu" id="ul-home-system">
	<li class="flaticon icon-file6">
		<a id="link-home-logqueue" href="${baseURL}/logqueue/">Historique<br>des actions</a>
	<li class="flaticon icon-graph5">
		<a id="link-home-stats" href="${baseURL}/stats/">Statistiques</a>
	<li class="flaticon icon-check6">
		<a id="link-home-monitoring" href="${baseURL}/monitoring/">Monitoring</a>
	<li class="flaticon icon-black56">
		<a id="link-home-logs" href="${baseURL}/logs/">Logs techniques</a>
	<li class="flaticon icon-down9">
		<a id="link-home-backups" href="${baseURL}/backups/">Sauvegardes (backups)</a>
</ul>

</div> <!-- end of div.body -->
</body>
</html>