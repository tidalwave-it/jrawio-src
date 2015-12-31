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
package it.tidalwave.imageio.io;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.stream.FileImageInputStream;

/***********************************************************************************************************************
 *
 * The purpose of this class is to provide a specialized FileImageInputStream which is able to tell which is the input 
 * file that we are using. This is needed by SPIs that handle formats in which there are multiple files (e.g. Canon CRW
 * which is composed of a .CRW file and a .THM file).
 * 
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class FileImageInputStream2 extends FileImageInputStream
  {
    @Nonnull
    private final File file;

    /*******************************************************************************************************************
     * 
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     * 
     ******************************************************************************************************************/
    public FileImageInputStream2 (final @Nonnull File file)
      throws FileNotFoundException, IOException
      {
        super(file);
        this.file = file;
      }

    /*******************************************************************************************************************
     * 
     * Returns the associated file
     * 
     * @return  the associated file
     * 
     ******************************************************************************************************************/
    @Nonnull
    public File getFile()
      {
        return file;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public String toString()
      {
        return String.format("FileImageInputStream2@%06x", System.identityHashCode(this));
      }
  }
