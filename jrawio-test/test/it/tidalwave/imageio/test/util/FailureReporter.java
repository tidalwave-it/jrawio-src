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
 * $Id: FailureReporter.java,v 1.1 2006/02/14 22:19:17 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.test.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FailureReporter
  {
    private Set failureList = new TreeSet();
    
    private Set specialTests = new TreeSet();
    
    private String name;
    
    public FailureReporter (String[] specialTests, String name)
      {
        this.name = name;
        this.specialTests.addAll(Arrays.asList(specialTests));
        
        if (new File(name).exists())
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
                    
                    failureList.add(f);
                  }
                
                br.close();
              }
            
            catch (IOException e)
              {
                throw new RuntimeException(e);
              }
          }
      }
    
    public void write() 
      {
        try
          {
            PrintWriter pw = new PrintWriter(new FileWriter(name));
            
            for (Iterator i = failureList.iterator(); i.hasNext(); )
              {
                pw.println(i.next());
              }
            
            pw.close();
          }
        
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }
    
    public void addFailedTest (String test, String fileName)
      {
        failureList.add(test + ": " + fileName);
        write();
      }
    
    public boolean isFailedTest (String test, String fileName)
      {
        return failureList.contains(test + ": " + fileName) || specialTests.contains(fileName);
      }

    public void addPassedTest (String test, String fileName)
      {
        failureList.remove(test + ": " + fileName);
        write();
      }
  }
