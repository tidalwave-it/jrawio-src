 <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">
 
   <xsl:template match="it_tidalwave_imageio_tiff_image_1.0">
     <table border="1" cellspacing="0" cellpadding="2"> 
       <xsl:apply-templates select="TIFFIFD"/>
     </table> 
   </xsl:template>

   <xsl:template match="TIFFIFD">
     <tr><td colspan="4">
       <b>
         <xsl:value-of select="@name"/>
         start=<xsl:value-of select="@start"/>
         end=<xsl:value-of select="@end"/>
         parent=<xsl:value-of select="@parentName"/>,<xsl:value-of select="@parentNumber"/>
       </b>
     </td></tr>
     <xsl:for-each select="TIFFField">
       <tr style="font-size: 80%">
         <td><xsl:value-of select="@number"/></td>
         <td><xsl:value-of select="@name"/></td>
         <xsl:apply-templates/>
       </tr>
     </xsl:for-each>
     <xsl:apply-templates select="TIFFIFD"/>
   </xsl:template>
 
   <xsl:template match="TIFFBytes">
        <td>byte</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFShorts">
        <td>short</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFSShorts">
        <td>sshort</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFLongs">
        <td>long</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFRationals">
        <td>rational</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFSRationals">
        <td>srational</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFAsciis">
        <td>ascii</td>
        <td><xsl:apply-templates/></td>
   </xsl:template> 

   <xsl:template match="TIFFUndefined">
        <td>ascii</td>
        <td><xsl:value-of select="substring(@value, 0, 40)"/></td>         
   </xsl:template> 

   <xsl:template match="TIFFByte">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFShort">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFSShort">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFLong">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFRational">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFSRational">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

   <xsl:template match="TIFFAscii">
        <xsl:value-of select="@value"/>
        <xsl:text> </xsl:text>
   </xsl:template> 

 </xsl:stylesheet>
