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
 * $Id: CRWImageInputStream.java,v 1.2 2006/02/08 19:49:01 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.crw;

import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.FileImageInputStream2;
import it.tidalwave.imageio.io.RAWImageInputStream;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: CRWImageInputStream.java,v 1.2 2006/02/08 19:49:01 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class CRWImageInputStream extends RAWImageInputStream
  {
    private final static String CLASS = "it.tidalwave.imageio.crw.CRWImageInputStream";

    private final static Logger logger = Logger.getLogger(CLASS);

    private final static String[] THM_EXTENSIONS = { "THM", "Thm", "thm" };

    private ImageInputStream crwInputStream;

    private ImageInputStream thmInputStream;

    /*******************************************************************************
     * 
     * @param delegate
     * @throws IOException 
     *      
     *******************************************************************************/
    public CRWImageInputStream (ImageInputStream delegate) throws IOException
      {
        super(delegate);
        crwInputStream = delegate;

        if (delegate instanceof FileImageInputStream2)
          {
            File file = ((FileImageInputStream2)delegate).getFile();
            File thmFile = null;
            String path = file.getAbsolutePath();

            int i = path.lastIndexOf('.');

            if (i > 0)
              {
                path = path.substring(0, i + 1);
              }

            for (i = 0; i < THM_EXTENSIONS.length; i++)
              {
                String thmPath = path + THM_EXTENSIONS[i];
                thmFile = new File(thmPath);

                if (thmFile.exists())
                  {
                    break;
                  }
              }

            if (!thmFile.exists())
              {
                logger.warning("File " + thmFile + " does not exist");
              }

            else
              {
                logger.fine("THM file is " + thmFile);
                thmInputStream = new FileImageInputStream(thmFile);
              }
          }

        else
          {
            logger.warning("delegate is " + delegate + ", won't be able to manage .THM file");
          }
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public void switchToCRWStream ()
      {
        super.delegate = crwInputStream;
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public boolean switchToTHMStream ()
      {
        if (thmInputStream != null)
          {
            super.delegate = thmInputStream;
            return true;
          }

        return false;
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public void close () throws IOException
      {
        try
          {
            super.close();
          }

        finally
          {
            if (thmInputStream != null)
              {
                thmInputStream.close(); // TODO: should catch exceptions here?
              }
          }
      }
  }