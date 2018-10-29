<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="EXCEPTION_MESSAGE"/>
    
    <xsl:template match="/">
		<ScheduleResponse xmlns="www.servicemix.edu/schedule/data">
			<code>-1</code>
			<status><xsl:value-of select="$EXCEPTION_MESSAGE"/></status>
		</ScheduleResponse>
	</xsl:template>
</xsl:stylesheet>