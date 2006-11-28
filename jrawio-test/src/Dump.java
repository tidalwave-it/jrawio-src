/*******************************************************************************
 * 
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ----------------------------------------------------------
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.dev.java.net
 * 
 *******************************************************************************
 * 
 * MIT License notice
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 * 
 *******************************************************************************
 * 
 * $Id: Dump.java,v 1.2 2006/02/08 22:31:57 fabriziogiudici Exp $
 *  
 ******************************************************************************/
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Dump
  {
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    private static DocumentBuilder builder;
    
    private static Transformer identityTransformer;
    
    private static Transformer htmlTransformer;
    
    private static File targetFolder = new File("../samples");
    
    private static File htmlFolder = new File(targetFolder, "html");

    private static File xmlFolder = new File(targetFolder, "xml");
    
    private static void dump (IIOMetadata metadata, File file) throws Exception
      {
        String name = metadata.getNativeMetadataFormatName();

        if (name != null)
          {
            DOMSource source = new DOMSource(metadata.getAsTree(name));
            StreamResult result = new StreamResult(new File(xmlFolder, file.getName() + ".xml"));
            identityTransformer.transform(source, result);                
            result = new StreamResult(new File(htmlFolder,  file.getName() + ".html"));
            htmlTransformer.transform(source, result);
          }
      }
    
    public static void process (File file) throws Exception
      {
        System.err.println("Processing " + file);
        ImageInputStream is = ImageIO.createImageInputStream(file);
        Iterator i = ImageIO.getImageReaders(is);

        if (!i.hasNext())
          {
            System.out.println("Cannot load the specified file: " + file);
            return;
          }

        ImageReader imageReader = (ImageReader)i.next();
        imageReader.setInput(is);

        IIOMetadata metadata = imageReader.getImageMetadata(0);
        dump(metadata, file);
      }

    public static void main (String[] args) throws Exception
      {
        builder = factory.newDocumentBuilder();
        Source xslt = new StreamSource("../samples/xslt/MetadataToTable.xslt");
        TransformerFactory tFactory = TransformerFactory.newInstance();
        htmlTransformer = tFactory.newTransformer(xslt);
        identityTransformer = tFactory.newTransformer();
        String testPhotos = System.getenv("TEST_PHOTOS");
        
        BufferedReader br = new BufferedReader(new FileReader("files"));
        
        for (;;)
          {
            String s = br.readLine ();
            
            if (s == null)
              {
                break;
              }
            
            if (!s.startsWith("/"))
              {
                s = testPhotos + "/" + s;  
              }
           
try{ 
            process(new File(s));
}
catch (Exception e)
{
e.printStackTrace();
}
          }
      }      
  }
