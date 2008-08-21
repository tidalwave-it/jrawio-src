/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: PropertyTracker.java 55 2008-08-21 19:43:51Z fabriziogiudici $
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
