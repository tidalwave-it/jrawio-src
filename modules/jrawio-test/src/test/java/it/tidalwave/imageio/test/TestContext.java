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
package it.tidalwave.imageio.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.LogManager;
import it.tidalwave.imageio.test.util.FailureReporter;
import it.tidalwave.imageio.test.util.PixelSpeed;
import it.tidalwave.imageio.test.util.PropertyTracker;
import it.tidalwave.imageio.test.util.Report;
import java.net.UnknownHostException;

/**
 *
 * @author fritz
 */
public class TestContext
  {
    private final static String[] specialTests = 
      {
        "/From Individuals/www.dpmagic.com/DSC_3616.NEF",
        "/From the Web/rrboc.jp/files/DSC_0208.NEF",
        "/From the Web/www.imaging-resource.com/PRODS/D1X/D1XFARLU_COMP.NEF"
      };

    public static File testFolder = new File(System.getenv("TEST_PHOTOS"));

    protected PixelSpeed pixelSpeed;
         
    protected String dateTag;

    protected String computerTag;

    protected String osTag;
    
    protected Report report = new Report();
    
    protected Map speedMapByFormat = new HashMap();
    
    protected FailureReporter failure = new FailureReporter(specialTests, "failures");

    protected PropertyTracker propertyTracker = new PropertyTracker(testFolder);
    
    /** Creates a new instance of TestFinalizer */
    public TestContext()
      {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmm");
        dateTag = dateFormat.format(new Date());
        osTag = System.getProperty("os.arch") + "." + System.getProperty("os.name") + " "
              + System.getProperty("os.version");
        try
          {
            computerTag = InetAddress.getLocalHost().getHostName();
          }  
        catch (UnknownHostException ex)
          {
            computerTag = "UNKNOWN";
          }

        try
          {
            configureLogging();
          }
        catch (Exception e)
          {
          }
        
        Runtime.getRuntime().addShutdownHook(new Thread()
          {
            public void run()
              {
                sync();  
              }
          });
      }
    
    /*******************************************************************************************************************
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * 
     *******************************************************************************/
    private static void configureLogging () throws FileNotFoundException, IOException
      {
        String logConfigFile = "test-log.properties";
        System.err.println("Log configuration file: " + logConfigFile);

        InputStream is = new FileInputStream(new File(logConfigFile));
        LogManager.getLogManager().readConfiguration(is);
        is.close();
      }

    public void sync()
      {
        for (Iterator i = speedMapByFormat.keySet().iterator(); i.hasNext();)
          {
            String format = (String)i.next();
            PixelSpeed pixelSpeed = (PixelSpeed)speedMapByFormat.get(format);
            double speed = 0.1 * Math.round(pixelSpeed.getSpeed() * 10);
            propertyTracker.setProperty(null, "MEGAPIXEL_PER_SECOND." + format + "." + dateTag + "." + computerTag
                + "." + osTag, new Double(speed));
          }

        report.write();
        failure.write();
        propertyTracker.write();
      }
    
  }
