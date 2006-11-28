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
 * $Id: PropertyTracker.java,v 1.1 2006/02/14 22:19:15 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.test.util;

import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;

public class PropertyTracker
  {
    private File testFolder;

    private Properties properties = new Properties();

    private int changes;

    /*******************************************************************************
     * 
     * @param testFolder
     * 
     *******************************************************************************/
    public PropertyTracker (File testFolder)
      {
        this.testFolder = testFolder;

        try
          {
            System.out.println("Reading properties...");
            InputStream os = new BufferedInputStream(new FileInputStream("image.properties"));
            properties.load(os);
            os.close();
            System.out.println("USED MEMORY:   "
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
          }
        catch (IOException e)
          {
          }
      }

    /*******************************************************************************
     * 
     * @param file
     * @param propertyName
     * @param propertyValue
     * 
     *******************************************************************************/
    public void updateProperty (File file,
                                String propertyName,
                                Object propertyValue)
      {
        String fileName = "";

        if (file != null)
          {
            fileName = file.getAbsolutePath();
            String folderName = testFolder.getAbsolutePath();

            if (!fileName.startsWith(folderName))
              {
                throw new IllegalArgumentException("" + fileName + ", " + folderName);
              }

            fileName = fileName.substring(folderName.length());
            propertyName = fileName + "." + propertyName;
          }

        String value = (propertyValue != null) ? propertyValue.toString() : "--null--";
        String oldValue = properties.getProperty(propertyName);

        if (oldValue == null)
          {
            properties.put(propertyName, value);
          }

        else if (!oldValue.equals(value))
          {
            String s = "**** file: " + fileName + " bad property: " + propertyName + " expected value: " + oldValue + " new value:" + value;
            System.err.println(">>>> " + s);
            TestCase.assertFalse(true);
          }
      }

    public void setProperty (File file,
                             String propertyName,
                             Object propertyValue)
      {
        String fileName = "";

        if (file != null)
          {
            fileName = file.getAbsolutePath();
            String folderName = testFolder.getAbsolutePath();

            if (!fileName.startsWith(folderName))
              {
                throw new IllegalArgumentException("" + file);
              }

            fileName = fileName.substring(folderName.length());
            propertyName = fileName + "." + propertyName;
          }

        String value = (propertyValue != null) ? propertyValue.toString() : "--null--";
        properties.put(propertyName, value);
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public void write ()
      {
        try
          {
            System.out.println("Syncing properties...");
            OutputStream os = new BufferedOutputStream(new FileOutputStream("image.properties"));
            properties.store(os, "");
            os.close();
            changes = 0;
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }
  }
