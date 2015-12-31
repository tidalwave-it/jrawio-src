/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
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
