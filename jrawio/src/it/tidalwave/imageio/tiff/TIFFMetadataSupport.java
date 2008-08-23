/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
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
 * $Id: TIFFMetadataSupport.java 69 2008-08-23 15:09:09Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.raw.TagRegistry;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: TIFFMetadataSupport.java 69 2008-08-23 15:09:09Z fabriziogiudici $
 *
 ******************************************************************************/
public class TIFFMetadataSupport extends RAWMetadataSupport
  {
    private static final String[] ORIENTATION_NAMES = 
      {
        null, "Normal", "FlipH", "Rotate180", "FlipV", "FlipHRotate90", "Rotate270", "FlipVRotate90", "Rotate90" 
      };

    private static final String[] COLOR_SPACE_NAMES = 
      {
        "GRAY", "GRAY", "RGB", "RGB", "GRAY", null, "YCbCr", "Lab", "Lab" 
      };

    private static final String[] TYPE_NAMES = 
      {
        null, "Byte", "Ascii", "Short", "Long", "Rational", "SByte", 
        "Undefined", "SShort", "SLong", "SRational", "Float", "Double", "IFDPointer" 
      };

    @Nonnull
    private IFD primaryIFD;

    @CheckForNull
    private IFD exifIFD;

    @CheckForNull
    protected IFD rasterIFD;
    
    protected final List<ThumbnailHelper> thumbnailHelperList = new ArrayList<ThumbnailHelper>();
        
    /***************************************************************************
     * 
     * Constructs this object.
     * 
     * @param   primaryDirectory  the primary directory
     * @param   iis               the input stream
     * @param   headerProcessor   the header processor
     * 
     **************************************************************************/
    public TIFFMetadataSupport (@Nonnull final Directory primaryDirectory,
                                @Nonnull final RAWImageInputStream iis, 
                                @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryDirectory, "it_tidalwave_imageio_tiff_image_1.0", headerProcessor);

        if (primaryDirectory != null)
          {
            primaryIFD = (IFD)primaryDirectory;
            exifIFD = (IFD)primaryDirectory.getNamedDirectory(IFD.EXIF_NAME);

            if (exifIFD == null)
              {
                for (final Iterator<Directory> i = primaryDirectory.subDirectories(); i.hasNext();)
                  {
                    final Directory directory = i.next();
                    exifIFD = (IFD)directory.getNamedDirectory(IFD.EXIF_NAME);

                    if (exifIFD != null)
                      {
                        break;
                      }
                  }
              }

            final List<IFD> ifdList = new ArrayList<IFD>();
        
            for (IFD ifd = primaryIFD; ifd != null; ifd = (IFD)ifd.getNextDirectory())
              {
                ifdList.add(ifd);
              }
        
            for (final Iterator i = primaryIFD.subDirectories(); i.hasNext(); )
              {
                ifdList.add((IFD)i.next());
              }
        
            for (final IFD ifd : ifdList)
              {
                if (isRasterIFD(ifd))
                  {
                    rasterIFD = ifd;  
                  }
            
                // no else, sometimes a single IFD will be both raster and thumbnail
                if (isThumbnailIFD(ifd)) 
                  {
                    thumbnailHelperList.add(new ThumbnailHelper(iis, ifd));
                  }
              }        
          }        
        
        postInit(iis);
      }

    /***************************************************************************
     * 
     * Returns the image width.
     * 
     * @return the image width
     * 
     **************************************************************************/
    @Nonnegative
    public int getWidth()
      {
        return rasterIFD.getImageWidth();
      }
    
    /***************************************************************************
     * 
     * Returns the image height.
     * 
     * @return the image height
     * 
     **************************************************************************/
    @Nonnegative
    public int getHeight()
      {
        return rasterIFD.getImageLength();
      }
    
    /***************************************************************************
     * 
     * Returns the thumbnail width.
     * 
     * @param   thumbnailIndex   the requested thumbnail
     * @return                   the width
     * 
     **************************************************************************/
    @Nonnegative
    public int getThumbnailWidth (@Nonnegative final int thumbnailIndex)
      {
        return thumbnailHelperList.get(thumbnailIndex).getWidth();
      }
    
    /***************************************************************************
     * 
     * Returns the thumbnail height.
     * 
     * @param   thumbnailIndex   the requested thumbnail
     * @return                   the height
     * 
     **************************************************************************/
    @Nonnegative
    public int getThumbnailHeight (@Nonnegative final int thumbnailIndex)
      {
        return thumbnailHelperList.get(thumbnailIndex).getHeight();
      }
     
    /***************************************************************************
     * 
     * @return
     * 
     **************************************************************************/
    @Nonnull
    public ThumbnailHelper[] getThumbnailHelper()
      {
        return thumbnailHelperList.toArray(new ThumbnailHelper[0]);
      }

    /***************************************************************************
     *
     * Returns the primary IFD.
     * 
     * @return the primary IFD
     * 
     **************************************************************************/
    @Nonnull
    public IFD getPrimaryIFD()
      {
        return primaryIFD;
      }

    /***************************************************************************
     * 
     * Returns the raster IFD.
     * 
     * @return the raster IFD
     * 
     **************************************************************************/
    @Nonnull
    public IFD getRasterIFD()
      {
        return rasterIFD;
      }
    
    /***************************************************************************
     * 
     * Returns the EXIF IFD.
     * 
     * @return the EXIF IFD
     * 
     **************************************************************************/
    @CheckForNull
    public IFD getExifIFD()
      {
        return exifIFD;
      }

    /***************************************************************************
     * 
     * Returns the maker note.
     * 
     * @return  the maker note
     * 
     **************************************************************************/
    @CheckForNull
    public Directory getMakerNote()
      {
        final IFD exifIFD = getExifIFD(); // call the method since it can be polimorphic
        return (exifIFD == null) ? null : exifIFD.getNamedDirectory(IFD.MAKER_NOTE_NAME);
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    public IIOMetadataNode getStandardDimensionNode()
      {
        final IIOMetadataNode dimensionNode = new IIOMetadataNode("Dimension");
        final IFD resolutionIFD = getPrimaryIFD();
        final IFD orientationIFD = getPrimaryIFD();

        if (resolutionIFD.isXResolutionAvailable() && resolutionIFD.isYResolutionAvailable())
          {
            final TagRational xres = resolutionIFD.getXResolution();
            final TagRational yres = resolutionIFD.getYResolution();
            final IIOMetadataNode node = new IIOMetadataNode("PixelAspectRatio");
            final float ratio = xres.floatValue() / yres.floatValue();
            node.setAttribute("value", Float.toString(ratio));
            dimensionNode.appendChild(node);

            boolean gotPixelSize = false;

            if (resolutionIFD.isResolutionUnitAvailable())
              {
                final int resolutionUnit = resolutionIFD.getResolutionUnit().intValue();

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
                    final float horizontalPixelSize = 10f / xres.floatValue();
                    addNameValueNode(dimensionNode, "HorizontalPixelSize", Float.toString(horizontalPixelSize));
                  }

                if (yres != null)
                  {
                    final float verticalPixelSize = 10f / yres.floatValue();
                    addNameValueNode(dimensionNode, "VerticalPixelSize", Float.toString(verticalPixelSize));
                  }
              }
          }

        if (orientationIFD.isOrientationAvailable())
          {
            final int o = orientationIFD.getOrientation().intValue();

            if ((o >= 0) && (o < ORIENTATION_NAMES.length))
              {
                addNameValueNode(dimensionNode, "ImageOrientation", ORIENTATION_NAMES[o]);
              }
          }

        addNameValueNode(dimensionNode, "HorizontalScreenSize", Integer.toString(getWidth()));
        addNameValueNode(dimensionNode, "VerticalScreenSize", Integer.toString(getHeight()));

        return dimensionNode;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    public IIOMetadataNode getStandardChromaNode()
      {
        final IIOMetadataNode chromaNode = new IIOMetadataNode("Chroma");

        if (primaryIFD.isPhotometricInterpretationAvailable())
          {
            final int photometricInterpretation = primaryIFD.getPhotometricInterpretation().intValue();

            if ((photometricInterpretation > 0) && (photometricInterpretation < COLOR_SPACE_NAMES.length))
              {
                final IIOMetadataNode node = new IIOMetadataNode("ColorSpaceType");
                node.setAttribute("name", COLOR_SPACE_NAMES[photometricInterpretation]);
                chromaNode.appendChild(node);
              }

            final IIOMetadataNode node = new IIOMetadataNode("BlackIsZero");
            node.setAttribute("value", (photometricInterpretation != 0) ? "TRUE" : "FALSE");
            chromaNode.appendChild(node);
          }

        if (primaryIFD.isBitsPerSampleAvailable())
          {
            final int numChannels = primaryIFD.getBitsPerSample().length;
            final IIOMetadataNode node = new IIOMetadataNode("NumChannels");
            node.setAttribute("value", Integer.toString(numChannels));
            chromaNode.appendChild(node);
          }

        return chromaNode;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    public IIOMetadataNode getStandardDocumentNode()
      {
        final IIOMetadataNode documentNode = new IIOMetadataNode("Document");

        if (primaryIFD.isDateTimeAvailable())
          {
            final String s = primaryIFD.getDateTime();
            final String[] st = s.split("[:. ]");
            final IIOMetadataNode node = new IIOMetadataNode("ImageCreationTime");
            node.setAttribute("year", st[0]);
            node.setAttribute("month", st[1]);
            node.setAttribute("day", st[2]);
            node.setAttribute("hour", st[3]);
            node.setAttribute("minute", st[4]);

            if (st.length > 5)
              {
                node.setAttribute("second", st[5]);
              }

            documentNode.appendChild(node);
          }

        return documentNode;
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Nonnull
    public Node getNativeNode (@Nonnull final TIFFTag field, 
                               @Nonnull final TagRegistry registry)
      {
        final IIOMetadataNode node = new IIOMetadataNode("TIFFField");
        node.setAttribute("number", Integer.toString(field.getCode()));
        final String name = registry.getTagName(field.getCode());

        if (name != null)
          {
            node.setAttribute("name", name);
          }

        final int count = field.getValuesCount();
        final int type = field.getType();
        IIOMetadataNode child = new IIOMetadataNode("TIFF" + TYPE_NAMES[type] + "s");

        if (type == TIFFTag.TYPE_UNDEFINED)
          {
            child = new IIOMetadataNode("TIFFUndefined");

            final byte[] data = field.getByteValues();
            final StringBuilder sb = new StringBuilder(count * 4);

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
                final IIOMetadataNode cchild = new IIOMetadataNode("TIFF" + TYPE_NAMES[type]);

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
                      final StringBuilder buffer = new StringBuilder();
                      final String value = field.getASCIIValue();
                      
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

    /***************************************************************************
     * 
     * @param   iis               the input stream
     * 
     **************************************************************************/
    protected void postInit (@Nonnull final RAWImageInputStream iis)
      {
      }

    /***************************************************************************
     *
     * Probes an IFD and returns true if it's the raster IFD, i.e. it's 
     * describing the main image.
     * 
     * @param  ifd  the IFD to probe
     * @return      true if it's the raster IFD
     *
     **************************************************************************/
    protected boolean isRasterIFD (final IFD ifd)
      {
        return false;
      }
    
    /***************************************************************************
     *
     * Probes an IFD and returns true if it's a thumbnail IFD, i.e. it's 
     * describing a thumbnail.
     * 
     * @param  ifd  the IFD to probe
     * @return      true if it's a thumbnail IFD
     *
     **************************************************************************/
    protected boolean isThumbnailIFD (final IFD ifd)
      {
        return false;
      }
    
    /***************************************************************************
     *
     **************************************************************************/
    @Nonnull
    protected Node getNativeTree (@Nonnull final String name)
      {
        final IIOMetadataNode root = new IIOMetadataNode(name);

        if (primaryIFD != null) // at the moment CRW doesn't have it
          {
            appendIFDAsTree(root, primaryIFD, "IFD", null, 0);
          }
        
        return root;
      }

    /***************************************************************************
     *
     * Utility method to create a new node with a name=value pair.
     * 
     * @param  parent   the parent node
     * @param  name     the name
     * @param  value    the value
     * 
     **************************************************************************/
    private static void addNameValueNode (@Nonnull final IIOMetadataNode parent,
                                          @Nonnull final String name,
                                          @Nonnull final String value)
      {
        final IIOMetadataNode node = new IIOMetadataNode(name);
        node.setAttribute("value", value);
        parent.appendChild(node);
      }

    /***************************************************************************
     *
     **************************************************************************/
    private void appendIFDAsTree (final IIOMetadataNode parentNode, 
                                  final Directory directory,
                                  final String name,
                                  final String parentName,
                                  final int parentTagNumber)
      {
        int ifdCounter = 0;
            
        for (Directory currentDirectory = directory; 
             currentDirectory != null; 
             currentDirectory = currentDirectory.getNextDirectory(), ifdCounter++)
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

    /***************************************************************************
     *
     * {@inheritDoc}
     * 
     **************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        final StringBuilder buffer = new StringBuilder();

        buffer.append(getClass().getName() + "[");
        buffer.append("\n****primaryDirectory: " + primaryIFD);
        buffer.append("\n****exifIFD: " + getExifIFD());
        buffer.append("\n****makerNoteDirectory: " + getMakerNote());

        return buffer.toString();
      }
  }
