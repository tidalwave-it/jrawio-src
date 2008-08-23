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
 * $Id: IFDSupport.java 69 2008-08-23 15:09:09Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.logging.Logger;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.TagRegistry;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * This class provides the capability of loading an IFD.
 * 
 * @author Fabrizio Giudici
 * @version $Id: IFDSupport.java 69 2008-08-23 15:09:09Z fabriziogiudici $
 *
 ******************************************************************************/
public class IFDSupport extends Directory
  {
    private final static String CLASS = IFDSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = -8252917582886315978L;

    /***************************************************************************
     * 
     * 
     **************************************************************************/
    protected IFDSupport()
      {
      }

    /***************************************************************************
     * 
     * Creates a new <code>IFD</code> whose tags belong to the given registry.
     * 
     * @param tagRegistry  the registry
     * 
     **************************************************************************/
    protected IFDSupport (@Nonnull final TagRegistry tagRegistry)
      {
        super(tagRegistry);
      }

    /***************************************************************************
     * 
     * {@inheritDoc}
     * 
     **************************************************************************/
    public long load (@Nonnull final RAWImageInputStream iis, long offset)
      throws IOException
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
        // Some NEF photos (e.g. 20051208-0005.NEF, Nikon_D70s_0001.NEF) have an 
        // invalid ifdOffset at the end of an IFD. Should investigate, since this 
        // fix is not enough: what if the ifdOffset is invalid but lies within 
        // the file size range?
        //
//        catch (IllegalArgumentException e)
//          {
//            logger.warning("Ignoring invalid ifdOffset: " + offset);
//            return 0;
//          }
//        catch (EOFException e)
        catch (Exception e)
          {
            logger.warning("Ignoring invalid ifdOffset: " + offset);
            return 0;
          }

        logger.finest(">>>> entryCount: " + entryCount);

        for (int i = 0; i < entryCount; i++)
          {
            final int ifdTag = iis.readUnsignedShort();
            final TIFFTag tag = new TIFFTag(tagRegistry, ifdTag);
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
