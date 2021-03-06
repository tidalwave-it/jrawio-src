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
package it.tidalwave.imageio.crw;

import it.tidalwave.imageio.util.Logger;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.io.FileImageInputStream2;
import it.tidalwave.imageio.io.RAWImageInputStreamImpl;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWImageInputStream extends RAWImageInputStreamImpl
  {
    private final static String CLASS = CRWImageInputStream.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    private final static String[] THM_EXTENSIONS = { "THM", "Thm", "thm" };

    private ImageInputStream crwInputStream;

    private ImageInputStream thmInputStream;

    /*******************************************************************************************************************
     * 
     * @param delegate
     * @throws IOException 
     *      
     *******************************************************************************/
    public CRWImageInputStream (ImageInputStream delegate) 
      throws IOException
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
                logger.fine("THM file is %s", thmFile);
                thmInputStream = new FileImageInputStream(thmFile);
              }
          }

        else
          {
            logger.warning("delegate is " + delegate + ", won't be able to manage .THM file");
          }
      }

    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public void switchToCRWStream ()
      {
        super.delegate = crwInputStream;
      }

    /*******************************************************************************************************************
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

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    @Override
    public void close() 
      throws IOException
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
