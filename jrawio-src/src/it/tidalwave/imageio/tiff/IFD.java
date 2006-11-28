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
 * $Id: IFD.java,v 1.2 2006/02/08 20:26:54 fabriziogiudici Exp $
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
 * @version CVS $Id: IFD.java,v 1.2 2006/02/08 20:26:54 fabriziogiudici Exp $
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
