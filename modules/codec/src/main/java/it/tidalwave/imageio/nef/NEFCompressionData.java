/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import it.tidalwave.imageio.util.Logger;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class NEFCompressionData
  {
    private final static String CLASS = NEFCompressionData.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    @Nonnull
    private final NikonMakerNote3 makerNote;

    @Nonnull
    private final ShortBuffer shortBuffer;

    @Nonnull
    private final int[] lut;

    private final int version;

    private final int[] vPredictor;

    /*******************************************************************************************************************
     *
     * @return the linearization table
     *
     ******************************************************************************************************************/
    public NEFCompressionData (final @Nonnull NikonMakerNote3 makerNote)
      {
        logger.fine("NEFCompressionData()");
        this.makerNote = makerNote;

        final ByteBuffer byteBuffer = ByteBuffer.wrap(makerNote.getCompressionData());
        final ShortBuffer tempShortBuffer = byteBuffer.asShortBuffer();
        version = tempShortBuffer.get(0);
        final int endiannessMarker = tempShortBuffer.get(1);
        // FIXME: this is really empyrical - these are compression predictors...
        final ByteOrder order = (endiannessMarker == 0x4801) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        byteBuffer.order(order);
        shortBuffer = byteBuffer.asShortBuffer();

        logger.finest(">>>> version:           0x%04x", version);
        logger.finest(">>>> endianness marker: 0x%04x", endiannessMarker);
        logger.finest(">>>> endianness:        %s", order);

        shortBuffer.position(1);
        vPredictor = new int[4];

        for (int i = 0; i < vPredictor.length; i++)
          {
            vPredictor[i] = shortBuffer.get();
          }

        shortBuffer.position(5);
        int lutSize = shortBuffer.get();
        logger.finer(">>>> samples: %d", lutSize);

        if ((version & 0xff00) == 0x4600)
          {
            logger.finer(">>>> using linear table");
            lutSize = 1 << 14;
            lut = new int[lutSize];

            for (int i = 0; i < lutSize; i++)
              {
                lut[i] = i;
              }
          }
        else
          {
            logger.finer(">>>> using sampled table");
            lut = new int[lutSize];

            for (int i = 0; i < lutSize; i++)
              {
                lut[i] = shortBuffer.get() & 0xFFFF;
              }
          }

        logger.finer(toString());
      }

    /*******************************************************************************************************************
     *
     * @return the version
     *
     ******************************************************************************************************************/
    @Nonnull
    public int getVersion()
      {
        return version;
      }
    
    /*******************************************************************************************************************
     *
     * @return the vertical predictor
     *
     ******************************************************************************************************************/
    @Nonnull
    public int[] getVPredictor()
      {
        return vPredictor.clone();
      }

    /*******************************************************************************************************************
     *
     * @return the linearization table values
     *
     ******************************************************************************************************************/
    @Nonnull
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("EI_EXPOSE_REP")
    public int[] getValues()
      {
        return lut; // TODO: not so large, maybe you can clone it
      }

    /*******************************************************************************************************************
     *
     * @return the linearization table values
     *
     ******************************************************************************************************************/
    @Nonnull
    public int[] getExpandedValues (final @Nonnegative int bitsPerSample)
      {
        logger.fine("getExpandedValues(%d)", bitsPerSample);
        
        final int[] values = new int[1 << 16];
        final int max = 1 << bitsPerSample;
        final int step = max / (this.lut.length - 1);

        logger.finer(">>>> version: %04x samples: %d step: %d", version, this.lut.length, step);
          
        if ((version == 0x4420) && (step > 0))
          {
            logger.finer(">>>> returning interpolated and padded values");

            for (int i = 0; i < this.lut.length; i++)
              {
                values[i * step] = this.lut[i];
              }

            for (int i = 0; i < max; i++)
              {
                values[i] = (values[i - i % step] * (step - i % step) +
                             values[i - i % step + step] * (i % step)) / step;
              }

            for (int i = max; i < values.length; i++)
              {
                values[i] = values[max - 1];
              }
          }
        else
          {
            logger.finer(">>>> returning padded values");

            for (int i = 0; i < this.lut.length; i++)
              {
                values[i] = this.lut[i];
              }

            for (int i = this.lut.length; i < values.length; i++)
              {
                values[i] = values[this.lut.length - 1];
              }
          }

        return values;
      }

    /*******************************************************************************************************************
     *
     * @return the white level
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getWhiteLevel()
      {
        return lut[lut.length - 1];
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnull
    @Override
    public String toString()
      {
        return String.format("NEFCompressionData[version: 0x%04x, size: %d, whiteLevel: %d]", version, lut.length, getWhiteLevel());
      }
  }
