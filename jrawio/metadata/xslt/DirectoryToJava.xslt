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

$Id: DirectoryToJava.xslt,v 1.2 2006/02/13 22:41:17 fabriziogiudici Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml">
    <xsl:output omit-xml-declaration="yes"/>
    
    <xsl:variable name="lcletters"> abcdefghijklmnopqrstuvwxyz(?).,-</xsl:variable>
    <xsl:variable name="xcletters"> ABCDEFGHIJKLMNOPQRSTUVWXYZCHK.,-</xsl:variable>
    <xsl:variable name="ucletters">_ABCDEFGHIJKLMNOPQRSTUVWXYZCHK___</xsl:variable>
     
    <xsl:variable name="l1"> abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ(?)</xsl:variable>
    <xsl:variable name="l2"> abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZCHK</xsl:variable>
     
    <xsl:param name="serialVersionUID" select="serialVersionUID"/>
    <xsl:param name="package" select="package"/>

    <xsl:template match="directory">
        package <xsl:value-of select='$package'/>;

        import it.tidalwave.imageio.raw.Directory;
        import it.tidalwave.imageio.raw.TagRational;
        import it.tidalwave.imageio.raw.TagRegistry;
        import it.tidalwave.imageio.tiff.IFDSupport;
 
        class <xsl:value-of select='@name'/>MakerNoteSupport extends IFDSupport
        {
        private final static long serialVersionUID = <xsl:value-of select='$serialVersionUID'/>;
        public final static TagRegistry REGISTRY = TagRegistry.getRegistry("<xsl:value-of select='@name'/>");
    
        public <xsl:value-of select='@name'/>MakerNoteSupport()
        {
        super(REGISTRY);
        }

        <xsl:apply-templates select="tag"/>
        }
    </xsl:template>

    <xsl:template match="tag">
        <xsl:variable name='constant' select='translate(name,$lcletters,$ucletters)'/>
        <xsl:variable name='property'>
            <xsl:call-template name="removeSpaces">
                <xsl:with-param name="string" select="translate(name,$l1,$l2)" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="typeUC">
            <xsl:value-of select='translate(substring(type,1,1),$lcletters,$ucletters)'/><xsl:value-of select='substring(type,2)'/>
        </xsl:variable>

        <xsl:variable name="scalarReturnType">
            <xsl:choose>
                <xsl:when test="type='ascii'">String</xsl:when>
                <xsl:when test="type='rational'">TagRational</xsl:when>
                <xsl:when test="type='undefined'">byte</xsl:when>
                <xsl:when test="type='byte'">int</xsl:when>
                <xsl:when test="type='short'">int</xsl:when>
                <xsl:when test="type='signed short'">int</xsl:when>
                <xsl:when test="type='long'">int</xsl:when>
                <xsl:otherwise><xsl:value-of select='type'/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name='returnType'>
            <xsl:value-of select='$scalarReturnType'/>
            <xsl:choose>
                <xsl:when test="length='1'"></xsl:when>
                <xsl:when test="type='ascii'"></xsl:when>
                <xsl:otherwise>[]</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
         
        <xsl:variable name='typeGetter'>
            <xsl:choose>
                <xsl:when test="type='ascii'">String</xsl:when>
                <xsl:when test="type='undefined'">Byte</xsl:when>
                <xsl:when test="type='int'">Integer</xsl:when>
                <xsl:when test="type='short'">Integer</xsl:when>
                <xsl:when test="type='signed short'">Integer</xsl:when>
                <xsl:when test="type='long'">Integer</xsl:when>
                <xsl:otherwise><xsl:value-of select='$typeUC'/></xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="length='1'"></xsl:when>
                <xsl:when test="type='ascii'"></xsl:when>
                <xsl:otherwise>s</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="enumeration">
            public static class <xsl:value-of select='$property'/> extends Directory.Enumeration
            {
            private <xsl:value-of select='$property'/> (<xsl:value-of select='$scalarReturnType'/> value, String name)
            {
            super(value, name);
            }
            
            private <xsl:value-of select='$property'/> (<xsl:value-of select='$scalarReturnType'/>[] value, String name)
            {
            super(value, name);
            }
           
            <xsl:for-each select="enumeration/item">
                <xsl:variable name='literalValue'>
                    <xsl:choose>
                        <xsl:when test="../../type='ascii'">"<xsl:value-of select='value'/>"</xsl:when>
                        <xsl:otherwise><xsl:value-of select='value'/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name='enum'>_<xsl:value-of select='translate(value,$lcletters,$ucletters)'/></xsl:variable>
                public final static <xsl:value-of select='$property'/><xsl:text> </xsl:text><xsl:value-of select='$enum'/> = new <xsl:value-of select='$property'/>(<xsl:value-of select='$literalValue'/>, "<xsl:value-of select='value'/>");
            </xsl:for-each>
             
            public static <xsl:value-of select='$property'/> getInstance(<xsl:value-of select='$returnType'/> value)
            {
            <xsl:for-each select="enumeration/item">
                <xsl:variable name='literalValue'>
                    <xsl:choose>
                        <xsl:when test="../../type='ascii'">"<xsl:value-of select='value'/>"</xsl:when>
                        <xsl:otherwise><xsl:value-of select='value'/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name='enum'>_<xsl:value-of select='translate(value,$lcletters,$ucletters)'/></xsl:variable>
                if (equals(value, <xsl:value-of select='$literalValue'/>)) return <xsl:value-of select='$enum'/>;
            </xsl:for-each>

            return new <xsl:value-of select='$property'/>(value, "unknown");
            }
            }
        </xsl:if>
  
        /** Field (#<xsl:value-of select='code'/>) */
        public final static Object <xsl:value-of select='$constant'/> = REGISTRY.register(<xsl:value-of select='code'/>, "<xsl:value-of select='name'/>");
        
        /** Returns true if the tag <xsl:value-of select='name'/> is contained in this IFD. */
        public boolean is<xsl:value-of select='$property'/>Available()
        {
        return containsTag(<xsl:value-of select='$constant'/>);
        }
  
        /** Returns the value of the tag <xsl:value-of select='name'/>. */
        <xsl:choose>
            <xsl:when test="enumeration">
                public <xsl:value-of select='$property'/> get<xsl:value-of select='$property'/>() 
                {
                return <xsl:value-of select='$property'/>.getInstance(get<xsl:value-of select='$typeGetter'/>(<xsl:value-of select='$constant'/>));
                }
            </xsl:when>
            <xsl:otherwise>
                public <xsl:value-of select='$returnType'/> get<xsl:value-of select='$property'/>() 
                {
                return get<xsl:value-of select='$typeGetter'/>(<xsl:value-of select='$constant'/>);
                }
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
     
    <xsl:template name="removeSpaces">
        <xsl:param name="string" />
        <xsl:choose>
            <xsl:when test="contains($string, ' ')">
                <xsl:value-of select="substring-before($string, ' ')" />
                <xsl:call-template name="removeSpaces">
                    <xsl:with-param name="string" select="substring-after($string, ' ')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$string" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>     
          
</xsl:stylesheet>
