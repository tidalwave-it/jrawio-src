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
package it.tidalwave.imageio.io;

import java.util.Iterator;
import java.util.Locale;
import java.io.File;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.craw.Version;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class FileImageInputStream2Spi extends ImageInputStreamSpi 
  {
    private static final String VENDOR_NAME = "www.tidalwave.it";

    private static final String VERSION = Version.TAG;

    private static final Class INPUT_CLASS = File.class;

    /*******************************************************************************************************************
     * 
     * 
     *******************************************************************************/
    public FileImageInputStream2Spi()
      {
        super(VENDOR_NAME, VERSION, INPUT_CLASS);
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Service provider that wraps a FileImageInputStream";
      }

    /*******************************************************************************************************************
     * 
     * Upon registration, this method ensures that this SPI is listed at the top
     * of the ImageInputStreamSpi items, so that it will be invoked before the
     * default FileImageInputStreamSpi
     * 
     * @param registry  the registry
     * @param category  the registration category
     * 
     *******************************************************************************/
    @Override
    public void onRegistration (ServiceRegistry registry, Class category)
      {
        super.onRegistration(registry, category);
        
        for (final Iterator<ImageInputStreamSpi> i = registry.getServiceProviders(ImageInputStreamSpi.class, true); 
             i.hasNext();)
          {
            final ImageInputStreamSpi other = i.next();

            if (this != other)
              {
                registry.setOrdering(ImageInputStreamSpi.class, this, other);
              }
          }
      }

    /*******************************************************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public ImageInputStream createInputStreamInstance (Object input, boolean useCache, File cacheDir)
      {
        if (input instanceof File)
          {
            try
              {
                return new FileImageInputStream2((File)input);
              }
            catch (Exception e)
              {
                return null;
              }
          }

        throw new IllegalArgumentException();
      }
  }
