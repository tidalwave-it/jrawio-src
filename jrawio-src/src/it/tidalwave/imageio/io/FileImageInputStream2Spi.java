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
 * $Id: FileImageInputStream2Spi.java,v 1.3 2006/02/12 22:25:53 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.io;

import java.util.Iterator;
import java.util.Locale;
import java.io.File;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.raw.Version;

/*******************************************************************************
 *
 * @author  fritz
 * @version CVS $Id: FileImageInputStream2Spi.java,v 1.3 2006/02/12 22:25:53 fabriziogiudici Exp $
 *
 ******************************************************************************/
public class FileImageInputStream2Spi extends ImageInputStreamSpi 
  {
    private static final String vendorName = "www.tidalwave.it";

    private static final String version = Version.BUILD;

    private static final Class inputClass = File.class;

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public FileImageInputStream2Spi()
      {
        super(vendorName, version, inputClass);
      }

    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     *******************************************************************************/
    public String getDescription (Locale locale)
      {
        return "Service provider that wraps a FileImageInputStream";
      }

    /*******************************************************************************
     * 
     * Upon registration, this method ensures that this SPI is listed at the top
     * of the ImageInputStreamSpi items, so that it will be invoked before the
     * default FileImageInputStreamSpi
     * 
     * @param registry  the registry
     * @param category  the registration category
     * 
     *******************************************************************************/
    public void onRegistration (ServiceRegistry registry, Class category)
      {
        super.onRegistration(registry, category);
        
        for (Iterator i = registry.getServiceProviders(ImageInputStreamSpi.class, true); i.hasNext();)
          {
            Object other = i.next();

            if (this != other)
              {
                registry.setOrdering(ImageInputStreamSpi.class, this, other);
              }
          }
      }

    /*******************************************************************************
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
