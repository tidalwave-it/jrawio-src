<?xml version="1.0" encoding="UTF-8"?>
<!--

jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
==========================================================

Copyright (C) 2003-2008 by Fabrizio Giudici
Project home page: http://jrawio.dev.java.net

=============================================================================

MIT License notice

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

=============================================================================

$Id: DirectoryToText.xslt,v 1.1 2006/02/09 12:17:05 fabriziogiudici Exp $
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml">
 
    <xsl:template match="directory">
        <xsl:apply-templates select="tag"/>
    </xsl:template>

    <xsl:template match="tag">
        <h5>Tag #<xsl:value-of select="code"/> - <xsl:value-of select="name"/></h5>
        <b>Type: </b><xsl:value-of select="type"/>
        <xsl:if test="type!='ascii'">[<xsl:value-of select="length"/>]</xsl:if><br/>
        <b>Example: </b><xsl:value-of select="example"/><br/>
        <b>Description: </b><xsl:value-of select="description"/>
        <xsl:apply-templates select="enumeration"/>
    </xsl:template>
     
    <xsl:template match="enumeration">
        Allowed values are:
        <ul>
            <xsl:apply-templates select="item"/>
        </ul>
    </xsl:template>

    <xsl:template match="item">
        <li><b><xsl:value-of select="value"/></b>: <xsl:value-of select="description"/></li>
    </xsl:template>

</xsl:stylesheet>
