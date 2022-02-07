<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:tns="urn://gosuslugi/sig-contract/1.0.2">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:param name="mnemonic"/>
	<xsl:param name="OID"/>
	<xsl:param name="descDoc"/>
	<xsl:param name="documentId"/>
	<xsl:param name="documentPath"/>
	<xsl:param name="documentDescription"/>
	<xsl:param name="id"/>
	<xsl:param name="contractUUID"/>
	<xsl:param name="signUUID"/>
	<xsl:template match="/">
		<tns:RequestSigContract>
			<xsl:attribute name="Id"><xsl:value-of select="$id"/></xsl:attribute>
			<xsl:attribute name="timestamp"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
			<xsl:attribute name="routeNumber"><xsl:value-of select="$mnemonic"/></xsl:attribute>
			<tns:OID>
				<xsl:value-of select="$OID"/>
			</tns:OID>
			<tns:signExp>2025-12-31T00:00:00Z</tns:signExp>
			<tns:descDoc>
				<xsl:value-of select="$descDoc"/>
			</tns:descDoc>
			<tns:Contracts>
				<tns:Contract>
					<tns:Document>
						<xsl:attribute name="mimeType">application/pdf</xsl:attribute>
						<xsl:attribute name="uuid"><xsl:value-of select="$contractUUID"/></xsl:attribute>
						<xsl:attribute name="description"><xsl:value-of select="$documentDescription"/></xsl:attribute>
						<xsl:attribute name="docId"><xsl:value-of select="$documentId"/></xsl:attribute>
					</tns:Document>
					<tns:Signature>
						<xsl:attribute name="mimeType">application/sig</xsl:attribute>
						<xsl:attribute name="uuid"><xsl:value-of select="$signUUID"/></xsl:attribute>
						<xsl:attribute name="description">Подпись оператора связи</xsl:attribute>
						<xsl:attribute name="docId"><xsl:value-of select="$documentId"/></xsl:attribute>
					</tns:Signature>
				</tns:Contract>
			</tns:Contracts>
			<tns:Backlink>https://volnamobile.ru/</tns:Backlink>
		</tns:RequestSigContract>
	</xsl:template>
</xsl:stylesheet>
