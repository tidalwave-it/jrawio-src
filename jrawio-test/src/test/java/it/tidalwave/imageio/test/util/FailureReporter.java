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
 * $Id: FailureReporter.java 55 2008-08-21 19:43:51Z fabriziogiudici $
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
