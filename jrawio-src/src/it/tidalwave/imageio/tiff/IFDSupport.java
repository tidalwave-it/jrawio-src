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
 * $Id: IFDSupport.java,v 1.3 2006/02/12 19:45:34 fabriziogiudici Exp $
 * 
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Logger;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.TagRegistry;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author Fabrizio Giudici
 * @version CVS $Id: IFDSupport.java,v 1.3 2006/02/12 19:45:34 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class IFDSupport extends Directory
  {
    private final static long serialVersionUID = -8252917582886315978L;

    private final static String CLASS = "it.tidalwave.imageio.tiff.IFDSupport";

    private final static Logger logger = Logger.getLogger(CLASS);

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    protected IFDSupport()
      {
      }

    /*******************************************************************************
     * 
     * Creates a new <code>IFD</code> whose tags belong to the given registry.
     * 
     * @param tagRegistry  the registry
     * 
     *******************************************************************************/
    protected IFDSupport (TagRegistry tagRegistry)
      {
        super(tagRegistry);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public long load (RAWImageInputStream iis, long offset) throws IOException
      {
        logger.finer(">>>> Reading IFD at offset: " + offset + " + " + iis.getBaseOffset());
        int entryCount;

        try
          {
            iis.seek(offset);
            start = iis.getStreamPosition() + iis.getBaseOffset();
            entryCount = iis.readShort();
          }
        //
        // Some NEF photos (e.g. 20051208-0005.NEF) have an invalid ifdOffset at the
        // end of an IFD. Should investigate, since this fix is not enough: what if
        // the ifdOffset is invalid but lies within the file size range?
        //
        catch (EOFException e)
          {
            logger.warning("Ignoring invalid ifdOffset: " + offset);
            return 0;
          }

        logger.finest(">>>> entryCount: " + entryCount);

        for (int i = 0; i < entryCount; i++)
          {
            int ifdTag = iis.readUnsignedShort();
            TIFFTag tag = new TIFFTag(tagRegistry, ifdTag);
            tag.read(iis);
            addTag(tag);
          }

        offset = iis.readUnsignedInt();
        end = iis.getStreamPosition() + iis.getBaseOffset() - 1;
        logger.finest(">>>> next ifdOffset: " + offset);
        logger.finest(">>>> loaded: " + this);
 
        return offset;
      }
  }