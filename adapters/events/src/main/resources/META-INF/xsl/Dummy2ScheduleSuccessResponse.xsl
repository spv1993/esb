<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output version="1.0" method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:template match="/">
		<ScheduleResponse xmlns="www.servicemix.edu/schedule/data">
			<code>0</code>
			<status>Success</status>
			<ArrayOfEvents>
				<Event>
					<user>String</user>
					<name>String</name>
					<start>2001-12-17T09:30:47.0Z</start>
					<duration>P1Y2M3DT10H30M0S</duration>
					<description>String</description>
					<type>Офис</type>
				</Event>
				<Event>
					<user>String</user>
					<name>String</name>
					<start>2001-12-17T09:30:47.0Z</start>
					<duration>P1Y2M3DT10H30M0S</duration>
					<description>String</description>
					<type>Офис</type>
				</Event>
				<Event>
					<user>String</user>
					<name>String</name>
					<start>2001-12-17T09:30:47.0Z</start>
					<duration>P1Y2M3DT10H30M0S</duration>
					<description>String</description>
					<type>Офис</type>
				</Event>
				<Event>
					<user>String</user>
					<name>String</name>
					<start>2001-12-17T09:30:47.0Z</start>
					<duration>P1Y2M3DT10H30M0S</duration>
					<description>String</description>
					<type>Офис</type>
				</Event>
			</ArrayOfEvents>
		</ScheduleResponse>
	</xsl:template>
</xsl:stylesheet>