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
 * $Id: SonyMakerNote.java,v 1.2 2006/02/14 18:48:51 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.srf;

import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFDSupport;

public class SonyMakerNote extends IFDSupport
  {
    private final static long serialVersionUID = -3814200534424599307L;

    private final static Logger logger = Logger.getLogger("it.tidalwave.imageio.srf.SonyMakerNote");

    private final static int MAKER_NOTE_KEY_OFFSET = 200896;

    private final static int FIRST_ENCRYPTED_BLOCK_BYTE_COUNT = 9074 * 4;

    private final static int SECOND_ENCRYPTED_BLOCK_BYTE_COUNT = 174376 * 4;

    private final static int SECOND_ENCRYPTED_BLOCK_OFFSET = 40;

    private final static int MAKER_NOTE_BYTE_COUNT = SECOND_ENCRYPTED_BLOCK_BYTE_COUNT + SECOND_ENCRYPTED_BLOCK_OFFSET;

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        int makerNoteKey = getMakerNoteKey(iis);
        //
        // The first IFD (SRF0) is in plaintext.
        //
        SonySRF0 srf0 = new SonySRF0();
        setSRF(0, srf0);
        logger.fine("Loading plaintext SRF0 AT " + offset);
        long nextOffset = srf0.load(iis, offset);
        //
        // Follows a block of encrypted data. Part of this block is encrypted twice,
        // so unfortunately we cannot decrypt it with the simple use of a SonyDecypher.
        // We have to load it entirely in memory and process it.
        //
        byte[] buffer = new byte[MAKER_NOTE_BYTE_COUNT];
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
        SonySRF1 srf1 = new SonySRF1();
        setSRF(1, srf1);
        logger.fine("Loading encrypted SRF1 AT " + nextOffset);
        long srf1Offset = nextOffset;
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
                Class srfClass = Class.forName("it.tidalwave.imageio.srf.SonySRF" + srfIndex);
                srf = (Directory)srfClass.newInstance();
              }
            catch (Exception e)
              {
                throw new IOException(e.toString());
              }

            setSRF(srfIndex, srf);
            logger.fine("Loading encrypted SRF" + srfIndex + " AT " + nextOffset);
            nextOffset = srf.load(iis, nextOffset);
          }

        ((SRFImageInputStream)iis).stopEncryptedSection();
      }

    private void setSRF (int i,
                        Directory srf)
      {
        this.addNamedDirectory("SRF" + i, srf);
      }

    public SonySRF0 getSRF0 ()
      {
        return (SonySRF0)getNamedDirectory("SRF0");
      }

    public SonySRF1 getSRF1 ()
      {
        return (SonySRF1)getNamedDirectory("SRF1");
      }

    public SonySRF2 getSRF2 ()
      {
        return (SonySRF2)getNamedDirectory("SRF2");
      }

    public String getColorMode ()
      {
        // TODO Auto-generated method stub
        return "UNKNOWN";
      }

    public String getToneCompensation ()
      {
        // TODO Auto-generated method stub
        return "UNKNOWN";
      }

    public boolean isCompressionDataAvailable ()
      {
        // TODO Auto-generated method stub
        return false;
      }

    public int[] getLinearizationTable ()
      {
        // TODO Auto-generated method stub
        return null;
      }

    public byte[] getCurve ()
      {
        // TODO Auto-generated method stub
        return null;
      }

    public String getWhiteBalance ()
      {
        // TODO Auto-generated method stub
        return "SUNNY";
      }

    public boolean isWhiteBalanceFineAvailable ()
      {
        // TODO Auto-generated method stub
        return false;
      }

    public int getWhiteBalanceFine ()
      {
        return 0;
      }

    public TagRational[] getWhiteBalanceRBCoefficients ()
      {
        TagRational ONE = new TagRational(1, 1);
        return new TagRational[] { ONE, ONE };
      }
    
    /*******************************************************************************
     * 
     * Retrieves the makerNote decryption key.
     * 
     * @param  iis          the image input stream
     * @return              the key
     * @throws IOException
     * 
     *******************************************************************************/
    private int getMakerNoteKey (RAWImageInputStream iis) throws IOException
      {
        iis.seek(MAKER_NOTE_KEY_OFFSET); 
        int skip = iis.readByte() & 0xff;
        // System.err.println("BBB is " + bbb);
        iis.skipBytes(skip * 4 - 1);
        ByteOrder byteOrderSave = iis.getByteOrder();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);
        // System.err.println("Reading key at: " + iis.getStreamPosition());
        int ifdKey = iis.readInt();
        iis.setByteOrder(byteOrderSave);
        
        return ifdKey;
      }
  }
