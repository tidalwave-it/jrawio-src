/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: TIFFMetadataSupport.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import it.tidalwave.imageio.raw.HeaderProcessor;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.raw.TagRegistry;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: TIFFMetadataSupport.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class TIFFMetadataSupport extends RAWMetadataSupport
  {
             
    private static final String[] ORIENTATION_NAMES = {
      null, "Normal", "FlipH", "Rotate180", "FlipV", "FlipHRotate90", "Rotate270", "FlipVRotate90", "Rotate90" };

    private static final String[] COLOR_SPACE_NAMES = {
      "GRAY", "GRAY", "RGB", "RGB", "GRAY", null, "YCbCr", "Lab", "Lab" };

    private static final String[] TYPE_NAMES = {
      null, "Byte", "Ascii", "Short", "Long", "Rational", "SByte", "Undefined", "SShort", "SLong", "SRational",
      "Float", "Double", "IFDPointer" };

    private IFD primaryIFD;

    private IFD exifIFD;

    protected IFD rasterIFD;
    
    protected List thumbnailHelperList = new ArrayList();
        
    /*******************************************************************************
     * 
     * @param primaryIFD
     * @param nativeFormatName
     * 
     *******************************************************************************/
    public TIFFMetadataSupport (Directory primaryDirectory, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryDirectory, "it_tidalwave_imageio_tiff_image_1.0", headerProcessor);
        primaryIFD = (IFD)primaryDirectory;

        if (primaryDirectory != null)
          {
            exifIFD = (IFD)primaryDirectory.getNamedDirectory(IFD.EXIF_NAME);

            if (exifIFD == null)
              {
                for (Iterator i = primaryDirectory.subDirectories(); i.hasNext();)
                  {
                    Directory directory = (Directory)i.next();
                    exifIFD = (IFD)directory.getNamedDirectory(IFD.EXIF_NAME);

                    if (exifIFD != null)
                      {
                        break;
                      }
                  }
              }
          }

        if (primaryDirectory != null)
          {
            List ifdList = new ArrayList();
        
            for (IFD ifd = primaryIFD; ifd != null; ifd = (IFD)ifd.getNextDirectory())
              {
                ifdList.add(ifd);
              }
        
            for (Iterator i = primaryIFD.subDirectories(); i.hasNext(); )
              {
                ifdList.add(i.next());
              }
        
            for (Iterator i = ifdList.iterator(); i.hasNext(); )
              {
                IFD ifd = (IFD)i.next();
            
                if (isRasterIFD(ifd))
                  {
                    rasterIFD = ifd;  
                  }
            
                if (isThumbnailIFD(ifd)) // no else, sometimes a single IFD will be both
                  {
                    thumbnailHelperList.add(new ThumbnailHelper(iis, ifd));
                  }
              }        
          }        
        
        postInit(iis);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    protected void postInit (RAWImageInputStream iis)
      {
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getWidth()
      {
        return rasterIFD.getImageWidth();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getHeight()
      {
        return rasterIFD.getImageLength();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getThumbnailWidth (int thumbnailIndex)
      {
        return ((ThumbnailHelper)thumbnailHelperList.get(thumbnailIndex)).getWidth();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getThumbnailHeight (int thumbnailIndex)
      {
        return ((ThumbnailHelper)thumbnailHelperList.get(thumbnailIndex)).getHeight();
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public ThumbnailHelper[] getThumbnailHelper ()
      {
        return (ThumbnailHelper[])thumbnailHelperList.toArray(new ThumbnailHelper[0]);
      }

    /*******************************************************************************
     *
     * Gets the primary IFD.
     * 
     * @return the primary IFD.
     * 
     ******************************************************************************/
    public IFD getPrimaryIFD ()
      {
        return primaryIFD;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public IFD getRasterIFD()
      {
        return rasterIFD;
      }
    
    /*******************************************************************************
     * 
     * Gets the EXIF IFD.
     * 
     * @return the EXIF IFD
     * 
     *******************************************************************************/
    public IFD getExifIFD ()
      {
        return exifIFD;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public Directory getMakerNote ()
      {
        IFD exifIFD = getExifIFD();

        return (exifIFD == null) ? null : exifIFD.getNamedDirectory(IFD.MAKER_NOTE_NAME);
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public IIOMetadataNode getStandardDimensionNode ()
      {
        IIOMetadataNode dimensionNode = new IIOMetadataNode("Dimension");
        IFD resolutionIFD = getPrimaryIFD();
        IFD orientationIFD = getPrimaryIFD();

        if (resolutionIFD.isXResolutionAvailable() && resolutionIFD.isYResolutionAvailable())
          {
            TagRational xres = resolutionIFD.getXResolution();
            TagRational yres = resolutionIFD.getYResolution();

            IIOMetadataNode node = new IIOMetadataNode("PixelAspectRatio");
            float ratio = xres.floatValue() / yres.floatValue();
            node.setAttribute("value", Float.toString(ratio));
            dimensionNode.appendChild(node);

            boolean gotPixelSize = false;

            if (resolutionIFD.isResolutionUnitAvailable())
              {
                int resolutionUnit = resolutionIFD.getResolutionUnit().intValue();

                if (resolutionUnit == 3)
                  {
                    gotPixelSize = true;
                  }

                else if (resolutionUnit == 2)
                  {
                    if (xres != null)
                      {
                        xres.multiply(254, 100);
                      }

                    if (yres != null)
                      {
                        yres.multiply(254, 100);
                      }

                    gotPixelSize = true;
                  }
              }

            if (gotPixelSize)
              {
                if (xres != null)
                  {
                    float horizontalPixelSize = 10f / xres.floatValue();
                    addNode(dimensionNode, "HorizontalPixelSize", Float.toString(horizontalPixelSize));
                  }

                if (yres != null)
                  {
                    float verticalPixelSize = 10f / yres.floatValue();
                    addNode(dimensionNode, "VerticalPixelSize", Float.toString(verticalPixelSize));
                  }
              }
          }

        if (orientationIFD.isOrientationAvailable())
          {
            int o = orientationIFD.getOrientation().intValue();

            if ((o >= 0) && (o < ORIENTATION_NAMES.length))
              {
                addNode(dimensionNode, "ImageOrientation", ORIENTATION_NAMES[o]);
              }
          }

        addNode(dimensionNode, "HorizontalScreenSize", Integer.toString(getWidth()));
        addNode(dimensionNode, "VerticalScreenSize", Integer.toString(getHeight()));

        return dimensionNode;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public IIOMetadataNode getStandardChromaNode ()
      {
        IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
        IIOMetadataNode node = null;

        if (primaryIFD.isPhotometricInterpretationAvailable())
          {
            int photometricInterpretation = primaryIFD.getPhotometricInterpretation().intValue();

            if ((photometricInterpretation > 0) && (photometricInterpretation < COLOR_SPACE_NAMES.length))
              {
                node = new IIOMetadataNode("ColorSpaceType");
                node.setAttribute("name", COLOR_SPACE_NAMES[photometricInterpretation]);
                chroma_node.appendChild(node);
              }

            node = new IIOMetadataNode("BlackIsZero");
            node.setAttribute("value", (photometricInterpretation != 0) ? "TRUE" : "FALSE");
            chroma_node.appendChild(node);
          }

        if (primaryIFD.isBitsPerSampleAvailable())
          {
            int numChannels = primaryIFD.getBitsPerSample().length;
            node = new IIOMetadataNode("NumChannels");
            node.setAttribute("value", Integer.toString(numChannels));
            chroma_node.appendChild(node);
          }

        return chroma_node;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public IIOMetadataNode getStandardDocumentNode ()
      {
        IIOMetadataNode document_node = new IIOMetadataNode("Document");
        IIOMetadataNode node = null;

        if (primaryIFD.isDateTimeAvailable())
          {
            String s = primaryIFD.getDateTime();
            String[] st = s.split("[:. ]");
            node = new IIOMetadataNode("ImageCreationTime");
            node.setAttribute("year", st[0]);
            node.setAttribute("month", st[1]);
            node.setAttribute("day", st[2]);
            node.setAttribute("hour", st[3]);
            node.setAttribute("minute", st[4]);

            if (st.length > 5)
              {
                node.setAttribute("second", st[5]);
              }

            document_node.appendChild(node);
          }

        return document_node;
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public Node getNativeNode (TIFFTag field, TagRegistry registry)
      {
        IIOMetadataNode node = new IIOMetadataNode("TIFFField");
        node.setAttribute("number", Integer.toString(field.getCode()));
        String name = registry.getTagName(field.getCode());

        if (name != null)
          {
            node.setAttribute("name", name);
          }

        int count = field.getValuesCount();
        int type = field.getType();
        IIOMetadataNode child = new IIOMetadataNode("TIFF" + TYPE_NAMES[type] + "s");

        if (type == TIFFTag.TYPE_UNDEFINED)
          {
            child = new IIOMetadataNode("TIFFUndefined");

            byte[] data = field.getByteValues();
            StringBuffer sb = new StringBuffer(count * 4);

            for (int j = 0; j < count; j++)
              {
                sb.append(Integer.toString(data[j] & 0xff));

                if (j < (count - 1))
                  {
                    sb.append(",");
                  }
              }

            child.setAttribute("value", sb.toString());
          }

        else
          {
            for (int i = 0; i < count; i++)
              {
                IIOMetadataNode cchild = new IIOMetadataNode("TIFF" + TYPE_NAMES[type]);

                switch (type)
                  {
                    case TIFFTag.TYPE_BYTE:
                    case TIFFTag.TYPE_SHORT:
                    case TIFFTag.TYPE_LONG:
                    case TIFFTag.TYPE_SBYTE:
                    case TIFFTag.TYPE_SSHORT:
                    case TIFFTag.TYPE_SLONG:
                      cchild.setAttribute("value", Integer.toString(field.getIntValues()[i]));
                      break;

                    case TIFFTag.TYPE_RATIONAL:
                    case TIFFTag.TYPE_SRATIONAL:
                    case TIFFTag.TYPE_FLOAT:
                    case TIFFTag.TYPE_DOUBLE:
                      cchild.setAttribute("value", field.getRationalValues()[i].toString());
                      break;

                    case TIFFTag.TYPE_ASCII:
                      StringBuffer buffer = new StringBuffer();
                      String value = field.getASCIIValue();
                      
                      for (int j = 0; j < value.length(); j++)
                        {
                          char c = value.charAt(j);
                          
                          if ((c >= 32) && (c <= 127))
                            {
                              buffer.append(c);
                            }
                          
                          else
                            {
                              buffer.append("\\u0x" + Integer.toHexString(c));                       
                            }
                        }
                      
                      cchild.setAttribute("value", buffer.toString());
                      i = count; 

                      break;
                  }

                child.appendChild(cchild);
              }
          }

        node.appendChild(child);

        return node;
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    private static void addNode (IIOMetadataNode parent,
                                 String name,
                                 String value)
      {
        IIOMetadataNode node = new IIOMetadataNode(name);
        node.setAttribute("value", value);
        parent.appendChild(node);
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return false;
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return false;
      }
    
    /*******************************************************************************
     *
     ******************************************************************************/
    protected Node getNativeTree (String name)
      {
        IIOMetadataNode root = new IIOMetadataNode(name);

        if (primaryIFD != null) // at the moment CRW doesn't have it
          {
            appendIFDAsTree(root, primaryIFD, "IFD", null, 0);
          }
        
        return root;
      }

    /*******************************************************************************
     *
     ******************************************************************************/
    private void appendIFDAsTree (IIOMetadataNode parentNode, 
                                  Directory directory,
                                  String name,
                                  String parentName,
                                  int parentTagNumber)
      {
        int ifdCounter = 0;
            
        for (Directory currentDirectory = directory; currentDirectory != null; currentDirectory = currentDirectory.getNextDirectory(), ifdCounter++)
          {
            IIOMetadataNode directoryNode = new IIOMetadataNode("TIFFIFD");
            parentNode.appendChild(directoryNode);
            String currentName = name;
                
            if (currentName.equals("IFD"))
              {
                currentName += ifdCounter;  
              }
            
            directoryNode.setAttribute("name", currentName);
            directoryNode.setAttribute("start",  Long.toString(currentDirectory.getStart()));
            directoryNode.setAttribute("end",  Long.toString(currentDirectory.getEnd()));

            if (parentName != null)
              {
                directoryNode.setAttribute("parentName", parentName);
              }

            if (parentTagNumber != 0)
              {
                directoryNode.setAttribute("parentNumber", Integer.toString(parentTagNumber));
              }

            for (Iterator i = currentDirectory.tags().iterator(); i.hasNext();)
              {
                TIFFTag f = (TIFFTag)i.next();
                int tagNumber = f.getCode();
                Object t = new Integer(tagNumber);
                Node node = getNativeNode(f, currentDirectory.getRegistry());

                if (node != null)
                  {
                    directoryNode.appendChild(node);
                  }
              }
            
            int n = 0;
            for (Iterator i = currentDirectory.subDirectories(); i.hasNext();)
              {
                 IFD subIFD = (IFD)i.next();
                 appendIFDAsTree(directoryNode, subIFD, "SubIFD" + n++, currentName, 0);
              }

            String[] subIFDNames = currentDirectory.getSubDirectoryNames();

            for (int j = 0; j < subIFDNames.length; j++)
              {
                int tagNumber = 0; // FIXME
                Directory subIFD = currentDirectory.getNamedDirectory(subIFDNames[j]);
                appendIFDAsTree(directoryNode, subIFD, subIFDNames[j], currentName, tagNumber);
              }            
          }
      }

    /*******************************************************************************
     *
     * @inheritDoc
     * 
     ******************************************************************************/
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName() + "[");
        buffer.append("\n****primaryDirectory: " + primaryIFD);
        buffer.append("\n****exifIFD: " + getExifIFD());
        buffer.append("\n****makerNoteDirectory: " + getMakerNote());

        return buffer.toString();
      }
  }
