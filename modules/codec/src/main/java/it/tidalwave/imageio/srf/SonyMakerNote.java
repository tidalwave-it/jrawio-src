/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.srf;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFDSupport;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class SonyMakerNote extends IFDSupport
  {
    private final static long serialVersionUID = -3814200534424599307L;
    private final static String CLASS = SonyMakerNote.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private final static int MAKER_NOTE_KEY_OFFSET = 200896;

    private final static int FIRST_ENCRYPTED_BLOCK_BYTE_COUNT = 9074 * 4;

    private final static int SECOND_ENCRYPTED_BLOCK_BYTE_COUNT = 174376 * 4;

    private final static int SECOND_ENCRYPTED_BLOCK_OFFSET = 40;

    private final static int MAKER_NOTE_BYTE_COUNT = SECOND_ENCRYPTED_BLOCK_BYTE_COUNT + SECOND_ENCRYPTED_BLOCK_OFFSET;

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    public void loadAll (final @Nonnull RAWImageInputStream iis, 
                         final @Nonnegative long offset)
      throws IOException
      {
        logger.fine("loadAll(%s, %d)", iis, offset);
        final int makerNoteKey = getMakerNoteKey(iis);
        //
        // The first IFD (SRF0) is in plaintext.
        //
        final SonySRF0 srf0 = new SonySRF0();
        setSRF(0, srf0);
        logger.finer(">>>> loading plaintext SRF0 AT %d", offset);
        long nextOffset = srf0.load(iis, offset);
        //
        // Follows a block of encrypted data. Part of this block is encrypted twice,
        // so unfortunately we cannot decrypt it with the simple use of a SonyDecypher.
        // We have to load it entirely in memory and process it.
        //
        final byte[] buffer = new byte[MAKER_NOTE_BYTE_COUNT];
        iis.seek(nextOffset);
        iis.readFully(buffer);
        ImageInputStream iis2 = new MemoryCacheImageInputStream(new ByteArrayInputStream(buffer));
        iis2.setByteOrder(ByteOrder.BIG_ENDIAN);
        //
        // A first block is encrypted with the makerNoteKey.
        //
        SonyDecipher decipher = new SonyDecipher(makerNoteKey);
        decipher.decrypt(iis2, FIRST_ENCRYPTED_BLOCK_BYTE_COUNT);
        decipher.getByteBuffer().rewind();
        decipher.getByteBuffer().get(buffer, 0, FIRST_ENCRYPTED_BLOCK_BYTE_COUNT);
        //
        // SRF1 lays entirely in the first block, so it can be read now.
        // And it *must* be read now, since it contains the key for finishing the decryption.
        //
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        bb.order(ByteOrder.BIG_ENDIAN);
        ((SRFImageInputStream)iis).startEncryptedSection(nextOffset, bb);
        final SonySRF1 srf1 = new SonySRF1();
        setSRF(1, srf1);
        logger.fine("Loading encrypted SRF1 AT %d", nextOffset);
        final long srf1Offset = nextOffset;
        nextOffset = srf1.load(iis, nextOffset);
        //
        // Now we decrypt the second block.
        //
        iis2 = new MemoryCacheImageInputStream(new ByteArrayInputStream(buffer));
        iis2.setByteOrder(ByteOrder.BIG_ENDIAN);
        iis2.seek(SECOND_ENCRYPTED_BLOCK_OFFSET);
        decipher = new SonyDecipher(srf1.getSRF2Key());
        decipher.decrypt(iis2, SECOND_ENCRYPTED_BLOCK_BYTE_COUNT);
        decipher.getByteBuffer().get(buffer, SECOND_ENCRYPTED_BLOCK_OFFSET, SECOND_ENCRYPTED_BLOCK_BYTE_COUNT);
        //
        // Now we can load the rest of the marker note.
        //
        bb = ByteBuffer.wrap(buffer);
        bb.order(ByteOrder.BIG_ENDIAN);
        ((SRFImageInputStream)iis).startEncryptedSection(srf1Offset, bb);

        for (int srfIndex = 2; nextOffset > 0; srfIndex++)
          {
            Directory srf;

            try
              {
                final Class srfClass = Class.forName("it.tidalwave.imageio.srf.SonySRF" + srfIndex);
                srf = (Directory)srfClass.newInstance();
              }
            catch (Exception e)
              {
                throw new IOException(e.toString());
              }

            setSRF(srfIndex, srf);
            logger.finer(">>>> loading encrypted SRF%d AT %d", srfIndex, nextOffset);
            nextOffset = srf.load(iis, nextOffset);
          }

        ((SRFImageInputStream)iis).stopEncryptedSection();
      }

    private void setSRF (int i, Directory srf)
      {
        this.addNamedDirectory("SRF" + i, srf);
      }

    @Nonnull
    public SonySRF0 getSRF0()
      {
        return (SonySRF0)getNamedDirectory("SRF0");
      }

    @Nonnull
    public SonySRF1 getSRF1()
      {
        return (SonySRF1)getNamedDirectory("SRF1");
      }

    @Nonnull
    public SonySRF2 getSRF2()
      {
        return (SonySRF2)getNamedDirectory("SRF2");
      }

    @Nonnull
    public String getColorMode()
      {
        // TODO Auto-generated method stub
        return "UNKNOWN";
      }

    @Nonnull
    public String getToneCompensation()
      {
        // TODO Auto-generated method stub
        return "UNKNOWN";
      }

    public boolean isCompressionDataAvailable()
      {
        // TODO Auto-generated method stub
        return false;
      }

    public int[] getLinearizationTable()
      {
        // TODO Auto-generated method stub
        return null;
      }

    public byte[] getCurve()
      {
        // TODO Auto-generated method stub
        return null;
      }

    @Nonnull
    public String getWhiteBalance()
      {
        // TODO Auto-generated method stub
        return "SUNNY";
      }

    public boolean isWhiteBalanceFineAvailable()
      {
        // TODO Auto-generated method stub
        return false;
      }

    public int getWhiteBalanceFine()
      {
        return 0;
      }

    @Nonnull
    public TagRational[] getWhiteBalanceRBCoefficients ()
      {
        TagRational ONE = new TagRational(1, 1);
        return new TagRational[] { ONE, ONE };
      }
    
    /*******************************************************************************************************************
     * 
     * Retrieves the makerNote decryption key.
     * 
     * @param  iis          the image input stream
     * @return              the key
     * @throws IOException
     * 
     ******************************************************************************************************************/
    private int getMakerNoteKey (final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        iis.seek(MAKER_NOTE_KEY_OFFSET); 
        final int skip = iis.readByte() & 0xff;
        // System.err.println("BBB is " + bbb);
        iis.skipBytes(skip * 4 - 1);
        final ByteOrder byteOrderSave = iis.getByteOrder();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        // System.err.println("Reading key at: " + iis.getStreamPosition());
        final int ifdKey = iis.readInt();
        iis.setByteOrder(byteOrderSave);
        
        return ifdKey;
      }
  }
