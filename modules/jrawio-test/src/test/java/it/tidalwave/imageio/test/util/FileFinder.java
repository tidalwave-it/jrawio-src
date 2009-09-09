/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.io.File;
import java.io.FileFilter;

public class FileFinder
  {
    public static Collection findTestFiles (File folder,
                                            final String extension)
      {
        Collection result = new ArrayList();

        File[] files = folder.listFiles(new FileFilter()
          {
            public boolean accept (File file)
              {
                return !file.getName().startsWith(".") && file.getName().toLowerCase().endsWith("." + extension.toLowerCase());
              }
          });

        result.addAll(Arrays.asList(files));

        File[] subFolders = folder.listFiles(new FileFilter()
          {
            public boolean accept (File file)
              {
                return file.isDirectory() && (file.getAbsolutePath().indexOf("blueMarine/Thumbnails") < 0);
              }
          });

        for (int i = 0; i < subFolders.length; i++)
          {
            result.addAll(findTestFiles(subFolders[i], extension));
          }

        return result;
      }
  }
