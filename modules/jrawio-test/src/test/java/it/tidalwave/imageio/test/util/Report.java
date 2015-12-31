/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * Copyright (C) 2003 - 2016 by Tidalwave s.a.s.
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://jrawio.rawdarkroom.org
 * SCM: https://kenai.com/hg/jrawio~src
 *
 **********************************************************************************************************************/
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

    /*******************************************************************************************************************
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

    /*******************************************************************************************************************
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
