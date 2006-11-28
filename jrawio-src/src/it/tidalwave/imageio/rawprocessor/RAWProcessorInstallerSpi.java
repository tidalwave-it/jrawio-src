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
 * $Id: RAWProcessorInstallerSpi.java,v 1.2 2006/02/12 22:25:51 fabriziogiudici Exp $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Fabrizio Giudici
 *
 * This is a dummy Spi used only for installing the RAWProcessor stuff. It's a temporary
 * trick to keep the RAWProcessor stuff in a different sub-project until it gets mature.
 *
 */
public class RAWProcessorInstallerSpi extends ImageInputStreamSpi
  {
    public RAWProcessorInstallerSpi()
      {
        super("dummy", "dummy", Float.class);
        install();
      }

    public ImageInputStream createInputStreamInstance (Object object, boolean b, File file) throws IOException
      {
        return null;
      }

    public String getDescription (Locale locale)
      {
        return "dummy";
      }
    
    private void install()
      {
        try
          {
            Class.forName("it.tidalwave.imageio.rawprocessor.RAWProcessorInstaller");
            System.err.println("RAWProcessor succesfully installed");
          }        
        catch (ClassNotFoundException e)
          {
            System.err.println("RAWProcessor not found: " + e);
          }        
        catch (Throwable e)
          {
            System.err.println("Error while running RAWProcessorInstaller: " + e.toString());
          }        
      }
  }
