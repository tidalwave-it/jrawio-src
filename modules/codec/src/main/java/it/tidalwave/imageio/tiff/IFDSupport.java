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
package it.tidalwave.imageio.tiff;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.TagRegistry;
import it.tidalwave.imageio.io.RAWImageInputStream;

/***********************************************************************************************************************
 *
 * This class provides the capability of loading an IFD.
 * 
 * @author Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class IFDSupport extends Directory
  {
    private final static String CLASS = IFDSupport.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    private final static long serialVersionUID = -8252917582886315978L;

    /** After this number of invalig tags it will give up loading further tags. */
    public final static int CONSECUTIVE_INVALID_TAG_THRESHOLD = 5;

    /*******************************************************************************************************************
     *
     * For de-serialization and for supporting CGLIB only. CGLIB is needed by
     * blueMarine. For this reason it must be public too.
     *
     ******************************************************************************************************************/
    public IFDSupport()
      {
      }

    /*******************************************************************************************************************
     * 
     * Creates a new <code>IFD</code> whose tags belong to the given registry.
     * 
     * @param tagRegistry  the registry
     * 
     ******************************************************************************************************************/
    protected IFDSupport (@Nonnull final TagRegistry tagRegistry)
      {
        super(tagRegistry);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    public long load (@Nonnull final RAWImageInputStream iis, long offset)
      throws IOException
      {
        logger.finer(">>>> Reading IFD at offset: %d + %d", offset, iis.getBaseOffset());
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
            logger.warning("Ignoring invalid ifdOffset: %d", offset);
            return 0;
          }

        logger.finest(">>>> entryCount: %d", entryCount);

        int consecutiveInvalidTagCount = 0;
        boolean giveUp = false;
        
        for (int i = 0; (i < entryCount) && !giveUp; i++)
          {
            final int ifdTag = iis.readUnsignedShort();
            final TIFFTag tag = new TIFFTag(tagRegistry, ifdTag);
            tag.read(iis);
            addTag(tag);

            if (tag.isValid())
              {
                consecutiveInvalidTagCount = 0;
              }
            else if (++consecutiveInvalidTagCount >= CONSECUTIVE_INVALID_TAG_THRESHOLD)
              {
                logger.warning("Too many consecutive invalid tags (%d), giving up", CONSECUTIVE_INVALID_TAG_THRESHOLD);
                giveUp = true;
                removeAllTags();
              }
          }

        offset = giveUp ? 0 : iis.readUnsignedInt();
        end = iis.getStreamPosition() + iis.getBaseOffset() - 1;
        logger.finest(">>>> next ifdOffset: %d", offset);
        logger.finest(">>>> loaded: %s", this);
 
        return offset;
      }
  }
