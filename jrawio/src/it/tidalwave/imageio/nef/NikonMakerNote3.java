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
 * $Id: NikonMakerNote3.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: NikonMakerNote3.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public final class NikonMakerNote3 extends Nikon3MakerNoteSupport
  {
    /** Lens info interpreter. */
    private transient NikonLensInfo lensInfo;
    
    private boolean bigEndian;
    // private ByteOrder byteOrder; // ByteOrder not serializable

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        //
        // The NEF MakerNote is quite strange. If the first 10 bytes start with
        // "Nikon", then a whole sub-file will occur (including a new header).
        // Otherwise a normal IFD occurs.
        //
 
        iis.seek(offset);
        long baseOffsetSave = iis.getBaseOffset();
        ByteOrder byteOrderSave = iis.getByteOrder();
        iis.setBaseOffset(0);
        offset = iis.getStreamPosition();

        byte[] buffer = new byte[10];
        iis.read(buffer);
        String s = new String(buffer);

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
    
    /*******************************************************************************
     * 
     * @return the lens info
     * 
     *******************************************************************************/
    public NikonLensInfo getLensInfo2 ()
      {
        if ((lensInfo == null) && isLensInfoAvailable())
          {
            lensInfo = new NikonLensInfo(getLensInfo());
          }

        return lensInfo;
      }

    /*******************************************************************************
     * 
     * @return true if the lens name is available
     * 
     *******************************************************************************/
    public boolean isLensNameAvailable ()
      {
        NikonLensInfo lensInfo = getLensInfo2();

        return (lensInfo != null) && (lensInfo.getLensName() != null);
      }

    /*******************************************************************************
     * 
     * @return the lens name
     * 
     *******************************************************************************/
    public String getLensName ()
      {
        if (!isLensNameAvailable())
          {
            throw new NoSuchElementException();
          }

        return getLensInfo2().getLensName();
      }

    /*******************************************************************************
     * 
     * @return the vertical predictor
     * 
     *******************************************************************************/
    public int[] getVPredictor ()
      {
        ShortBuffer shortBuffer = getCompressionDataAsShortBuffer();
        shortBuffer.position(1);
        int[] vPredictor = new int[4];

        for (int i = 0; i < vPredictor.length; i++)
          {
            vPredictor[i] = shortBuffer.get();
          }

        return vPredictor;

      }

    /*******************************************************************************
     * 
     * @return the linearization table
     * 
     *******************************************************************************/
    public int[] getLinearizationTable ()
      {
        ShortBuffer shortBuffer = getCompressionDataAsShortBuffer();
        shortBuffer.position(5);
        int lutSize = shortBuffer.get();
        int[] lut = new int[lutSize];

        for (int i = 0; i < lutSize; i++)
          {
            lut[i] = shortBuffer.get() & 0xFFFF;
          }

        return lut;
      }

    /*******************************************************************************
     * 
     * @return the compression data
     * 
     *******************************************************************************/
    private ShortBuffer getCompressionDataAsShortBuffer ()
      {
        byte[] bytes = getCompressionData();

        if (bytes != null)
          {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
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

    /*******************************************************************************
     *
     ******************************************************************************/
    public NEFWhiteBalanceInfo getWhiteBalanceInfo()
      {
        return isBlock151Available() ? new NEFWhiteBalanceInfo(this, bigEndian) : null;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String toString ()
      {
        StringBuffer buffer = new StringBuffer(super.toString());

        if (isLensInfoAvailable())
          {
            buffer.append("\n\tLensInfo:" + getLensInfo2());
          }

        return buffer.toString();
      }
  }
