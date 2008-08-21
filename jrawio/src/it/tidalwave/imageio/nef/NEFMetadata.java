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
 * $Id: NEFMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
 * @version $Id: NEFMetadata.java 57 2008-08-21 20:00:46Z fabriziogiudici $
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
