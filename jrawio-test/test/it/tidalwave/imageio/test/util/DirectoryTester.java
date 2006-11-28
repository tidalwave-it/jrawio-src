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
 * $Id: DirectoryTester.java,v 1.1 2006/02/14 22:19:15 fabriziogiudici Exp $
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
