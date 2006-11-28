/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2006 by Fabrizio Giudici (Fabrizio.Giudici@tidalwave.it)
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
 * $Id: DirectoryTester.java 9 2006-11-28 12:43:27Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.test.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class DirectoryTester
  {
    private List attributeList = new ArrayList();

    public DirectoryTester (String name)
      {
        try
          {
            BufferedReader br = new BufferedReader(new FileReader(name));

            for (;;)
              {
                String f = br.readLine();

                if (f == null)
                  {
                    break;
                  }

                attributeList.add(f);
              }

            br.close();
          }

        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }

    public void test (Object directory,
                      File file,
                      String prefix,
                      PropertyTracker propertyTracker)
      {
        Class clazz = directory.getClass();
        boolean failed = false;
        
        for (Iterator i = attributeList.iterator(); i.hasNext(); )
          {
            String attribute = (String)i.next();
            boolean available;
            Object value;
            
            try
              {
                Method is = clazz.getMethod("is" + attribute + "Available", new Class[0]);
                available = ((Boolean)is.invoke(directory, new Object[0])).booleanValue();
              }
            catch (Exception e)
              {
                throw new RuntimeException(e);
              }
            
            if (!available)
              {
                try
                  {
                    propertyTracker.updateProperty(file, prefix + "." + attribute, "@@@@ n.a.");
                  }
                catch (RuntimeException e)
                  {
                    failed = true;
                  }
              }

            else
              {
                try
                  {
                    Method getter = clazz.getMethod("get" + attribute, new Class[0]);
                    value = getter.invoke(directory, new Object[0]);
                  }
                catch (Exception e)
                  {
                    System.out.println("**** get" + attribute + "() failed on " + file);
                    failed = true;
                    value = "@@@@ FAILED!";
                    
                    e.printStackTrace();
                  }
                
                try
                  {
                    propertyTracker.updateProperty(file, prefix + "." + attribute, toString(value));
                  }
                catch (RuntimeException e)
                  {
                    failed = true;
                  }
              }
          }
        
        if (failed)
          {
            throw new RuntimeException("failed");
          }
      }
    
    private Object toString (Object value)
      {
        if (value == null)
          {
            return "@@@@ NULL";
          }
        
        if (value instanceof byte[])
          {
            return toString((byte[])value);
          }
        
        if (value instanceof short[])
          {
            return toString((short[])value);
          }
        
        if (value instanceof int[])
          {
            return toString((int[])value);
          }
        
        if (value instanceof Object[])
          {
            return toString((Object[])value);
          }
        
        return value.toString();
      }

    /*******************************************************************************
     * 
     * @param array
     * @return
     * 
     *******************************************************************************/
    public String toString (byte[] array)
      {
        if (array == null)
          {
            return "null";
          }

        if (array.length > 64)
          {
            return "" + array.length + " bytes";
          }

        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toHexString(array[i] & 0xff));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * @param array
     * @return
     * 
     *******************************************************************************/
    public String toString (int[] array)
      {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Integer.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * @param array
     * @return
     * 
     *******************************************************************************/
    public String toString (double[] array)
      {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(Double.toString(array[i]));
          }

        return buffer.toString();
      }

    /*******************************************************************************
     * 
     * @param array
     * @return
     * 
     *******************************************************************************/
    public String toString (Object[] array)
      {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            if (i > 0)
              {
                buffer.append(",");
              }

            buffer.append(array[i].toString());
          }

        return buffer.toString();
      }

  }
