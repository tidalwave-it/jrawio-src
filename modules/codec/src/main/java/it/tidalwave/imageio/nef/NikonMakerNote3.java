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
import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public final class NikonMakerNote3 extends Nikon3MakerNoteSupport
  {
    private static final long serialVersionUID = -802326201633669892L;
    
    /** Lens info interpreter. */
    private transient NikonLensInfo lensInfo;

    private transient NEFLinearizationTable nefLinearizationTable;
    
    private boolean bigEndian;
    // private ByteOrder byteOrder; // ByteOrder not serializable

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    public void loadAll (@Nonnull final RAWImageInputStream iis, 
                         long offset)
      throws IOException
      {
        //
        // The NEF MakerNote is quite strange. If the first 10 bytes start with
        // "Nikon", then a whole sub-file will occur (including a new header).
        // Otherwise a normal IFD occurs.
        //
        iis.seek(offset);
        final long baseOffsetSave = iis.getBaseOffset();
        final ByteOrder byteOrderSave = iis.getByteOrder();
        iis.setBaseOffset(0);
        offset = iis.getStreamPosition();

        final byte[] buffer = new byte[10];
        iis.readFully(buffer);
        final String s = new String(buffer);

        if (s.startsWith("Nikon"))
          {
            iis.setBaseOffset(iis.getStreamPosition());
            offset = TIFFImageReaderSupport.processHeader(iis, null);
          }

        bigEndian = iis.getByteOrder() == ByteOrder.BIG_ENDIAN;
        super.loadAll(iis, offset);
        iis.setBaseOffset(baseOffsetSave);
        iis.setByteOrder(byteOrderSave);        
      }
    
    /*******************************************************************************************************************
     * 
     * @return the lens info
     * 
     ******************************************************************************************************************/
    @Nonnull
    public NikonLensInfo getLensInfo2()
      {
        if ((lensInfo == null) && isLensInfoAvailable())
          {
            lensInfo = new NikonLensInfo(getLensInfo());
          }

        return lensInfo;
      }

    /*******************************************************************************************************************
     * 
     * @return true if the lens name is available
     * 
     ******************************************************************************************************************/
    public boolean isLensNameAvailable()
      {
        final NikonLensInfo lensInfo = getLensInfo2();
        return (lensInfo != null) && (lensInfo.getLensName() != null);
      }

    /*******************************************************************************************************************
     * 
     * @return the lens name
     * 
     ******************************************************************************************************************/
    @Nonnull
    public String getLensName()
      {
        if (!isLensNameAvailable())
          {
            throw new NoSuchElementException();
          }

        return getLensInfo2().getLensName();
      }

    /*******************************************************************************************************************
     * 
     * @return the vertical predictor
     * 
     ******************************************************************************************************************/
    @Nonnull
    public int[] getVPredictor()
      {
        final ShortBuffer shortBuffer = getCompressionDataAsShortBuffer();
        shortBuffer.position(1);
        final int[] vPredictor = new int[4];

        for (int i = 0; i < vPredictor.length; i++)
          {
            vPredictor[i] = shortBuffer.get();
          }

        return vPredictor;

      }

    /*******************************************************************************************************************
     * 
     * @return the linearization table
     * 
     ******************************************************************************************************************/
    @Nonnull
    public synchronized NEFLinearizationTable getLinearizationTable()
      {
        if (nefLinearizationTable == null)
          {
            nefLinearizationTable = new NEFLinearizationTable(this);
          }

        return nefLinearizationTable;
      }

    /*******************************************************************************************************************
     * 
     * @return the compression data
     * 
     ******************************************************************************************************************/
    @CheckForNull
    protected ShortBuffer getCompressionDataAsShortBuffer()
      {
        final byte[] bytes = getCompressionData();

        if (bytes != null)
          {
            final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            
            if ((shortBuffer.get(5) & 0xffff) > 4096) // LUT size is clearly less than 1<<12
              {
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                shortBuffer = byteBuffer.asShortBuffer();
              }
            
            return shortBuffer;
          }

        return null;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @CheckForNull
    public NEFWhiteBalanceInfo getWhiteBalanceInfo()
      {
        return isBlock151Available() ? new NEFWhiteBalanceInfo(this, bigEndian) : null;
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    public String toString()
      {
        final StringBuffer buffer = new StringBuffer(super.toString());

        if (isLensInfoAvailable())
          {
            buffer.append("\n>>>>LensInfo:" + getLensInfo2());
          }

        return buffer.toString();
      }
  }
