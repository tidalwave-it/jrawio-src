/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003-2011 by Tidalwave s.a.s.
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
package it.tidalwave.imageio.example.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import org.apache.commons.io.DirectoryWalker;
import it.tidalwave.imageio.nef.NEFMetadata;
import it.tidalwave.imageio.raw.TagRational;
import it.tidalwave.imageio.tiff.IFD;

/***********************************************************************************************************************
 *
 * @author Moritz Petersen
 * @version $Id$
 *
 **********************************************************************************************************************/
public class FocalLengthStats
  {
    public static void main (final String[] args)
      {
        try
          {
            final PrintWriter out = new PrintWriter(new File(args[1]));
            new DirectoryWalker()
              {
                @Override
                protected void handleFile (final File file, final int depth, final Collection results)
                  throws IOException
                  {
                    if (file.getName().toUpperCase().endsWith(".NEF"))
                      {
                        System.out.printf("Processing %s...\n", file.getCanonicalPath());
                        final ImageReader reader = (ImageReader) ImageIO.getImageReaders(file).next();
                        reader.setInput(ImageIO.createImageInputStream(file));
                        final IIOMetadata metadata = reader.getImageMetadata(0);
                        final NEFMetadata nefMetadata = (NEFMetadata) metadata;
                        final IFD exifIFD = nefMetadata.getExifIFD();
                        final TagRational focalLength = exifIFD.getFocalLength();
                        out.println(focalLength.doubleValue());
                      }
                  }

                public void start() 
                  throws IOException
                  {
                    super.walk(new File(args[0]), new ArrayList<Object>());
                  }
              }.start();

            out.flush();
            out.close();
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }
  }
