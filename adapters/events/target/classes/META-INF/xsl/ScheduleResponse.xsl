<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:param name="mybatisResponse" />
    
    <xsl:template match="/">
		<ScheduleResponse xmlns="www.servicemix.edu/schedule/data">
			<code>0</code>
			<status>Success</status>
			<ArrayOfEvents>
				<response><xsl:value-of select="$mybatisResponse"></xsl:value-of></response>
			</ArrayOfEvents>
		</ScheduleResponse>
	</xsl:template>
</xsl:stylesheet>