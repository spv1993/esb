<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output version="1.0" method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:param name="mybatisResponse" />

    <xsl:template match="/">
        <GetHashesResponse xmlns="www.servicemix.edu/schedule/data">
            <code>0</code>
            <status>Success</status>
            <Hashes>
                <EventId>

                </EventId>
            </Hashes>
        </GetHashesResponse>
    </xsl:template>
</xsl:stylesheet>