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
 * $Id: NEFMetadata.java,v 1.5 2006/02/25 00:03:19 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NEFMetadata.java,v 1.5 2006/02/25 00:03:19 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class NEFMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 1795868438676854749L;

    /** */
    private transient Object captureEditorMetadata;
    
    private int width;
    
    private int height;

    /*******************************************************************************
     *
     ******************************************************************************/
    public NEFMetadata (Directory primaryIFD, RAWImageInputStream iis, HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    protected void postInit (RAWImageInputStream iis)
      {
        if (!rasterIFD.isJPEGInterchangeFormatAvailable())
          {
            width = rasterIFD.getImageWidth();
            height = rasterIFD.getImageLength();
          }
        
        else // This NEF is just and embedded JPEG
          {
            try
              {
                int byteCount = rasterIFD.getJPEGInterchangeFormatLength();
                long offset = rasterIFD.getJPEGInterchangeFormat();
                byte[] buffer = new byte[byteCount];
                iis.seek(offset);
                iis.read(buffer);
                ImageReader ir = (ImageReader)(ImageIO.getImageReadersByFormatName("JPEG").next());
                ir.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(buffer)));
                width = ir.getWidth(0);
                height = ir.getHeight(0);
              }
            
            catch (IOException e)
              {
                System.err.println("While reading size of NEF embedded JPEG: " + e);
              }
          }
/*
        NikonMakerNote3 nefMakerNote = getNikonMakerNote();
        
        if (nefMakerNote.isThumbnailOffsetAvailable())
          {
            int thumbnailOffset = nefMakerNote.getThumbnailOffset();
            int thumbnailSize = 100000; // FIXME
            int thumbnailWidth = nefMakerNote.getBayerUnitCount()[0];
            int thumbnailHeight = nefMakerNote.getBayerUnitCount()[1];
            thumbnailHelperList.add(new ThumbnailHelper(iis, thumbnailOffset, thumbnailSize, thumbnailWidth, thumbnailHeight));
          }
 */
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getWidth()
      {
        return width;
      }
    
    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public int getHeight()
      {
        return height;
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/   
    public NikonMakerNote3 getNikonMakerNote ()
      {
        return (NikonMakerNote3)getMakerNote();
      }

    /*******************************************************************************
     * 
     * @return
     * 
     *******************************************************************************/
    public Object getCaptureEditorMetadata ()
      {
        return captureEditorMetadata;
      }

    /*******************************************************************************
     * 
     * @param captureEditorMetadata
     * 
     *******************************************************************************/
    public void _setCaptureEditorMetadata (Object captureEditorMetadata)
      {
        this.captureEditorMetadata = captureEditorMetadata;
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isRasterIFD (IFD ifd)
      {
        return ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.IMAGE); 
      }
    
    /*******************************************************************************
     *
     * @inheritDoc
     *
     ******************************************************************************/
    protected boolean isThumbnailIFD (IFD ifd)
      {
        return (ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.REDUCED_RESOLUTION))
        || ifd.isJPEGInterchangeFormatAvailable(); 
      }
  }
