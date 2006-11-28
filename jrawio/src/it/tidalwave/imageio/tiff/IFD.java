/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: IFD.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.tiff;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.util.logging.Logger;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: IFD.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
public class IFD extends IFDGenerated
  {
    private final static String CLASS = "it.tidalwave.imageio.tiff.IFD";

    private final static Logger logger = Logger.getLogger(CLASS);

    private final static long serialVersionUID = -4451964246159369585L;

    public final static String EXIF_NAME = "EXIF";

    public final static String MAKER_NOTE_NAME = "MakerNote";

    public final static String INTEROPERABILITY_NAME = "INTEROPERABILITY";

    public final static String GPS_NAME = "GPS";

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void loadAll (RAWImageInputStream iis, long offset) throws IOException
      {
        logger.fine("loadAll(iis=" + iis + ", offset=" + offset + ")");
        IFD currentIFD = this;
        
        while (offset > 0)
          {
            offset = currentIFD.load(iis, offset);
            currentIFD.loadSubDirectories(iis);
            
            if (offset > 0)
              {
                currentIFD.nextDirectory = new IFD();
                currentIFD = (IFD)currentIFD.nextDirectory;
              }
          }
        
        logger.fine(">>>> loadAll() completed ok");
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private void loadSubDirectories (RAWImageInputStream iis) throws IOException 
      {
        int[] subIFDOffsetsList = getSubIFDs();

        if (subIFDOffsetsList != null)
          {
            for (int i = 0; i < subIFDOffsetsList.length; i++)
              {
                logger.finer(">>>> Processing subIFD #" + i);
                IFD subIFD = new IFD();
                addDirectory(subIFD);
                subIFD.loadAll(iis, subIFDOffsetsList[i]);
                logger.finer(">>>>>>>> Completed processing subIFD #" + i);
              }
          }
      }
    
    /*******************************************************************************
     * 
     * Returns the 'AsShotICCProfile' as an {@link java.awt.color.ICC_Profile}.
     * 
     * @return  the profile
     * 
     *******************************************************************************/
    public ICC_Profile getAsShotICCProfile2 ()
      {
        return getProfile(getAsShotICCProfile());
      }

    /*******************************************************************************
     * 
     * Returns the 'CurrentICCProfile' as an {@link java.awt.color.ICC_Profile}.
     * 
     * @return  the profile
     * 
     *******************************************************************************/
    public ICC_Profile getCurrentICCProfile2 ()
      {
        return getProfile(getCurrentICCProfile());
      }

    /*******************************************************************************
     * 
     * Returns the maker note offset.
     * 
     * @return  the maker note offset
     * 
     *******************************************************************************/
    public int getMakerNoteOffset ()
      {
        return getMakerNote();
      }

    /*******************************************************************************
     * 
     * Turns an array of bytes into an ICC_Profile.
     * 
     * @param  the bytes
     * @return the profile
     * 
     *******************************************************************************/
    private static ICC_Profile getProfile (byte[] bytes)
      {
        return (bytes == null) ? null : ICC_Profile.getInstance(bytes);
      }
  }
