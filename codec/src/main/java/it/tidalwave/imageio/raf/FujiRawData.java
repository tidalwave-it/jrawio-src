/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tidalwave.imageio.raf;

import it.tidalwave.imageio.io.RAWImageInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 *
 * @author fritz
 */
public class FujiRawData
  {
    private String header;
    private byte[] b1 = new byte[16];
    private String version;
    private byte[] b2 = new byte[16];
    private int jpegImageOffset;
    private int jpegImageLength;
    private int table1Offset;
    private int table1Length;
    private int cfaOffset;
    private int cfaLength;
    private int unused1;
    private int unused2;
    private int unused3;
    private int unused4;
    private int unused5;
    private int unused6;
    private int unused7;
    private int unused8;
    private int unused9;
    private int unused10;

    private FujiTable1 fujiTable1;

    public int getBaseOffset()
      {
        return jpegImageOffset + 12;
      }

    public void load (final RAWImageInputStream iis, final int i, final ByteOrder byteOrder)
      throws IOException
      {
        final byte[] buffer1 = new byte[48];
        iis.readFully(buffer1);
        header = new String(buffer1);
        iis.readFully(b1);
        final byte[] buffer2 = new byte[4];
        iis.readFully(buffer2);
        version = new String(buffer2);
        iis.readFully(b2);
        jpegImageOffset = iis.readInt();
        jpegImageLength = iis.readInt();
        table1Offset = iis.readInt();
        table1Length = iis.readInt();
        cfaOffset = iis.readInt();
        cfaLength = iis.readInt();
        unused1 = iis.readInt();
        unused2 = iis.readInt();
        unused3 = iis.readInt();
        unused4 = iis.readInt();
        unused5 = iis.readInt();
        unused6 = iis.readInt();
        unused7 = iis.readInt();
        unused8 = iis.readInt();
        unused9 = iis.readInt();
        unused10 = iis.readInt();

        if (table1Offset > 0)
          {
            fujiTable1 = new FujiTable1();
            final long baseOffsetSave = iis.getBaseOffset();
            iis.setBaseOffset(0);
            fujiTable1.load(iis, table1Offset, table1Length);
            iis.setBaseOffset(baseOffsetSave);
          }
      }

    public FujiTable1 getFujiTable1()
      {
        return fujiTable1;
      }

    public byte[] getB1()
      {
        return b1;
      }

    public byte[] getB2()
      {
        return b2;
      }

    public int getCFALength()
      {
        return cfaLength;
      }

    public int getCFAOffset()
      {
        return cfaOffset;
      }

    public String getHeader()
      {
        return header;
      }

    public int getJPEGImageLength()
      {
        return jpegImageLength;
      }

    public int getJPEGImageOffset()
      {
        return jpegImageOffset;
      }

    public int getTable1Length()
      {
        return table1Length;
      }

    public int getTable1Offset()
      {
        return table1Offset;
      }

    public int getUnused1()
      {
        return unused1;
      }

    public int getUnused10()
      {
        return unused10;
      }

    public int getUnused2()
      {
        return unused2;
      }

    public int getUnused3()
      {
        return unused3;
      }

    public int getUnused4()
      {
        return unused4;
      }

    public int getUnused5()
      {
        return unused5;
      }

    public int getUnused6()
      {
        return unused6;
      }

    public int getUnused7()
      {
        return unused7;
      }

    public int getUnused8()
      {
        return unused8;
      }

    public int getUnused9()
      {
        return unused9;
      }

    public String getVersion()
      {
        return version;
      }
  }
