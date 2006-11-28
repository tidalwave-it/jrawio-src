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
 * $Id: RAWImageReaderSpiSupport.java,v 1.6 2006/02/11 20:29:39 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.raw;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: RAWImageReaderSpiSupport.java,v 1.6 2006/02/11 20:29:39 fabriziogiudici Exp $
 *
 ******************************************************************************/
public abstract class RAWImageReaderSpiSupport extends ImageReaderSpi
  {
    /** A postprocessor, if available, will be run against the loaded image. */
    private static Map postProcessorMapBySpiClass = new HashMap();
    
    /*******************************************************************************
     * 
     * @param names
     * @param suffixes
     * @param MIMETypes
     * @param readerClass
     * @param inputTypes
     * @param writerSpiNames
     * @param supportsStandardStreamMetadataFormat
     * @param nativeStreamMetadataFormatName
     * @param nativeStreamMetadataFormatClassName
     * @param extraStreamMetadataFormatNames
     * @param extraStreamMetadataFormatClassNames
     * @param supportsStandardImageMetadataFormat
     * @param nativeImageMetadataFormatName
     * @param nativeImageMetadataFormatClassName
     * @param extraImageMetadataFormatNames
     * @param extraImageMetadataFormatClassNames
     * 
     *******************************************************************************/
    protected RAWImageReaderSpiSupport (String[] names, String[] suffixes, String[] MIMETypes, Class readerClass,
                                        Class[] inputTypes, String[] writerSpiNames,
                                        boolean supportsStandardStreamMetadataFormat,
                                        String nativeStreamMetadataFormatName,
                                        String nativeStreamMetadataFormatClassName,
                                        String[] extraStreamMetadataFormatNames,
                                        String[] extraStreamMetadataFormatClassNames,
                                        boolean supportsStandardImageMetadataFormat,
                                        String nativeImageMetadataFormatName,
                                        String nativeImageMetadataFormatClassName,
                                        String[] extraImageMetadataFormatNames,
                                        String[] extraImageMetadataFormatClassNames)
      {
        super("tidalwave.it", Version.BUILD, names, suffixes, MIMETypes, readerClass.getName(), inputTypes,
            writerSpiNames, supportsStandardStreamMetadataFormat, nativeStreamMetadataFormatName,
            nativeStreamMetadataFormatClassName, extraStreamMetadataFormatNames, extraStreamMetadataFormatClassNames,
            supportsStandardImageMetadataFormat, nativeImageMetadataFormatName, nativeImageMetadataFormatClassName,
            extraImageMetadataFormatNames, extraImageMetadataFormatClassNames);
      }

    /*******************************************************************************
     * 
     * @param name
     * @param suffixes
     * @param mimeType
     * @param readerClass
     * 
     *******************************************************************************/
    protected RAWImageReaderSpiSupport (String name, String[] suffixes, String mimeType, Class readerClass)
      {
        this(new String[] { name.toLowerCase(), name.toUpperCase() }, 
            suffixes, 
            new String[] { mimeType }, 
            readerClass, 
            new Class[] { ImageInputStream.class }, // inputTypes
            null, // writerSpiNames
            false, // supportsStandardStreamMetadataFormat
            null, // nativeStreamMetadataFormatName
            null, // nativeStreamMetadataFormatClassName
            null, // extraStreamMetadataFormatNames
            null, // extraStreamMetadataFormatClassNames
            false, // supportsStandardImageMetadataFormat
            null, // nativeImageMetadataFormatName
            null, // nativeImageMetadataFormatClassName
            null, // extraImageMetadataFormatNames
            null); // extraImageMetadataFormatClassNam
      }

    /*******************************************************************************
     * 
     * @param name
     * @param suffix
     * @param mimeType
     * @param readerClass
     * 
     *******************************************************************************/
    protected RAWImageReaderSpiSupport (String name, String suffix, String mimeType, Class readerClass)
      {
        this(name, new String[] { suffix.toLowerCase(), suffix.toUpperCase() }, mimeType, readerClass);
      }

    /*******************************************************************************
     * 
     * Installs a postprocessor that will be run against all the instances of images
     * loaded by this Spi.
     * 
     * @param postProcessor  the post processor to install
     *
     *******************************************************************************/
    public static void installPostProcessor (Class spiClass, PostProcessor postProcessor)
      {
        postProcessorMapBySpiClass.put(spiClass, postProcessor);
      }

    /*******************************************************************************
     * 
     * Post-processes a raw image using the installed postprocessor, if any.
     *
     * @param  image  the raw image to postprocess
     * @return        the post-processed image
     * 
     *******************************************************************************/
    protected BufferedImage postProcess (BufferedImage image, RAWMetadataSupport metadata)
      {
        PostProcessor postProcessor = (PostProcessor)postProcessorMapBySpiClass.get(getClass());
        return (postProcessor != null) ? postProcessor.process(image, metadata) : image;
      }
    
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public boolean canDecodeInput (Object source) throws IOException
      {
        if (source instanceof ImageInputStream)
          {
            return canDecodeInput((ImageInputStream)source);
          }

        else
          {
            ImageInputStream iis = null;
            
            try
              {
                iis = ImageIO.createImageInputStream(source);
                
                if (iis != null)
                  {
                    return canDecodeInput(iis);
                  }
              }
            
            finally
              {
                if (iis != null)
                  {
                    iis.close();
                  }
              }
          }

        return false;
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/    
    private boolean canDecodeInput (ImageInputStream source) throws IOException
      {
        RAWImageInputStream iis = new RAWImageInputStream(source);
        iis.setDontClose(); // otherwise the garbage collector will close it together with the original source!!

        try
          {
            iis.mark();
            return canDecodeInput(iis);
          }

        catch (Exception e)
          {
            return false;
          }

        finally
          {
            iis.setBaseOffset(0);
            iis.reset();
          }
      }
    
    /*******************************************************************************
     * 
     * @param iis
     * @return
     * @throws IOException
     * 
     *******************************************************************************/
    protected abstract boolean canDecodeInput (RAWImageInputStream iis) throws IOException;
  }
