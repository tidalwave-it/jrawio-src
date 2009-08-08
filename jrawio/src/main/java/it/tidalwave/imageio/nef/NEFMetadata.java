/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.nef;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.Dimension;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.ThumbnailLoader;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFMetadata extends TIFFMetadataSupport
  {
    private final static String CLASS = NEFMetadata.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 1795868438676854749L;

    /** We have a different approach to compute dimensions, that are stored here .*/
    @Nonnegative
    private int width;
    
    /** We have a different approach to compute dimensions, that are stored here .*/
    @Nonnegative
    private int height;

    /** The Nikon Capture Editor metadata. */
    @CheckForNull
    private transient Object nceMetadata;
    
    /*******************************************************************************************************************
     *
     * Constructs this object.
     * 
     * @param   primaryIFD        the primary IFD
     * @param   iis               the input stream
     * @param   headerProcessor   the header processor
     * 
     ******************************************************************************************************************/
    public NEFMetadata (@Nonnull final Directory primaryIFD, 
                        @Nonnull final RAWImageInputStream iis, 
                        @CheckForNull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
      }

    /*******************************************************************************************************************
     * 
     * FIXME: coulldn't this just be moved to the constructor? You could even
     * turn fields final.
     * 
     * {@inheritDoc}
     * 
     *************************************************************************/
    @Override
    protected void postInit (@Nonnull final RAWImageInputStream iis)
      {
        // Special case
        // Seen for instance in fabriziogiudici/Nikon/D100/TIFF/TIFF-Large.TIF
        // which is a .NEF converted into .TIF with NCE.
        // The IFD don't have the 'newSubFileTypeAvailable', so isRasterIFD()
        // and isThumbnailIFD() fail.
        if (rasterIFD == null)
          {
            logger.fine(">>>> rasterIFD not detected yet, guessing...");
            rasterIFD = getPrimaryIFD();
            final Directory nextDirectory = rasterIFD.getNextDirectory();
            
            if ((nextDirectory != null) && (nextDirectory instanceof IFD))
              {
                thumbnailLoaders.add(new ThumbnailLoader(iis, (IFD)nextDirectory));
              }          
          }
        // end special case
        
        if (!rasterIFD.isJPEGInterchangeFormatAvailable())
          {
            width = rasterIFD.getImageWidth();
            height = rasterIFD.getImageLength();
          }
        
        else // This NEF is just an embedded JPEG
          {
            try
              {
                final int byteCount = rasterIFD.getJPEGInterchangeFormatLength();
                final long offset = rasterIFD.getJPEGInterchangeFormat();
                final byte[] buffer = new byte[byteCount];
                iis.seek(offset);
                iis.readFully(buffer);
                final ImageReader ir = ImageIO.getImageReadersByFormatName("JPEG").next();
                
                try
                  {
                    ir.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(buffer)));
                    width = ir.getWidth(0);
                    height = ir.getHeight(0);
                  }
                finally
                  {
                    ir.dispose();
                  }
              }
            catch (IOException e)
              {
                logger.warning("While reading size of NEF embedded JPEG: " + e);
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
    
    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    public NikonMakerNote3 getNikonMakerNote()
      {
        return (NikonMakerNote3)getMakerNote();
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @CheckForNull
    public Object getCaptureEditorMetadata()
      {
        return nceMetadata;
      }

    /*******************************************************************************************************************
     * 
     * @param captureEditorMetadata
     * 
     ******************************************************************************************************************/
    public void _setCaptureEditorMetadata (@CheckForNull final Object captureEditorMetadata)
      {
        this.nceMetadata = captureEditorMetadata;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getImageSize()
      {
        return new Dimension(width, height);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.IMAGE); 
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isThumbnailIFD (@Nonnull final IFD ifd)
      {
        return (ifd.isNewSubFileTypeAvailable() && (ifd.getNewSubFileType() == IFD.NewSubFileType.REDUCED_RESOLUTION))
        || ifd.isJPEGInterchangeFormatAvailable(); 
      }
  }
