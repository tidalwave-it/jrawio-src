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
 * $Id: Main.java,v 1.5 2006/02/12 19:45:34 fabriziogiudici Exp $
 * 
 ******************************************************************************/
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/*******************************************************************************
 *
 * A quick and dirty test program.
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: Main.java,v 1.5 2006/02/12 19:45:34 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class Main
  {
    private static String metadataToString (IIOMetadata metadata)
      {
        try
          {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            String[] names = metadata.getMetadataFormatNames();

            if (names == null)
              {
                return "n.a.";
              }

            for (int i = 0; i < names.length; i++)
              {
                pw.println("<!-- ========================= " + names[i] + " -->");

                Node node = metadata.getAsTree(names[i]);
                XMLPrinter xp = new XMLPrinter(node);
                xp.print(pw);
                pw.print("\n\n");
                break; // FIXME:
              }

            pw.flush();

            return sw.toString();
          }

        catch (RuntimeException e)
          {
            return e.toString();
          }
      }

    public static void main (String[] args) throws IOException
      {
        boolean dump = false;

        File file = null;
        int arg = 0;

        if (args.length > 0)
          {
            if (args[0].equals("-dump"))
              {
                dump = true;
                arg++;
              }
          }

        if (arg >= args.length)
          {
            JFileChooser fileChooser = new JFileChooser();
            int r = fileChooser.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION)
              {
                file = fileChooser.getSelectedFile();
              }

            if (file == null)
              {
                System.exit(0);
              }
          }

        else
          {
            file = new File(args[arg]);
          }

        if (!file.exists())
          {
            System.out.println("The specified file does not exist: " + file);
            System.exit(0);
          }

        ImageInputStream is = ImageIO.createImageInputStream(file);
        Iterator i = ImageIO.getImageReaders(is);

        if (!i.hasNext())
          {
            System.out.println("Cannot load the specified file");
            System.exit(0);
          }

        ImageReader imageReader = (ImageReader)i.next();
        imageReader.setInput(is);

        BufferedImage image = imageReader.read(0);
        IIOMetadata metadata = imageReader.getImageMetadata(0);

        JFrame frame = new JFrame("jrawio Test: " + file.getAbsolutePath());
        JTabbedPane tabbedPane = new JTabbedPane();

        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane1.getViewport().add(new ImageRenderer(image));
        tabbedPane.addTab("image", scrollPane1);

        if (imageReader.hasThumbnails(0))
          {
            for (int j = 0; j < imageReader.getNumThumbnails(0); j++)
              {
                BufferedImage thumbnail = imageReader.readThumbnail(0, j);
                JScrollPane scrollPane1a = new JScrollPane();
                scrollPane1a.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane1a.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane1a.getViewport().add(new ImageRenderer(thumbnail));
                tabbedPane.addTab("thumbnail #" + (j + 1), scrollPane1a);
              }
          }

        JScrollPane scrollPane3 = new JScrollPane();
        JTextArea textArea2 = new JTextArea();
        textArea2.setText(metadataToString(metadata));
        textArea2.setEditable(false);
        scrollPane3.getViewport().add(textArea2);
        tabbedPane.addTab("metadata", scrollPane3);

        frame.getContentPane().add(tabbedPane);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
  }

/*******************************************************************************
 *
 *
 ******************************************************************************/
class XMLPrinter
  {
    private Node node;

    public XMLPrinter (Node node)
      {
        this.node = node;
      }

    public void print (PrintWriter pw)
      {
        print(pw, "", node);
      }

    private void print (PrintWriter pw,
                        String pfx,
                        Node node)
      {
        pw.print(pfx + "<" + node.getNodeName());

        NamedNodeMap map = node.getAttributes();

        for (int i = 0; i < map.getLength(); i++)
          {
            Node attr = map.item(i);
            pw.print(" " + attr.getNodeName() + "='" + attr.getNodeValue() + "'");
          }

        Node ch = node.getFirstChild();

        if (ch == null)
          {
            pw.println("/>");
          }

        else
          {
            pw.println(">");

            while (ch != null)
              {
                print(pw, pfx + "  ", ch);
                ch = ch.getNextSibling();
              }

            pw.print(pfx + "</" + node.getNodeName() + ">\n");
          }
      }
  }

/*******************************************************************************
 *
 *
 ******************************************************************************/
class ImageRenderer extends JComponent
  {
    private BufferedImage image;

    private BufferedImage compatibleImage;

    public ImageRenderer (BufferedImage image)
      {
        this.image = image;
      }

    public Dimension getMinimumSize ()
      {
        return new Dimension(image.getWidth(), image.getHeight());
      }

    public Dimension getPreferredSize ()
      {
        return getMinimumSize();
      }

    public void paintComponent (Graphics g)
      {
        Graphics2D g2 = (Graphics2D)g;

        if (compatibleImage == null)
          {
            compatibleImage = (BufferedImage)createImage(image.getWidth(), image.getHeight());

            Graphics2D g3 = (Graphics2D)compatibleImage.getGraphics();

            try
              {
                g3.drawImage(image, null, 0, 0);
              }

            finally
              {
                g3.dispose();
              }
          }

        g2.drawImage(compatibleImage, null, 0, 0);
      }
  }
