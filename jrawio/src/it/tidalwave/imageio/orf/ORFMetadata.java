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
 * $Id: ORFMetadata.java 93 2008-08-24 13:57:07Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.orf;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.io.IOException;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: ORFMetadata.java 93 2008-08-24 13:57:07Z fabriziogiudici $
 *
 ******************************************************************************/
public class ORFMetadata extends TIFFMetadataSupport
  {
    private final static String CLASS = ORFMetadata.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = 1795868418676854749L;

    /*******************************************************************************
     *
     ******************************************************************************/
    public ORFMetadata (@Nonnull final Directory primaryIFD, 
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor)
      {
        super(primaryIFD, iis, headerProcessor);
        
        final OlympusMakerNote olympusMakerNote = getOlympusMakerNote();
        
        if (olympusMakerNote != null)
          {
            try
              {
                thumbnailHelperList.add(olympusMakerNote.loadThumbnailHelper(iis));
              }
            catch (IOException e) 
              {
                logger.warning("Cannot load thumbnail: " + e);
              } 
            catch (NoSuchElementException e) 
              {
                // not an error
              }
          }
                
//                final byte[] buffer = olympusMakerNote.getJpegThumbnail();
//                thumbnailHelperList.add(new ThumbnailHelper(iis, buffer)
//                  {
////                     The embedded JPEG thumbnail lacks the JPEG header
//                    @Override
//                    protected InputStream createInputStream (final byte[] buffer)
//                      {
//                        FileOutputStream fos;
//                        try {
//                            fos = new FileOutputStream("/tmp/dump");
//                            fos.write(buffer);
//                            fos.close();
//                        } catch (Exception ex) {
//                            Logger.getLogger(MRWMetadata.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                        
//                        
//                        int skip = 0x100;
//                        InputStream is = super.createInputStream(buffer);
//                        try {
//                            is.skip(skip);
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                        return is;
//                      }
//                  });
      }

    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @CheckForNull
    public OlympusMakerNote getOlympusMakerNote()
      {
        return (OlympusMakerNote)getMakerNote();
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return ifd.isPhotometricInterpretationAvailable();
      }
    
    /***************************************************************************
     *
     * {@inheritDoc}
     *
     **************************************************************************/
    @Override
    protected boolean isThumbnailIFD (@Nonnull final IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }
  }
