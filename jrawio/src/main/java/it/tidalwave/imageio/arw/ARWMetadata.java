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
package it.tidalwave.imageio.arw;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.minolta.MinoltaRawData;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.HeaderProcessor;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ARWMetadata extends TIFFMetadataSupport
  {
    private final static long serialVersionUID = 3012868418676854749L;
    
    private final MinoltaRawData minoltaRawData = new MinoltaRawData();
    
//    private final IFD exifIFD;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public ARWMetadata (@Nonnull final Directory primaryIFD,
                        @Nonnull final RAWImageInputStream iis, 
                        @Nonnull final HeaderProcessor headerProcessor) 
      throws IOException
      {
        super(primaryIFD, iis, headerProcessor);
        
        final IFD ifd = (IFD)primaryIFD;
        
        if (ifd.isDNGPrivateDataAvailable())
          {
            final ByteBuffer byteBuffer = ByteBuffer.wrap(ifd.getDNGPrivateData());
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            final int offset = byteBuffer.asIntBuffer().get();
            final long save = iis.getStreamPosition();
            iis.seek(offset);
            
            if ((iis.read() == 0) && (iis.read() == 'M') && (iis.read() == 'R'))
              {
                final int order = iis.read();
                minoltaRawData.load(iis, offset, (order == 'I') ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
              }
            
            iis.seek(save);
          }
//        exifIFD = (IFD)primaryIFD.getNamedDirectory(IFD.EXIF_NAME);
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    public ARWMakerNote getARWMakerNote()
      {
        return (ARWMakerNote)getMakerNote();
      }

    /*******************************************************************************************************************
     * 
     * @return
     * 
     ******************************************************************************************************************/
    @Nonnull
    public MinoltaRawData getMinoltaRawData() 
      {
        return minoltaRawData;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
//    @Override
//    public IFD getExifIFD() 
//      {
//        return exifIFD;
//      }
//    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isRasterIFD (@Nonnull final IFD ifd)
      {
        return false; // TODO: type == 0, but never happens
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    protected boolean isThumbnailIFD (@Nonnull final IFD ifd)
      {
        return ifd.isJPEGInterchangeFormatAvailable();
      }
    

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Dimension getSize()
      {
        return minoltaRawData.getPRD().getCcdSize();
      }
  }
