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
package it.tidalwave.imageio.example.example1;

import javax.annotation.Nonnull;
import java.util.logging.LogManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.net.URL;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class Main
  {
    public static void main (final @Nonnull String ... args)
      throws IOException
      {
        final InputStream is = Main.class.getResourceAsStream("log.properties");
        LogManager.getLogManager().readConfiguration(is);
        is.close();
        
        final URL url = new URL("http://www.rawsamples.ch/raws/nikon/d3/RAW_NIKON_D3.NEF");
        final BufferedImage image = ImageIO.read(url);
        ImageIO.write(image, "jpg", new File("target/RAW_NIKON_D3.jpg"));
      }
  }
