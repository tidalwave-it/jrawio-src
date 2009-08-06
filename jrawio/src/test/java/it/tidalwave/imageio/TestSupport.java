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
package it.tidalwave.imageio;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.File;
import java.io.InputStream;
import it.tidalwave.util.logging.SingleLineLogFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class TestSupport 
  {
    @BeforeClass
    public static void setupLogging()
      throws Exception
      {
        try 
          {
            new File("target/logs").mkdirs();
            final InputStream is = TestSupport.class.getResourceAsStream("log.properties");
            LogManager.getLogManager().readConfiguration(is);
            is.close();
            
            //
            // The formatter must be set programmatically as the property in the log.properties won't
            // be honored. I suspect it is related with NetBeans module classloaders as the formatter
            // is loaded inside LogManager by using the SystemClassLoader, which only sees the classpath.
            //
            final SingleLineLogFormatter formatter = new SingleLineLogFormatter();
            Logger rootLogger = Logger.getLogger(TestSupport.class.getName());
            
            while (rootLogger.getParent() != null)
              {
                rootLogger = rootLogger.getParent();  
              }

            for (final Handler handler : rootLogger.getHandlers())
              {
                handler.setFormatter(formatter);
              }
          } 
        catch (Exception e) 
          {
            e.printStackTrace();
          }
      }
//
//    @Test(timeout=60000)
//    public void dummy() // FIXME: drop it, make this class ignored
//      {
//      }
  }
