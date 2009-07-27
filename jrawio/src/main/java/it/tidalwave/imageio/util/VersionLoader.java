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
 * $Id: VersionLoader.java 263 2009-06-15 09:18:57Z fabriziogiudici $
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.util;

import javax.annotation.Nonnull;
import java.util.Properties;
import it.tidalwave.imageio.raw.Version;
import java.io.InputStream;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: VersionLoader.java 263 2009-06-15 09:18:57Z fabriziogiudici $
 *
 **********************************************************************************************************************/
public final class VersionLoader
  {
    private VersionLoader()
      {
      }

    public static String getVersion (final @Nonnull Class<?> clazz)
      {
        try
          {
            final InputStream is = Version.class.getResourceAsStream("version.properties");
            final Properties properties = new Properties();
            properties.load(is);
            is.close();
            return  properties.getProperty("version");
          }
        catch (Exception e)
          {
            System.err.printf("SEVERE: Can't read version number for: %s because of %s \n", clazz, e);
            return "unknown";
          }
      }
  }
