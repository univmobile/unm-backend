<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@
	page contentType="text/plain"%><%@ 
	page pageEncoding="UTF-8"%><c:choose><c:when
	test="${not empty commentId}"
>comment.id: ${commentId}</c:when><c:otherwise
>OK</c:otherwise></c:choose>
