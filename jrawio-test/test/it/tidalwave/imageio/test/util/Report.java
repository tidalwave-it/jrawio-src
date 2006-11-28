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
 * $Id: Report.java,v 1.1 2006/02/14 22:19:16 fabriziogiudici Exp $
 *  
 ******************************************************************************/
package it.tidalwave.imageio.test.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Report
  {
    private Map makerToFormatMap = new TreeMap();

    /*******************************************************************************
     * 
     * @param maker
     * @param format
     * @param cameraModel
     * 
     *******************************************************************************/
    public void addFormatAndCameraModel (String maker,
                                          String format,
                                          String cameraModel)
      {
        maker = maker.trim();
        maker = maker.substring(0, 1).toUpperCase() + maker.substring(1).toLowerCase();

        if (maker.equals("Nikon corporation"))
          {
            maker = "Nikon";
          }

        cameraModel = cameraModel.trim();

        Map formatToCameraSetMap = (Map)makerToFormatMap.get(maker);

        if (formatToCameraSetMap == null)
          {
            formatToCameraSetMap = new TreeMap();
            makerToFormatMap.put(maker, formatToCameraSetMap);
          }

        Set cameraSet = (Set)formatToCameraSetMap.get(format);

        if (cameraSet == null)
          {
            cameraSet = new TreeSet();
            formatToCameraSetMap.put(format, cameraSet);
          }

        cameraSet.add(cameraModel);
      }

    /*******************************************************************************
     * 
     * 
     *******************************************************************************/
    public void write ()
      {
        try
          {
            PrintWriter bw = new PrintWriter(new FileWriter("Supported.html"));
            bw.println("<table border='1' cellpadding='2' cellspacing='0'>");

            bw.println("<tr><th>Maker</th><th>Format</th><th>Model</th></tr>");

            for (Iterator i = makerToFormatMap.keySet().iterator(); i.hasNext(); )
              {
                String maker = (String)i.next();
                
                Map formatToCameraSetMap = (Map)makerToFormatMap.get(maker);

                for (Iterator j = formatToCameraSetMap.keySet().iterator(); j.hasNext(); )
                  {
                    String format = (String)j.next();
                    bw.println("<tr><td>" + maker + "</td><td>" + format + "</td><td>");
                    Set cameraSet = (Set)formatToCameraSetMap.get(format);

                    for (Iterator k = cameraSet.iterator(); k.hasNext(); )
                      {
                        String model = (String)k.next();
                        bw.println(model + ", ");
                      }

                    bw.println("</td></tr>");
                  }
              }

            bw.println("</table>");
            bw.close();
          }

        catch (IOException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      }

  }
