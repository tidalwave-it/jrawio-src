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
 * $Id: TestContext.java,v 1.1 2006/02/14 22:19:21 fabriziogiudici Exp $
 *  
 ******************************************************************************/
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
    
    /*******************************************************************************
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
