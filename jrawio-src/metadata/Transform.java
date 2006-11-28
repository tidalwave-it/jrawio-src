import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*; 
import javax.xml.transform.stream.*; 
import java.io.*; 
import org.w3c.dom.*;

public class Transform
  {
    public static void main (String[] args)
      throws Exception
      {
        File f = new File(args[0]);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(f);

        Source xslt = new StreamSource(new File(args[1]));
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(xslt);
      
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result); 
      }
  }
