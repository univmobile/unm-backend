<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:j2ee="http://java.sun.com/xml/ns/j2ee">

<xsl:param name="dataDir" select="'/tmp/unm-backend/data'"/>
<xsl:param name="baseURL" select="'http://localhost:8080/unm-backend/'"/>

<!-- TODO code is duplicated in: unm-backend-app, unm-mobileweb-app -->

<xsl:template match="*">

	<xsl:copy>
	<xsl:copy-of select="@*"/>
	
		<xsl:apply-templates/>
	
	</xsl:copy>

</xsl:template>

<xsl:template match="j2ee:servlet
		[j2ee:servlet-name = 'BackendServlet']/j2ee:init-param
		[j2ee:param-name = 'dataDir']/j2ee:param-value">

	<xsl:copy>
	<xsl:copy-of select="@*"/>
	
		<xsl:value-of select="$dataDir"/>
	
	</xsl:copy>
	
</xsl:template>

<xsl:template match="j2ee:servlet
		[j2ee:servlet-name = 'BackendServlet']/j2ee:init-param
		[j2ee:param-name = 'baseURL']/j2ee:param-value">

	<xsl:copy>
	<xsl:copy-of select="@*"/>
	
		<xsl:value-of select="$baseURL"/>
	
	</xsl:copy>
	
</xsl:template>

</xsl:stylesheet>